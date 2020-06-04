package com.polytech.model;

import com.polytech.model.exception.UndefinedStopException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Route {

    private int id;
    private final double capacity;
    private final List<Step> stepList = new LinkedList<>();

    public Route(double capacity) {
        this.capacity = capacity;
    }

    public Route(int id, double capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public Route(Route route) {
        this.id = id;
        this.capacity = route.getCapacity();
        this.stepList.addAll(route.getStepList().stream().map(Step::new).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        return stepList.equals(route.getStepList());
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

    public boolean isValid() {
        return isComplete() && getCapacity() <= capacity;
    }

    public boolean isEmpty() {
        return stepList.isEmpty();
    }

    public void addStep(Step step) {
        stepList.add(step);
    }

    public double getCost() {
        return stepList.stream().mapToDouble(Step::getCost).sum();
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return getStopList().stream().mapToInt(Stop::getQuantity).sum();
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
        if (getQuantity() + newStop.getQuantity() <= capacity) {
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
                int index = stepList.indexOf(stepToRemove);
                stepList.add(index, step1ToAdd);
                stepList.add(index+1, step2ToAdd);
                stepList.remove(stepToRemove);
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

        if (departureStop == null || arrivalStop == null) {
            throw new UndefinedStopException("Stop was not present in the route");
        }
        stepList.removeAll(stepToRemove);
        stepList.add(new Step(departureStop, arrivalStop));
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

    public List<Step> getStepList() {
        return stepList;
    }

    public double getCapacity() {
        return capacity;
    }
}
