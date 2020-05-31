package com.polytech.ui;

import com.polytech.model.Graph;
import com.polytech.model.Solution;
import com.polytech.model.algorithm.CVRPAlgorithm;
import com.polytech.model.algorithm.GeneticAlgorithm;
import com.polytech.model.algorithm.SimulatedAnnealing;
import com.polytech.model.algorithm.TabuSearch;
import com.polytech.model.filereader.CVRPFileReader;
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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParamViewModel implements ViewModel {

    public static final String ERROR_ALERT = "ERROR_ALERT";
    public static final String ROUTE_LOADED = "ROUTE_LOADED";

    @InjectScope
    private CustomerScope scope;

    private final Command greedyCommand = new DelegateCommand(this::greedyAction, true);
    private final Command simulatedAnnealingCommand = new DelegateCommand(this::simulatedAnnealingAction, true);
    private final Command tabuSearchCommand = new DelegateCommand(this::tabuSearchAction, true);
    private final Command geneticCommand = new DelegateCommand(this::geneticAlgorithmAction, true);
    private final Command launchCommand = new CompositeCommand(greedyCommand, simulatedAnnealingCommand, tabuSearchCommand);

    private final IntegerProperty totalClientNumber = new SimpleIntegerProperty(0);
    private final DoubleProperty totalDistance = new SimpleDoubleProperty(0.0);
    private final IntegerProperty totalVehicleNumber = new SimpleIntegerProperty(0);

    private final ObservableList<String> fileList = FXCollections.observableArrayList();
    private final StringProperty selectedFile = new SimpleStringProperty();

    private final BooleanProperty greedySolution = new SimpleBooleanProperty();
    private final BooleanProperty simulatedAnnealingSolution = new SimpleBooleanProperty();
    private final BooleanProperty tabuSolution = new SimpleBooleanProperty();
    private final BooleanProperty geneticSolution = new SimpleBooleanProperty();

    private final BooleanProperty stopLoaded = new SimpleBooleanProperty(false);

    private final DoubleProperty progress = new SimpleDoubleProperty(0.0);
    private final BooleanProperty progressBarVisible = new SimpleBooleanProperty(false);

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

    public IntegerProperty selectedVehicleClientNumber() {
        return scope.selectedVehicleClientNumber();
    }

    public DoubleProperty selectedVehicleDistance() {
        return scope.selectedVehicleDistance();
    }

    public DoubleProperty selectedVehicleCharge() {
        return scope.selectedVehicleCharge();
    }

    public DoubleProperty selectedVehicleCapacity() {
        return scope.selectedVehicleCapacity();
    }

    public ObservableList<String> fileList() {
        return fileList;
    }

    public StringProperty selectedFile() {
        return selectedFile;
    }

    public BooleanProperty greedySolution() {
        return greedySolution;
    }

    public BooleanProperty simulatedAnnealingSolution() {
        return simulatedAnnealingSolution;
    }

    public BooleanProperty tabuSolution() {
        return tabuSolution;
    }

    public BooleanProperty geneticSolution() {
        return geneticSolution;
    }

    public BooleanProperty stopLoaded() {
        return stopLoaded;
    }

    public DoubleProperty progress() {
        return progress;
    }

    public BooleanProperty progressBarVisible() {
        return progressBarVisible;
    }

    public void initialize() {
        scope.subscribe(ROUTE_LOADED, (key, payload) ->
                scope.getGraph().ifPresent(this::refreshSolutionInformation)
        );
        try (Stream<Path> walk = Files.walk(Paths.get("src/main/resources/data"))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString()).collect(Collectors.toList());

            fileList.addAll(result);

        } catch (IOException e) {
            publish(ERROR_ALERT, e.getClass().getCanonicalName());
        }
    }

    public void loadData() {
        try {
            Graph graph = CVRPFileReader.loadDataFile(selectedFile.get());
            scope.setGraph(graph);
            scope.publish("STOP_LOADED");
            refreshSolutionInformation(graph);
            stopLoaded.setValue(!graph.getStopList().isEmpty());
        } catch (Exception e) {
            publish(ERROR_ALERT, e.getClass().getCanonicalName());
        }
    }

    public void launchSimulation() {
        try {
            if (greedySolution.get()) {
                greedyCommand.execute();
            }
            if (simulatedAnnealingSolution.get()) {
                simulatedAnnealingCommand.execute();
            }
            if (tabuSolution.get()) {
                tabuSearchCommand.execute();
            }
            if (geneticSolution.get()) {
                geneticCommand.execute();
            }
        } catch (Exception e) {
            publish(ERROR_ALERT, e.getClass().getCanonicalName());
        }
    }

    private void refreshSolutionInformation(Graph graph) {
        totalClientNumber.setValue(graph.getStopList().size());
        Solution solution = graph.getRoutingSolution();
        totalVehicleNumber.setValue(solution.getRouteList().size());
        totalDistance.setValue((double) Math.round(solution.getFitness() * 100) / 100);
        progressBarVisible.setValue(scope.currentIteration().isNotEqualTo(0).get());
        progress.setValue(scope.currentIteration().get() / scope.totalIteration().get());
    }

    private Action greedyAction() {
        return new Action() {
            @Override
            protected void action() {
                scope.getGraph().ifPresent(CVRPAlgorithm::greedySolution);
                scope.publish(ROUTE_LOADED);
            }
        };
    }

    private Action simulatedAnnealingAction() {
        return new Action() {
            @Override
            protected void action() {
                new SimulatedAnnealing().applyAlgorithm(scope);
            }
        };
    }

    private Action tabuSearchAction() {
        return new Action() {
            @Override
            protected void action() {
                new TabuSearch().applyAlgorithm(scope);
            }
        };
    }

    private Action geneticAlgorithmAction() {
        return new Action() {
            @Override
            protected void action() {
                new GeneticAlgorithm().applyAlgorithm(scope);
            }
        };
    }
}
