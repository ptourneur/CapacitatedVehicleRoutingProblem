package com.polytech.ui;

import com.polytech.model.CVRP;
import com.polytech.model.CVRPGraph;
import com.polytech.model.Solution;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.CompositeCommand;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParamViewModel implements ViewModel {

    public static final String ERROR_ALERT = "ERROR_ALERT";

    @InjectScope
    private CustomerScope scope;

    private final Command greedyCommand = new DelegateCommand(this::greedyAction, true);
    private final Command simulatedAnnealingCommand = new DelegateCommand(this::simulatedAnnealingAction, true);
    private final Command tabuSearchCommand = new DelegateCommand(this::tabuSearchAction, true);
    private final Command launchCommand = new CompositeCommand(greedyCommand, simulatedAnnealingCommand, tabuSearchCommand);

    private final IntegerProperty totalClientNumber = new SimpleIntegerProperty(0);
    private final DoubleProperty totalDistance = new SimpleDoubleProperty(0.0);
    private final IntegerProperty totalVehicleNumber = new SimpleIntegerProperty(0);

    private final BooleanProperty greedySolution = new SimpleBooleanProperty();
    private final BooleanProperty simulatedAnnealingSolution = new SimpleBooleanProperty();
    private final BooleanProperty tabuSolution = new SimpleBooleanProperty();

    private final BooleanProperty launchButtonDisable = new SimpleBooleanProperty(!CVRPGraph.getClientList().isEmpty());

    public Command launchCommand() {
        return launchCommand;
    }

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

    public void initialize() {
        scope.subscribe("ROUTE_LOADED", (key, payload) -> refreshSolutionInformation());
    }

    public void loadData() {
        try {
            CVRPGraph.reinitializeRoutingSolution();
            CVRPGraph.loadDataFile("src/main/resources/data/A3205.txt");
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
            if (greedySolution.get()) {
                greedyCommand.execute();
            }
            if (simulatedAnnealingSolution.get()) {
                simulatedAnnealingCommand.execute();
            }
            if (tabuSolution.get()) {
                tabuSearchCommand.execute();
            }
        } catch (Exception e) {
            log.error("launchSimulation", e);
            publish(ERROR_ALERT, e.getClass().getCanonicalName());
        }
    }

    private void refreshSolutionInformation() {
        totalClientNumber.setValue(CVRPGraph.getClientList().size());
        Solution solution = CVRPGraph.getBestSolution();
        totalVehicleNumber.setValue(solution.getRouteList().size());
        totalDistance.setValue((double) Math.round(solution.getFitness() * 100) / 100);
    }

    private Action greedyAction() {
        return new Action() {
            @Override
            protected void action() {
                CVRP.greedySolution();
                scope.publish("ROUTE_LOADED");
            }
        };
    }

    private Action simulatedAnnealingAction() {
        return new Action() {
            @Override
            protected void action() {
                CVRP.simulatedAnnealing(scope);
            }
        };
    }

    private Action tabuSearchAction() {
        return new Action() {
            @Override
            protected void action() {
                CVRP.tabuSearch(scope);
            }
        };
    }
}
