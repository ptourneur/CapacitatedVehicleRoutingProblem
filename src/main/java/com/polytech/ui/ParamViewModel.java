package com.polytech.ui;

import com.polytech.model.CVRPGraph;
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
    private final BooleanProperty launchButtonDisable = new SimpleBooleanProperty(!CVRPGraph.getClientList().isEmpty());

    public IntegerProperty vehicleNumber() {
        return vehicleNumber;
    }

    public BooleanProperty dataLoaded () {
        return launchButtonDisable;
    }

    public void loadData() {
        CVRPGraph.loadDataFile("src/main/resources/data/data01.txt");
        scope.publish("LOADED");
        launchButtonDisable.setValue(!CVRPGraph.getClientList().isEmpty());
    }
}
