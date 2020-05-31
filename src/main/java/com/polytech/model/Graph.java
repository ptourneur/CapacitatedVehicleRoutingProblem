package com.polytech.model;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private final Stop depot;
    private final List<Stop> stopList;

    private Solution routingSolution;

    public Graph(Stop depot, List<Stop> clientList) {
        this.stopList = clientList;
        this.depot = depot;
        this.routingSolution = new Solution(new ArrayList<>());
    }

    public List<Stop> getStopList() {
        return stopList;
    }

    public Stop getDepot() {
        return depot;
    }

    public Solution getRoutingSolution() {
        return routingSolution;
    }

    public void setRoutingSolution(Solution routingSolution) {
        this.routingSolution = routingSolution;
    }

    public void reinitializeRoutingSolution() {
        stopList.forEach(stop -> stop.setRouted(false));
    }
}
