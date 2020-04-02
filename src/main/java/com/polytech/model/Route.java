package com.polytech.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Route {

    private final double capacity;
    private final List<Step> stepList = new LinkedList<>();

    public boolean isComplete() {
        Stop initialStop = getFirstStop();
        Stop finalStop = getLastStop();
        return (initialStop != null && finalStop != null) && initialStop.equals(finalStop);
    }

    public void addStep(Step step) {
        stepList.add(step);
    }

    public double getCost() {
        return stepList.stream().mapToDouble(Step::getCost).sum();
    }

    public Stop getLastStop() {
        try {
            return stepList.get(stepList.size() - 1).getArrivalStop();
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
    }

    public Stop getFirstStop() {
        try {
            return stepList.get(0).getDepartureStop();
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
    }
}
