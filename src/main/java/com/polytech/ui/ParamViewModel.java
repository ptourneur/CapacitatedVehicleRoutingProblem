package com.polytech.ui;

import com.polytech.model.CVRP;
import com.polytech.model.CVRPGraph;
import com.polytech.model.Route;
import com.polytech.model.Step;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

@Slf4j
public class ParamViewModel implements ViewModel {

    public static final String ERROR_ALERT = "ERROR_ALERT";
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @InjectScope
    private CustomerScope scope;


    private final IntegerProperty clientNumber = new SimpleIntegerProperty(0);
    private final IntegerProperty vehicleNumber = new SimpleIntegerProperty(0);
    private final DoubleProperty totalDistance = new SimpleDoubleProperty(0.0);
    private final BooleanProperty launchButtonDisable = new SimpleBooleanProperty(!CVRPGraph.getClientList().isEmpty());

    public IntegerProperty clientNumber() {
        return clientNumber;
    }

    public IntegerProperty vehicleNumber() {
        return vehicleNumber;
    }

    public DoubleProperty totalDistance() {
        return totalDistance;
    }

    public BooleanProperty dataLoaded () {
        return launchButtonDisable;
    }

    public void loadData() {
        try {
            CVRPGraph.loadDataFile("src/main/resources/data/A3405.txt");
            scope.publish("STOP_LOADED");
            clientNumber.setValue(CVRPGraph.getClientList().size());
            launchButtonDisable.setValue(!CVRPGraph.getClientList().isEmpty());
        } catch (Exception e) {
            log.error("loadData", e);
            publish(ERROR_ALERT, e.getClass().getCanonicalName());
        }
    }

    public void launchSimulation() {
        try {
            CVRP.generateRandomSolution();
            scope.publish("ROUTE_LOADED");
            List<Route> routingSolution = CVRPGraph.getRoutingSolution();
            vehicleNumber.setValue(routingSolution.size());
            totalDistance.setValue((double) Math.round(routingSolution.stream()
                    .map(Route::getStepList)
                    .flatMap(Collection::stream)
                    .mapToDouble(Step::getCost).sum() * 100) / 100);
        } catch (Exception e) {
            log.error("launchSimulation", e);
            publish(ERROR_ALERT, e.getClass().getCanonicalName());
        }
    }
}
