package com.polytech.model;

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

    public Stop(Stop stop) {
        this.id = stop.getId();
        this.x = stop.getX();
        this.y = stop.getY();
        this.quantity = stop.getQuantity();
        this.routed = stop.isRouted();
    }

    public void setRouted(boolean routed) {
        this.routed = routed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stop stop = (Stop) o;

        if (id != stop.id) return false;
        if (Double.compare(stop.x, x) != 0) return false;
        if (Double.compare(stop.y, y) != 0) return false;
        return quantity == stop.quantity;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + quantity;
        return result;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isRouted() {
        return routed;
    }
}
