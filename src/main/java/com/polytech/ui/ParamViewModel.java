package com.polytech.ui;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ParamViewModel implements ViewModel {

    private final DoubleProperty vehicleNumber = new SimpleDoubleProperty();

    public DoubleProperty vehicleNumber() {
        return vehicleNumber;
    }

    public double getVehicleNumber() {
        return this.vehicleNumber.get();
    }

    public void setVehicleNumber(double vehicleNumber) {
        this.vehicleNumber.set(vehicleNumber);
    }
}
