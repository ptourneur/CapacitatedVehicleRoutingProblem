package com.polytech.model;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class Route {

    private final List<Step> stepList = new LinkedList<>();
    private final double capacity;
    private double quantity;

    public Route(double capacity) {
        this.capacity = capacity;
        this.quantity = 0;
    }

    public boolean isComplete() {
        Stop initialStop = getFirstStop();
        Stop finalStop = getLastStop();
        return (initialStop != null && finalStop != null) && initialStop.equals(finalStop);
    }

    public void addStep(Step step) {
        stepList.add(step);
        quantity += step.getArrivalStop().getQuantity();
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
