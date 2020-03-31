package com.polytech.ui;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;

public class ParamViewModel implements ViewModel {

    private final IntegerProperty vehicleNumber = new SimpleIntegerProperty(1);

    public IntegerProperty vehicleNumber() {
        return vehicleNumber;
    }
}
