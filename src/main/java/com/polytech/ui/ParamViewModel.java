package com.polytech.ui;

import com.polytech.model.CVRP;
import com.polytech.model.CVRPGraph;
import com.polytech.model.Route;
import com.polytech.model.Step;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

@Slf4j
public class ParamViewModel implements ViewModel {

    public static final String ERROR_ALERT = "ERROR_ALERT";

    @InjectScope
    private CustomerScope scope;

    private final IntegerProperty totalClientNumber = new SimpleIntegerProperty(0);
    private final DoubleProperty totalDistance = new SimpleDoubleProperty(0.0);
    private final IntegerProperty totalVehicleNumber = new SimpleIntegerProperty(0);

    private final BooleanProperty greedySolution = new SimpleBooleanProperty();
    private final BooleanProperty simulatedAnnealingSolution = new SimpleBooleanProperty();
    private final BooleanProperty tabuSolution = new SimpleBooleanProperty();

    private final BooleanProperty launchButtonDisable = new SimpleBooleanProperty(!CVRPGraph.getClientList().isEmpty());

    public IntegerProperty totalClientNumber() {
        return totalClientNumber;
    }

    public DoubleProperty totalDistance() {
        return totalDistance;
    }

    public IntegerProperty totalVehicleNumber() {
        return totalVehicleNumber;
    }

    public IntegerProperty selectedVehicleClientNumber() { return scope.selectedVehicleClientNumber(); }

    public DoubleProperty selectedVehicleDistance() { return scope.selectedVehicleDistance(); }

    public DoubleProperty selectedVehicleCharge() { return scope.selectedVehicleCharge(); }

    public DoubleProperty selectedVehicleCapacity() { return scope.selectedVehicleCapacity(); }

    public BooleanProperty greedySolution() { return greedySolution; }

    public BooleanProperty simulatedAnnealingSolution() { return simulatedAnnealingSolution; }

    public BooleanProperty tabuSolution() { return tabuSolution; }

    public BooleanProperty dataLoaded () {
        return launchButtonDisable;
    }

    public void loadData() {
        try {
            CVRPGraph.reinitializeRoutingSolution();
            CVRPGraph.loadDataFile("src/main/resources/data/A3405.txt");
            scope.publish("STOP_LOADED");
            refreshSolutionInformation();
            launchButtonDisable.setValue(!CVRPGraph.getClientList().isEmpty());
        } catch (Exception e) {
            log.error("loadData", e);
            publish(ERROR_ALERT, e.getClass().getCanonicalName());
        }
    }

    public void launchSimulation() {
        try {
            CVRPGraph.reinitializeRoutingSolution();
            CVRP.greedySolution();
            scope.publish("ROUTE_LOADED");
            refreshSolutionInformation();
        } catch (Exception e) {
            log.error("launchSimulation", e);
            publish(ERROR_ALERT, e.getClass().getCanonicalName());
        }
    }

    private void refreshSolutionInformation() {
        totalClientNumber.setValue(CVRPGraph.getClientList().size());
        List<Route> routingSolution = CVRPGraph.getRoutingSolution();
        totalVehicleNumber.setValue(routingSolution.size());
        totalDistance.setValue((double) Math.round(routingSolution.stream()
                .map(Route::getStepList)
                .flatMap(Collection::stream)
                .mapToDouble(Step::getCost).sum() * 100) / 100);
    }
}
