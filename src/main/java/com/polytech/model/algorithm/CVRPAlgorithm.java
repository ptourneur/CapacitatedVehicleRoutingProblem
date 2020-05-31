package com.polytech.model.algorithm;

import com.polytech.model.Graph;
import com.polytech.model.Route;
import com.polytech.model.Solution;
import com.polytech.model.Step;
import com.polytech.model.Stop;
import com.polytech.model.exception.StopNotLoadedException;
import com.polytech.model.filereader.CVRPFileReader;
import com.polytech.ui.CustomerScope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CVRPAlgorithm {

    public static final double VEHICLE_CAPACITY = 100;
    public static final String ROUTE_LOADED = "ROUTE_LOADED";

    public Solution applyAlgorithm(CustomerScope scope) {
        Graph graph = scope.getGraph()
                .orElseThrow(StopNotLoadedException::new);
        return applyAlgorithm(graph, Optional.of(scope));
    }

    public Solution applyAlgorithm(String filename) throws IOException {
        Graph graph = CVRPFileReader.loadDataFile(filename);
        return applyAlgorithm(graph, Optional.empty());
    }

    public abstract Solution applyAlgorithm(Graph graph, Optional<CustomerScope> scope);

    /**
     * Takes stops and inserts them into a route. When it is full, closes it and inserts into another one
     *
     * @return generated solution
     */
    public static Solution randomSolution(Graph graph) {
        List<Route> randomSolution = new ArrayList<>();

        final Stop depot = graph.getDepot();

        Route currentRoute = new Route(VEHICLE_CAPACITY);
        randomSolution.add(currentRoute);

        for (Stop stop : graph.getStopList()) {
            if (currentRoute.getQuantity() + stop.getQuantity() <= currentRoute.getCapacity()) {
                currentRoute.addStep(new Step(currentRoute.getLastStop().orElse(depot), stop));
            } else {
                currentRoute.addStep(new Step(currentRoute.getLastStop().orElseThrow(), depot));
                currentRoute = new Route(VEHICLE_CAPACITY);
                currentRoute.addStep(new Step(depot, stop));
                randomSolution.add(currentRoute);
            }
        }

        currentRoute.addStep(new Step(currentRoute.getLastStop().orElseThrow(), depot));

        Solution finalSolution = new Solution(randomSolution);
        graph.setRoutingSolution(finalSolution);
        return finalSolution;
    }

    /**
     * Inserts in each route the best stop in order to minimize the cost
     *
     * @return generated solution
     */
    public static Solution greedySolution(Graph graph) {

        List<Route> greedySolution = new ArrayList<>();

        List<Stop> stopList = graph.getStopList();

        Stop depot = graph.getDepot();

        Route currentRoute = new Route(VEHICLE_CAPACITY);
        greedySolution.add(currentRoute);

        while (stopList.stream().map(Stop::isRouted).anyMatch(isRouted -> !isRouted)) {
            double minCost = Double.MAX_VALUE;
            Stop lastStop = currentRoute.getLastStop().orElse(depot);
            Stop bestStop = null;

            for (Stop currentStop : stopList) {
                if (!currentStop.isRouted() &&
                        currentRoute.getQuantity() + currentStop.getQuantity() <= currentRoute.getCapacity() &&
                        currentRoute.getCost() + computeCost(lastStop, currentStop) < minCost) {
                    bestStop = currentStop;
                    minCost = currentRoute.getCost() + computeCost(lastStop, currentStop);
                }
            }

            if (bestStop == null) {
                currentRoute.addStep(new Step(lastStop, depot));
                currentRoute = new Route(VEHICLE_CAPACITY);
                greedySolution.add(currentRoute);
            } else {
                currentRoute.addStep(new Step(lastStop, bestStop));
                bestStop.setRouted(true);
            }
        }
        currentRoute.addStep(new Step(currentRoute.getLastStop().orElseThrow(), depot));

        Solution finalSolution = new Solution(greedySolution);
        graph.setRoutingSolution(finalSolution);

        return finalSolution;
    }

    /**
     * Computes the Euclidean distance between two stops
     *
     * @param initialStop the first stop
     * @param finalStop   the second stop
     * @return the distance between them
     */
    public static double computeCost(Stop initialStop, Stop finalStop) {
        return Math.sqrt(
                Math.pow(finalStop.getX() - initialStop.getX(), 2) +
                        Math.pow(finalStop.getY() - initialStop.getY(), 2)
        );
    }


}
