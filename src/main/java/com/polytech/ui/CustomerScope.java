package com.polytech.ui;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CustomerScope implements Scope {

    private final IntegerProperty selectedVehicleClientNumber = new SimpleIntegerProperty(0);
    private final DoubleProperty selectedVehicleDistance = new SimpleDoubleProperty(0.0);
    private final DoubleProperty selectedVehicleCharge = new SimpleDoubleProperty(0.0);
    private final DoubleProperty selectedVehicleCapacity = new SimpleDoubleProperty(0.0);

    private final IntegerProperty currentIteration = new SimpleIntegerProperty(0);
    private final DoubleProperty totalIteration = new SimpleDoubleProperty(1.0);

    public IntegerProperty selectedVehicleClientNumber() { return selectedVehicleClientNumber; }

    public DoubleProperty selectedVehicleDistance() { return selectedVehicleDistance; }

    public DoubleProperty selectedVehicleCharge() { return selectedVehicleCharge; }

    public DoubleProperty selectedVehicleCapacity() { return selectedVehicleCapacity; }

    public IntegerProperty currentIteration() { return currentIteration; }

    public DoubleProperty totalIteration() { return totalIteration; }
}
