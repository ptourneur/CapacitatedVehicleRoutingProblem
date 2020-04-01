package com.polytech.model;

import lombok.Getter;

@Getter
public class Customer {

    private final int id;
    private final double x;
    private final double y;
    private final int quantity;

    public boolean isDepot() { return id == 0; }

    public Customer(int id, double x, double y, int quantity) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.quantity = quantity;
    }
}
