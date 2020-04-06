package com.polytech.ui;

import com.polytech.model.CVRPGraph;
import com.polytech.model.Route;
import com.polytech.model.Step;
import com.polytech.model.Stop;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphViewModel implements ViewModel {

    private static final double UI_UNIT = 7.5;

    private final ObservableList<Group> stopList = FXCollections.observableArrayList();
    private final ObservableList<Line> stepList = FXCollections.observableArrayList();

    @InjectScope
    private CustomerScope scope;

    public void initialize() {

        scope.subscribe("STOP_LOADED", (key, payload) -> {
            stepList.clear();
            stopList.clear();
            Stop depot = CVRPGraph.getDepot();
            stopList.add(new Group(new Circle(toUiUnit(depot.getX()), toUiUnit(depot.getY()), 3, Color.BLACK)));
            stopList.addAll(CVRPGraph.getClientList().stream()
                    .map(this::initializeCircleGroup)
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

    public ObservableList<Group> stopList() {
        return stopList;
    }

    public ObservableList<Line> stepList() {
        return stepList;
    }

    private Group initializeCircleGroup(Stop stop) {
        Circle circle = new Circle(toUiUnit(stop.getX()), toUiUnit(stop.getY()), 3, Color.RED);
        if (scope.displayLabel().get()) {
            Text text = new Text(circle.getCenterX() - 5, circle.getCenterY() - 4, String.valueOf(stop.getQuantity()));
            text.setFont(Font.font("Arial", 9));
            return new Group(circle, text);
        } else {
            return new Group(circle);
        }
    }

    private double toUiUnit(double coordinate) {
        return (coordinate*UI_UNIT)+UI_UNIT;
    }
}
