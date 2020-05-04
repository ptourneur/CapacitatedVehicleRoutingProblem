package com.polytech.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Solution {

    private final List<Route> routingSolution = new ArrayList<>();

    public Solution(List<Route> routingSolution) {
        this.routingSolution.addAll(routingSolution);
    }

    public Solution(Solution solution) {
        this.routingSolution.addAll(
                solution.getRoutingSolution().stream()
                        .map(Route::new)
                        .collect(Collectors.toList()));
    }

    public double getFitness() {
        return routingSolution.stream()
                .map(Route::getStepList)
                .flatMap(Collection::stream)
                .mapToDouble(Step::getCost).sum();
    }

    public void swapTwoStop(Stop stop1, Stop stop2) {
        Step stepBeforeStop1 = null;
        Step stepAfterStop1 = null;
        Step stepBeforeStop2 = null;
        Step stepAfterStop2 = null;
        for (Route route : routingSolution) {
            for (Step step: route.getStepList()) {
                if (step.getDepartureStop().equals(stop1)) {
                    stepAfterStop1 = step;
                }
                if (step.getArrivalStop().equals(stop1)) {
                    stepBeforeStop1 = step;
                }
                if (step.getDepartureStop().equals(stop2)) {
                    stepAfterStop2 = step;
                }
                if (step.getArrivalStop().equals(stop2)) {
                    stepBeforeStop2 = step;
                }
            }
        }
        if (stepBeforeStop1 != null && stepAfterStop1 != null && stepBeforeStop2 != null && stepAfterStop2 != null) {
            stepBeforeStop1.setArrivalStop(stop2);
            stepAfterStop1.setDepartureStop(stop2);
            stepBeforeStop2.setArrivalStop(stop1);
            stepAfterStop2.setDepartureStop(stop1);
        }
    }

    public void addStopToExistingRoute(Stop newStop, Route route) {
        boolean stopWasAdded = false;
        for (Route currentRoute: routingSolution) {
            if (currentRoute.equals(route)) {
                stopWasAdded = currentRoute.addStop(newStop);
            }
        }
        if (stopWasAdded) {
            for (Route currentRoute : routingSolution) {
                if (currentRoute.containsStop(newStop)) {
                    currentRoute.removeStop(newStop);
                }
            }
        }
    }
}
