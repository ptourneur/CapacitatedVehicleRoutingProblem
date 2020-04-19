package com.polytech.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class Solution {

    private final List<Route> routingSolution = new ArrayList<>();

    public Solution(List<Route> routingSolution) {
        this.routingSolution.addAll(routingSolution);
    }

    public double getFitness() {
        return routingSolution.stream()
                .map(Route::getStepList)
                .flatMap(Collection::stream)
                .mapToDouble(Step::getCost).sum();
    }
}
