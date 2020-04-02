package com.polytech.ui;

import com.polytech.model.CVRPGraph;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParamViewModel implements ViewModel {

    public static final String ERROR_ALERT = "ERROR_ALERT";

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
        try {
            CVRPGraph.loadDataFile("src/main/resources/data/A3405_.txt");
            scope.publish("LOADED");
            launchButtonDisable.setValue(!CVRPGraph.getClientList().isEmpty());
        } catch (Exception e) {
            log.error("loadData", e);
            publish(ERROR_ALERT, e.getClass().getCanonicalName());
        }
    }
}
