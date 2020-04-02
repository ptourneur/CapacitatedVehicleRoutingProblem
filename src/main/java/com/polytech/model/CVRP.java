package com.polytech.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CVRP {

    private static final double VEHICLE_CAPACITY = 100;

    public static void generateRandomSolution() {
        List<Route> randomRoutingSolution = new ArrayList<>();

        List<Stop> stopList = CVRPGraph.getClientList();

        Stop depot = CVRPGraph.getDepot();

        Route currentRoute = new Route(VEHICLE_CAPACITY);
        for (Stop currentStop: stopList) {
            // we compute the cost between the last stop of the route and the current stop
            Stop lastStop = Optional.ofNullable(currentRoute.getLastStop()).orElse(depot);
            double cost = computeCost(lastStop, currentStop);
            if (currentRoute.getCost() + computeCost(currentStop, depot) <= currentRoute.getCapacity()) {
                currentRoute.addStep(new Step(lastStop, currentStop, cost));
            } else {
                currentRoute.addStep(new Step(lastStop, depot, computeCost(lastStop, depot)));
                randomRoutingSolution.add(currentRoute);
                currentRoute = new Route(VEHICLE_CAPACITY);
            }
        }

        if (!currentRoute.isComplete()) {
            Stop lastStop = currentRoute.getLastStop();
            if (lastStop != null) {
                currentRoute.addStep(new Step(lastStop, depot, computeCost(lastStop, depot)));
                randomRoutingSolution.add(currentRoute);
            }
        }
        CVRPGraph.setRoutingSolution(randomRoutingSolution);
    }

    private static double computeCost(Stop initialStop, Stop finalStop) {
        return Math.sqrt(
                Math.pow(finalStop.getX() - initialStop.getX(), 2) +
                        Math.pow(finalStop.getY() - initialStop.getY(), 2)
        );
    }
}
