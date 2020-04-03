package com.polytech.model;

import lombok.Getter;

@Getter
public class Stop {

    private final int id;
    private final double x;
    private final double y;
    private final int quantity;
    private boolean routed;

    public boolean isDepot() { return id == 0; }

    public Stop(int id, double x, double y, int quantity) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.quantity = quantity;
        this.routed = false;
    }

    public void setRouted(boolean routed) {
        this.routed = routed;
    }
}
