package com.polytech.model;

import com.polytech.model.exception.RandomNumberGenerationException;
import de.saxsys.mvvmfx.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class CVRP {

    private static final Random random = new Random();

    private static final double VEHICLE_CAPACITY = 100;
    private static final double SIMULATED_ANNEALING_MAX_TEMPERATURE_CHANGE = 1000;

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

        Solution currentSolution = greedySolution();
        Solution bestSolution = currentSolution;
        double decreasingLaw = 0.99;


        double temperature = -100 / Math.log(0.8);

        for (int i = 0; i < SIMULATED_ANNEALING_MAX_TEMPERATURE_CHANGE; i++) {
            List<Solution> neighbours = getNeighbours(currentSolution);
            int randomIndex = random.ints(0, neighbours.size())
                    .findAny()
                    .orElseThrow(() -> new RandomNumberGenerationException("Can't generate random number"));
            Solution selectedNeighbour = neighbours.get(randomIndex);

            if (selectedNeighbour.getFitness() <= currentSolution.getFitness()) {
                currentSolution = selectedNeighbour;
                if (selectedNeighbour.getFitness() < bestSolution.getFitness()) {
                    bestSolution = selectedNeighbour;
                }
            } else {
                int p = random.nextInt();
                if (p <= Math.exp(currentSolution.getFitness() - selectedNeighbour.getFitness()) / temperature) {
                    currentSolution = selectedNeighbour;
                }
            }
            CVRPGraph.setRoutingSolution(currentSolution);
            scope.publish("ROUTE_LOADED");
            temperature = temperature * decreasingLaw;
        }

        return bestSolution;
    }

    private static List<Solution> getNeighbours(Solution solution) {

        List<Solution> neighbours = new ArrayList<>();

        for (Stop stop : CVRPGraph.getClientList()) {
            for (Route route1 : solution.getRoutingSolution()) {

                // We add stop to an existing route
                Solution newSolution = new Solution(solution);
                newSolution.addStopToExistingRoute(stop, route1);
                neighbours.add(newSolution);

                for (Step step1 : route1.getStepList()) {

                    Stop departureStop1 = step1.getDepartureStop();
                    Stop arrivalStop1 = step1.getArrivalStop();

                    // We swap stops
                    if (!departureStop1.isDepot()) {
                        Solution newSolution2 = new Solution(solution);
                        newSolution2.swapTwoStop(stop, departureStop1);
                        neighbours.add(newSolution2);
                    }

                    if (!arrivalStop1.isDepot()) {
                        Solution newSolution3 = new Solution(solution);
                        newSolution3.swapTwoStop(stop, arrivalStop1);
                        neighbours.add(newSolution3);
                    }
                }
            }
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
