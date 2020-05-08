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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Solution solution = (Solution) o;

        return getFitness() == solution.getFitness();
    }

    @Override
    public int hashCode() {
        return routeList.hashCode();
    }

    /**
     * Swaps two stops (from same route or different one) if it is possible
     *
     * @param stop1 first stop to switch
     * @param stop2 second stop to switch
     * @return true if stops were swapped
     */
    public boolean swapTwoStops(Stop stop1, Stop stop2) {

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

    /**
     * Inserts the stop to its new route if it is possible,
     * delete it from its old one and delete its old route if it was the only stop inside
     *
     * @param newStop the stop to add
     * @param route the route which wil
     * @return true if the node was added
     */
    public boolean addStopToExistingRoute(Stop newStop, Route route) {

        boolean stopWasAdded = false;
        Route newStopOldRoute = null;

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
                    newStopOldRoute = currentRoute; // We save the route to delete it after if it is empty
                }
            }

            if (newStopOldRoute != null && newStopOldRoute.getStopList().isEmpty()) {
                routeList.remove(newStopOldRoute);
            }
            return true;
        }
        return false;
    }

    /**
     * Merges route1 into route2 if possible and remove route1 from the solution
     * All route we be added in order to minimize the fitness
     *
     * @param route1 the route you want to merge
     * @param route2 the route where you want to merge
     * @return true if route1 was merged into route2
     */
    public boolean mergeTwoRoutes(Route route1, Route route2) {
        if (route1.getQuantity() + route2.getQuantity() <= route2.getCapacity()) {

            List<Stop> stopList = route1.getStopList();

            for (Route route : routeList) {
                if (route.equals(route2)) {
                    stopList.forEach(route::addStop);
                }
            }

            routeList.remove(route1);
            return true;
        }
        return false;
    }
}
