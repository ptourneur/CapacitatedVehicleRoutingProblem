package com.polytech.ui;

import com.polytech.model.CustomerList;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ParamViewModel implements ViewModel {

    @InjectScope
    private CustomerScope scope;

    private final IntegerProperty vehicleNumber = new SimpleIntegerProperty(1);
    private final BooleanProperty launchButtonDisable = new SimpleBooleanProperty(!CustomerList.getCustomerList().isEmpty());

    public void initialize() {
        scope.subscribe("LOADED", (key, payload) ->
                launchButtonDisable.setValue(!CustomerList.getCustomerList().isEmpty())
        );
    }

    public IntegerProperty vehicleNumber() {
        return vehicleNumber;
    }

    public BooleanProperty dataLoaded () {
        return launchButtonDisable;
    }

    public void loadData() {
        CustomerList.loadDataFile("src/main/resources/data/data01.txt");
        scope.publish("LOADED");
    }
}
