package com.polytech.model;

import com.polytech.model.algorithm.CVRPAlgorithm;

public class Step {

    private Stop departureStop;
    private Stop arrivalStop;

    public Step(Stop departureStop, Stop arrivalStop) {
        this.departureStop = departureStop;
        this.arrivalStop = arrivalStop;
    }

    public Step(Step step) {
        this.departureStop = new Stop(step.getDepartureStop());
        this.arrivalStop = new Stop(step.getArrivalStop());
    }

    public double getCost() {
        return CVRPAlgorithm.computeCost(departureStop, arrivalStop);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Step step = (Step) o;

        if (!departureStop.equals(step.departureStop)) return false;
        return arrivalStop.equals(step.arrivalStop);
    }

    @Override
    public int hashCode() {
        int result = departureStop.hashCode();
        result = 31 * result + arrivalStop.hashCode();
        return result;
    }

    public Stop getDepartureStop() {
        return departureStop;
    }

    public Stop getArrivalStop() {
        return arrivalStop;
    }

    public void setDepartureStop(Stop departureStop) {
        this.departureStop = departureStop;
    }

    public void setArrivalStop(Stop arrivalStop) {
        this.arrivalStop = arrivalStop;
    }
}
