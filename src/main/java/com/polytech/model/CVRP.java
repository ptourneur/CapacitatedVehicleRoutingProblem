package com.polytech.model;

import de.saxsys.mvvmfx.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class CVRP {

    private static final Random random = new Random();

    private static final double VEHICLE_CAPACITY = 100;

    private static final double SIMULATED_ANNEALING_MAX_TEMPERATURE_CHANGE = 500;
    private static final double SIMULATED_ANNEALING_MAX_MOVE_AT_TEMPERATURE = 50;
    private static final double SIMULATED_ANNEALING_DECREASING_LAW = 0.99;

    public static Solution randomSolution() {
        List<Route> randomSolution = new ArrayList<>();

        Stop depot = CVRPGraph.getDepot();

        Route currentRoute = new Route(VEHICLE_CAPACITY);
        randomSolution.add(currentRoute);

        for (Stop stop : CVRPGraph.getClientList()) {
            if (currentRoute.getQuantity() + stop.getQuantity() <= currentRoute.getCapacity() - 30) {
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

    public static Solution greedySolution() {
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
        return finalSolution;
    }

    public static Solution simulatedAnnealing(Scope scope) {

        Solution currentSolution = randomSolution();
        Solution bestSolution = currentSolution;

        Double temperature = null;

        for (int i = 0; i < SIMULATED_ANNEALING_MAX_TEMPERATURE_CHANGE; i++) {
            for (int j = 0; j < SIMULATED_ANNEALING_MAX_MOVE_AT_TEMPERATURE; j++) {

                List<Solution> neighbours = getNeighbours(currentSolution);

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
                scope.publish("ROUTE_LOADED");
            }
            temperature = temperature * SIMULATED_ANNEALING_DECREASING_LAW;
        }

        CVRPGraph.setRoutingSolution(bestSolution);
        scope.publish("ROUTE_LOADED");
        return bestSolution;
    }

    /**
     * Get 10 fitness worse that initial solution's fitness and use the average to compute initial temperature
     *
     * @param initialSolution
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

    private static List<Solution> getNeighbours(Solution solution) {

        List<Solution> neighbours = new ArrayList<>();

        for (Stop stop : CVRPGraph.getClientList()) {
            for (Route route : solution.getRouteList()) {

                // We add stop to an existing route
                if (!route.containsStop(stop)) {
                    Solution newSolution = new Solution(solution);
                    if (newSolution.addStopToExistingRoute(stop, route)) {
                        neighbours.add(newSolution);
                    }
                }

                for (Stop stop1 : route.getStopList()) {

                    // We swap stops
                    if (!stop.equals(stop1)) {
                        Solution newSolution1 = new Solution(solution);
                        if (newSolution1.swapTwoStop(stop, stop1)) {
                            neighbours.add(newSolution1);
                        }
                    }
                }
            }
        }

        for (Route route : solution.getRouteList()) {
            Solution newSolution = new Solution(solution);
            List<Route> routeToRemove = new ArrayList<>();
            for (Route route1 : newSolution.getRouteList()) {
                if (!routeToRemove.contains(route) && !route.equals(route1) && newSolution.mergeTwoRoute(route, route1)) {
                    routeToRemove.add(route);
                    neighbours.add(newSolution);
                }
            }
            routeToRemove.forEach(route1 -> newSolution.getRouteList().remove(route1));
        }

        return neighbours;
    }

    protected static double computeCost(Stop initialStop, Stop finalStop) {
        return Math.sqrt(
                Math.pow(finalStop.getX() - initialStop.getX(), 2) +
                        Math.pow(finalStop.getY() - initialStop.getY(), 2)
        );
    }
}
