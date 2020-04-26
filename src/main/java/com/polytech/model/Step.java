package com.polytech.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Step {

    private Stop departureStop;
    private Stop arrivalStop;

    public Step(Stop departureStop, Stop arrivalStop) {
        this.departureStop = departureStop;
        this.arrivalStop = arrivalStop;
    }

    public double getCost() {
        return CVRP.computeCost(departureStop, arrivalStop);
    }
}
