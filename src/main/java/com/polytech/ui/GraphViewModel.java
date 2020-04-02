package com.polytech.ui;

import com.polytech.model.CVRPGraph;
import com.polytech.model.Route;
import com.polytech.model.Step;
import com.polytech.model.Stop;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphViewModel implements ViewModel {

    private final ObservableList<Circle> stopList = FXCollections.observableArrayList();
    private final ObservableList<Line> stepList = FXCollections.observableArrayList();

    @InjectScope
    private CustomerScope scope;

    public void initialize() {
        scope.subscribe("STOP_LOADED", (key, payload) -> {
            stopList.clear();
            Stop depot = CVRPGraph.getDepot();
            stopList.add(new Circle(toUiUnit(depot.getX()), toUiUnit(depot.getY()), 3, Color.BLACK));
            stopList.addAll(CVRPGraph.getClientList().stream()
                    .map(stop -> new Circle(toUiUnit(stop.getX()), toUiUnit(stop.getY()), 3, Color.RED))
                    .collect(Collectors.toList()));
        });

        scope.subscribe("ROUTE_LOADED", (key, payload) -> {
            stepList.clear();
            List<Route> routes = CVRPGraph.getRoutingSolution();
            List<Line> lines = new ArrayList<>();
            for (Route route: routes) {
                Color color = Color.color(Math.random(), Math.random(), Math.random());
                List<Step> steps = route.getStepList();
                for (Step step: steps) {
                    Line line = new Line(toUiUnit(step.getDepartureStop().getX()), toUiUnit(step.getDepartureStop().getY()),
                            toUiUnit(step.getArrivalStop().getX()), toUiUnit(step.getArrivalStop().getY()));
                    line.setStroke(color);
                    lines.add(line);
                }
            }
            stepList.addAll(lines);
        });
    }

    public ObservableList<Circle> stopList() {
        return stopList;
    }

    public ObservableList<Line> stepList() {
        return stepList;
    }

    private double toUiUnit(double coordinate) {
        return (coordinate*3.5)+3.5;
    }
}
