package com.polytech.model.algorithm;

import com.polytech.model.Graph;
import com.polytech.model.Route;
import com.polytech.model.Solution;
import com.polytech.model.Step;
import com.polytech.model.Stop;
import com.polytech.model.exception.StopNotLoadedException;
import com.polytech.model.io.CVRPDataWriter;
import com.polytech.model.io.CVRPFileReader;
import com.polytech.ui.CustomerScope;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CVRPAlgorithm {

    public static final double VEHICLE_CAPACITY = 100;
    public static final String ROUTE_LOADED = "ROUTE_LOADED";

    protected static final Random random = new Random();

    public void runAlgorithm(CustomerScope scope) {
        Graph graph = scope.getGraph()
                .orElseThrow(StopNotLoadedException::new);
        runAlgorithm(graph, scope);
    }

    /**
     * For each filename in the in the list, load it, run the algorithm on it and make a report
     *
     * @param scope the scope we have to notify to update the view
     * @param filenameList the list of files we have to load and execute
     * @throws IOException if there is a problem to read or write a file
     */
    public void runAlgorithm(CustomerScope scope, List<String> filenameList) throws IOException {
        String analyseFilename = CVRPDataWriter.initializeFile();
        for (String fileName : filenameList) {
            Graph graph = CVRPFileReader.loadDataFile(fileName);
            long start = System.nanoTime();
            runAlgorithm(graph, scope);
            long end = System.nanoTime();
            CVRPDataWriter.writeData(analyseFilename, graph, this.getClass().getSimpleName(), fileName, Duration.ofNanos(end - start));
        }
    }

    public abstract void runAlgorithm(Graph graph, CustomerScope scope);

    /**
     * Takes stops randomly and inserts them into a route. When it is full, closes it and inserts into another one
     *
     * @param graph       the graph where stops are loaded and in which we have to set the solution
     * @param fillingRate a percent of maximul filling of each route
     * @return generated solution
     */
    public static Solution randomSolution(Graph graph, double fillingRate) {
        List<Route> randomSolution = new ArrayList<>();

        final Stop depot = graph.getDepot();

        Route currentRoute = new Route(VEHICLE_CAPACITY);
        randomSolution.add(currentRoute);

        final List<Stop> stopList = new ArrayList<>(graph.getStopList());

        while (!stopList.isEmpty()) {
            Stop stop = stopList.get(random.nextInt(stopList.size()));

            if (currentRoute.getQuantity() + stop.getQuantity() <= currentRoute.getCapacity() * fillingRate) {
                currentRoute.addStep(new Step(currentRoute.getLastStop().orElse(depot), stop));
            } else {
                currentRoute.addStep(new Step(currentRoute.getLastStop().orElseThrow(), depot));
                currentRoute = new Route(VEHICLE_CAPACITY);
                currentRoute.addStep(new Step(depot, stop));
                randomSolution.add(currentRoute);
            }
            stopList.remove(stop);
        }

        currentRoute.addStep(new Step(currentRoute.getLastStop().orElseThrow(), depot));

        Solution finalSolution = new Solution(randomSolution);
        graph.setRoutingSolution(finalSolution);
        return finalSolution;
    }

    public static Solution randomSolution(Graph graph) {
        return randomSolution(graph, 1.0);
    }

    /**
     * Inserts in each route the best stop in order to minimize the cost
     *
     * @param graph the graph where stops are loaded and in which we have to set the solution
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
