package com.polytech.model;

import com.polytech.model.exception.UndefinedStop;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Route {

    private final List<Step> stepList = new LinkedList<>();
    private final double capacity;
    private double quantity;

    public Route(double capacity) {
        this.capacity = capacity;
        this.quantity = 0;
    }

    public Route(Route route) {
        this.capacity = route.getCapacity();
        this.quantity = route.getQuantity();
        this.stepList.addAll(route.getStepList().stream().map(Step::new).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        return stepList.equals(route.stepList);
    }

    @Override
    public int hashCode() {
        return stepList.hashCode();
    }

    public boolean isComplete() {
        Optional<Stop> initialStop = getFirstStop();
        Optional<Stop> finalStop = getLastStop();
        return initialStop.isPresent() && finalStop.isPresent() && initialStop.equals(finalStop);
    }

    public void addStep(Step step) {
        stepList.add(step);
        quantity += step.getArrivalStop().getQuantity();
    }

    public double getCost() {
        return stepList.stream().mapToDouble(Step::getCost).sum();
    }

    public Optional<Stop> getLastStop() {
        try {
            return Optional.of(stepList.get(stepList.size() - 1).getArrivalStop());
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public Optional<Stop> getFirstStop() {
        try {
            return Optional.of(stepList.get(0).getDepartureStop());
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return Optional.empty();
        }
    }

    /**
     * Add the stop at the best place in the route in order to minimize the cost
     *
     * @param newStop the Stop to add
     * @return true if the stop was added to the route
     */
    public boolean addStop(Stop newStop) {
        if (quantity + newStop.getQuantity() <= capacity) {
            double minCost = Double.MAX_VALUE;
            Step step1ToAdd = null;
            Step step2ToAdd = null;
            Step stepToRemove = null;

            for (Step step : stepList) {
                Step tempStep1 = new Step(step.getDepartureStop(), newStop);
                Step tempStep2 = new Step(newStop, step.getArrivalStop());

                if (tempStep1.getCost() + tempStep2.getCost() - step.getCost() < minCost) {
                    minCost = tempStep1.getCost() + tempStep2.getCost() - step.getCost();
                    step1ToAdd = tempStep1;
                    step2ToAdd = tempStep2;
                    stepToRemove = step;
                }

                tempStep1 = new Step(step.getArrivalStop(), newStop);
                tempStep2 = new Step(newStop, step.getDepartureStop());

                if (tempStep1.getCost() + tempStep2.getCost() - step.getCost() < minCost) {
                    minCost = tempStep1.getCost() + tempStep2.getCost() - step.getCost();
                    step1ToAdd = tempStep1;
                    step2ToAdd = tempStep2;
                    stepToRemove = step;
                }
            }

            if (step1ToAdd != null) {
                stepList.add(step1ToAdd);
                stepList.add(step2ToAdd);
                stepList.remove(stepToRemove);
                quantity += newStop.getQuantity();
                return true;
            }
        }
        return false;
    }

    public void removeStop(Stop stop) {
        Stop departureStop = null;
        Stop arrivalStop = null;
        List<Step> stepToRemove = new ArrayList<>();

        for (Step step : stepList) {
            if (step.getArrivalStop().equals(stop)) {
                departureStop = step.getDepartureStop();
                stepToRemove.add(step);
            }

            if (step.getDepartureStop().equals(stop)) {
                arrivalStop = step.getArrivalStop();
                stepToRemove.add(step);
            }
        }

        // TODO remove or generalize to other methods
        if (departureStop == null || arrivalStop == null) {
            throw new UndefinedStop("Stop was not present in the route");
        }
        stepList.removeAll(stepToRemove);
        stepList.add(new Step(departureStop, arrivalStop));
        quantity -= stop.getQuantity();
    }

    public List<Stop> getStopList() {
        return stepList.stream()
                .map(Step::getArrivalStop)
                .filter(stop -> !stop.isDepot())
                .collect(Collectors.toList());
    }

    public boolean containsStop(Stop stop) {
        return stepList.stream()
                .anyMatch(step -> step.getDepartureStop().equals(stop) || step.getArrivalStop().equals(stop));
    }
}
