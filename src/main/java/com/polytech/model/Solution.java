package com.polytech.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Solution {

    private final List<Route> routeList = new ArrayList<>();

    public Solution(List<Route> routeList) {
        this.routeList.addAll(routeList);
    }

    public Solution(Solution solution) {
        this.routeList.addAll(
                solution.getRouteList().stream()
                        .map(Route::new)
                        .collect(Collectors.toList()));
    }

    public double getFitness() {
        return routeList.stream()
                .map(Route::getStepList)
                .flatMap(Collection::stream)
                .mapToDouble(Step::getCost).sum();
    }

    public boolean swapTwoStop(Stop stop1, Stop stop2) {
        Step stepBeforeStop1 = null;
        Step stepAfterStop1 = null;
        Step stepBeforeStop2 = null;
        Step stepAfterStop2 = null;
        Route stop1Route = null;
        Route stop2Route = null;

        for (Route route : routeList) {
            for (Step step : route.getStepList()) {
                if (step.getDepartureStop().equals(stop1)) {
                    stepAfterStop1 = step;
                    stop1Route = route;
                }
                if (step.getArrivalStop().equals(stop1)) {
                    stepBeforeStop1 = step;
                }
                if (step.getDepartureStop().equals(stop2)) {
                    stop2Route = route;
                    stepAfterStop2 = step;
                }
                if (step.getArrivalStop().equals(stop2)) {
                    stepBeforeStop2 = step;
                }
            }
        }
        if (stepBeforeStop1 != null && stepAfterStop1 != null && stepBeforeStop2 != null && stepAfterStop2 != null
                && stop1Route != null && stop1Route.getQuantity() - stop1.getQuantity() + stop2.getQuantity() <= stop1Route.getCapacity()
                && stop2Route != null && stop2Route.getQuantity() - stop2.getQuantity() + stop1.getQuantity() <= stop2Route.getCapacity()) {

            stepBeforeStop1.setArrivalStop(stop2);
            stepAfterStop1.setDepartureStop(stop2);
            stepBeforeStop2.setArrivalStop(stop1);
            stepAfterStop2.setDepartureStop(stop1);
            return true;
        }
        return false;
    }

    public boolean addStopToExistingRoute(Stop newStop, Route route) {
        boolean stopWasAdded = false;
        for (Route currentRoute : routeList) {
            if (currentRoute.equals(route)) {
                stopWasAdded = currentRoute.addStop(newStop);
                route = currentRoute; // We affect this route to route, if not the stop will be removed from this route too
            }
        }
        if (stopWasAdded) {
            for (Route currentRoute : routeList) {
                if (currentRoute.containsStop(newStop) && !currentRoute.equals(route)) {
                    currentRoute.removeStop(newStop);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Merge route1 into route2 if possible
     *
     * @param route1
     * @param route2
     */
    public boolean mergeTwoRoute(Route route1, Route route2) {
        if (route1.getQuantity() + route2.getQuantity() <= route2.getCapacity()) {
            List<Stop> stopList = route1.getStopList();
            stopList.forEach(route2::addStop);
            return true;
        }
        return false;
    }
}
