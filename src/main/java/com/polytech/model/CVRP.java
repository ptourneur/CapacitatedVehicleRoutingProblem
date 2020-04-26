package com.polytech.model;

import java.util.ArrayList;
import java.util.List;

public final class CVRP {

    private static final double VEHICLE_CAPACITY = 100;

    public static void greedySolution() {
        List<Route> greedySolution = new ArrayList<>();

        List<Stop> stopList = CVRPGraph.getClientList();

        Stop depot = CVRPGraph.getDepot();

        Route currentRoute = new Route(VEHICLE_CAPACITY);
        greedySolution.add(currentRoute);

        while (stopList.stream().map(Stop::isRouted).anyMatch(isRouted -> !isRouted)) {
            double minCost = Double.MAX_VALUE;
            Stop lastStop = (currentRoute.getLastStop() == null ? depot : currentRoute.getLastStop());
            Stop bestStop = null;

            for (Stop currentStop: stopList) {
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
        currentRoute.addStep(new Step(currentRoute.getLastStop(), depot));
        CVRPGraph.setRoutingSolution(greedySolution);
    }

    protected static double computeCost(Stop initialStop, Stop finalStop) {
        return Math.sqrt(
                Math.pow(finalStop.getX() - initialStop.getX(), 2) +
                        Math.pow(finalStop.getY() - initialStop.getY(), 2)
        );
    }


}
