package com.polytech.model;

import com.polytech.model.exception.NoNeighborException;
import com.polytech.ui.CustomerScope;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public final class CVRP {

    private static final Random random = new Random();

    private static final double VEHICLE_CAPACITY = 100;

    private static final int SIMULATED_ANNEALING_MAX_TEMPERATURE_CHANGE = 500;
    private static final int SIMULATED_ANNEALING_MAX_MOVE_AT_TEMPERATURE = 50;
    private static final double SIMULATED_ANNEALING_DECREASING_LAW = 0.99;

    private static final int TABU_LIST_SIZE = 500;
    private static final int TABU_LIST_MAX_ITERATION = 10000;

    /**
     * Takes stops and inserts them into a route. When it is full, closes it and inserts into another one
     *
     * @return generated solution
     */
    public static Solution randomSolution() {
        List<Route> randomSolution = new ArrayList<>();

        Stop depot = CVRPGraph.getDepot();

        Route currentRoute = new Route(VEHICLE_CAPACITY);
        randomSolution.add(currentRoute);

        for (Stop stop : CVRPGraph.getClientList()) {
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
        CVRPGraph.setRoutingSolution(finalSolution);
        return finalSolution;
    }

    /**
     * Inserts in each route the best stop in order to minimize the cost
     *
     * @return generated solution
     */
    public static Solution greedySolution(CustomerScope scope) {
        List<Route> greedySolution = new ArrayList<>();

        List<Stop> stopList = CVRPGraph.getClientList();

        Stop depot = CVRPGraph.getDepot();

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
        CVRPGraph.setRoutingSolution(finalSolution);
        scope.publish("ROUTE_LOADED");

        return finalSolution;
    }

    /**
     * Simulated annealing implementation
     * - The initial solution is given by {@code randomSolution()}
     * - The initial temperature is given by {@code initializeTemperature()}
     *
     * @param scope the scope we have to notify to update the view
     * @return generated solution
     */
    public static Solution simulatedAnnealing(CustomerScope scope) {

        Solution currentSolution = randomSolution();
        Solution bestSolution = currentSolution;

        scope.totalIteration().setValue(SIMULATED_ANNEALING_MAX_TEMPERATURE_CHANGE);

        Double temperature = null;

        for (int i = 0; i < SIMULATED_ANNEALING_MAX_TEMPERATURE_CHANGE; i++) {
            for (int j = 0; j < SIMULATED_ANNEALING_MAX_MOVE_AT_TEMPERATURE; j++) {

                List<Solution> neighbours = getNeighbors(currentSolution);

                // We initialize the temperature
                if (temperature == null) {
                    temperature = initializeTemperature(currentSolution, neighbours);
                }

                int randomIndex = random.nextInt(neighbours.size());
                Solution selectedNeighbour = neighbours.get(randomIndex);

                if (selectedNeighbour.getFitness() <= currentSolution.getFitness()) {
                    currentSolution = selectedNeighbour;
                    if (selectedNeighbour.getFitness() < bestSolution.getFitness()) {
                        bestSolution = selectedNeighbour;
                    }
                } else {
                    double p = random.nextDouble();
                    if (p <= Math.exp((currentSolution.getFitness() - selectedNeighbour.getFitness()) / temperature)) {
                        currentSolution = selectedNeighbour;
                    }
                }
                CVRPGraph.setRoutingSolution(currentSolution);
                scope.currentIteration().setValue(i+1);
                scope.publish("ROUTE_LOADED");
            }
            temperature = temperature * SIMULATED_ANNEALING_DECREASING_LAW;
        }

        CVRPGraph.setRoutingSolution(bestSolution);
        scope.currentIteration().setValue(0);
        scope.publish("ROUTE_LOADED");
        return bestSolution;
    }

    /**
     * Gets 10 fitness worse that initial solution's fitness and use the average to compute initial temperature
     *
     * @param initialSolution the initial solution
     * @param neighbours      neighbours of the initial solution
     * @return (initial fitness - average) / ln(0.8)
     */
    private static double initializeTemperature(Solution initialSolution, List<Solution> neighbours) {
        List<Double> tenWorseFitnessThanRandomSolution = new ArrayList<>();

        while (tenWorseFitnessThanRandomSolution.size() <= 10) {

            int randomIndex = random.nextInt(neighbours.size());
            Solution selectedNeighbour = neighbours.get(randomIndex);

            if (selectedNeighbour.getFitness() > initialSolution.getFitness()) {
                tenWorseFitnessThanRandomSolution.add(selectedNeighbour.getFitness());
            }
        }
        double worseSolutionAverageFitness = tenWorseFitnessThanRandomSolution.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(initialSolution.getFitness());

        return (initialSolution.getFitness() - worseSolutionAverageFitness) / Math.log(0.8);
    }

    /**
     * Tabu search implementation
     * - The initial solution is given by {@code randomSolution()}
     * - The initial temperature is given by {@code initializeTemperature()}
     *
     * @param scope the scope we have to notify to update the view
     * @return generated solution
     */
    public static Solution tabuSearch(CustomerScope scope) {

        scope.totalIteration().setValue(TABU_LIST_MAX_ITERATION);

        Solution currentSolution = randomSolution();
        Solution bestSolution = currentSolution;

        LinkedList<Double> tabuList = new LinkedList<>();
        tabuList.add(currentSolution.getFitness());

        for (int i = 0; i < TABU_LIST_MAX_ITERATION; i++) {

            Solution bestNeighbor = getNeighbors(currentSolution).stream()
                    .filter(neighbor -> !tabuList.contains(neighbor.getFitness()))
                    .min(Comparator.comparingDouble(Solution::getFitness))
                    .orElseThrow(() -> new NoNeighborException("Can't find any neighbor"));

            if (bestNeighbor.getFitness() >= currentSolution.getFitness()) {
                tabuList.addLast(currentSolution.getFitness());
                if (tabuList.size() > TABU_LIST_SIZE) {
                    tabuList.removeFirst();
                }
            }

            currentSolution = bestNeighbor;
            if (currentSolution.getFitness() < bestSolution.getFitness()) {
                bestSolution = currentSolution;
            }

            CVRPGraph.setRoutingSolution(currentSolution);
            scope.currentIteration().setValue(i+1);
            scope.publish("ROUTE_LOADED");
        }

        CVRPGraph.setRoutingSolution(bestSolution);
        scope.currentIteration().setValue(0);
        scope.publish("ROUTE_LOADED");
        return bestSolution;
    }

    /**
     * Generates neighbors from three elementary transformations available in {@code Solution} : {@code swapTwoStops()},
     * {@code addStopToExistingRoute()} and {@code mergeTwoRoutes()}
     *
     * @param solution the solution from which we will generate the neighbors
     * @return the neighbors in list of solution
     */
    private static List<Solution> getNeighbors(Solution solution) {

        List<Solution> neighbors = new ArrayList<>();

        for (Stop stop : CVRPGraph.getClientList()) {
            for (Route route : solution.getRouteList()) {

                // We add stop to an existing route
                if (!route.containsStop(stop)) {
                    Solution newSolution = new Solution(solution);
                    if (newSolution.addStopToExistingRoute(stop, route)) {
                        neighbors.add(newSolution);
                    }
                }

                for (Stop stop1 : route.getStopList()) {

                    // We swap stops
                    if (!stop.equals(stop1)) {
                        Solution newSolution1 = new Solution(solution);
                        if (newSolution1.swapTwoStops(stop, stop1)) {
                            neighbors.add(newSolution1);
                        }
                    }
                }
            }
        }

        // We merge two routes
        for (Route route : solution.getRouteList()) {
            for (Route route1 : solution.getRouteList()) {
                if (!route.equals(route1)) {
                    Solution newSolution = new Solution(solution);
                    if (newSolution.mergeTwoRoutes(route, route1)) {
                        neighbors.add(newSolution);
                    }
                }
            }
        }

        return neighbors;
    }

    /**
     * Computes the Euclidean distance between two stops
     *
     * @param initialStop the first stop
     * @param finalStop   the second stop
     * @return the distance between them
     */
    static double computeCost(Stop initialStop, Stop finalStop) {
        return Math.sqrt(
                Math.pow(finalStop.getX() - initialStop.getX(), 2) +
                        Math.pow(finalStop.getY() - initialStop.getY(), 2)
        );
    }
}
