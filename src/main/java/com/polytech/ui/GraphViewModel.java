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

import java.util.List;
import java.util.stream.Collectors;

public class GraphViewModel implements ViewModel {

    private static final double UI_UNIT = 7.4;

    private final ObservableList<Group> stopList = FXCollections.observableArrayList();
    private final ObservableList<Group> stepList = FXCollections.observableArrayList();

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
            List<Route> routes = CVRPGraph.getRoutingSolution().getRoutingSolution();
            for (Route route : routes) {
                Color color = Color.color(Math.random(), Math.random(), Math.random());
                List<Step> steps = route.getStepList();
                Group group = new Group();
                Group labelGroup = new Group();
                labelGroup.setVisible(false);
                for (Step step : steps) {
                    Line line = new Line(toUiUnit(step.getDepartureStop().getX()), toUiUnit(step.getDepartureStop().getY()),
                            toUiUnit(step.getArrivalStop().getX()), toUiUnit(step.getArrivalStop().getY()));
                    line.setStroke(color);
                    Line lineForEventTriggering = new Line(toUiUnit(step.getDepartureStop().getX()), toUiUnit(step.getDepartureStop().getY()),
                            toUiUnit(step.getArrivalStop().getX()), toUiUnit(step.getArrivalStop().getY()));
                    lineForEventTriggering.setStrokeWidth(10);
                    lineForEventTriggering.setStroke(Color.TRANSPARENT);
                    Text text = new Text((line.getStartX() + (line.getEndX() - line.getStartX()) / 2), (line.getStartY() + (line.getEndY() - line.getStartY()) / 2),
                            String.valueOf((double) Math.round(step.getCost() * 10) / 10));
                    text.setFont(Font.font("Arial", 9));
                    lineForEventTriggering.setOnMouseEntered(mouseEvent -> handleMouseEntered(labelGroup, route));
                    lineForEventTriggering.setOnMouseExited(mouseEvent -> handleMouseExited(labelGroup));
                    group.getChildren().addAll(line, lineForEventTriggering);
                    labelGroup.getChildren().add(text);
                }
                stepList.addAll(group, labelGroup);
            }
        });
    }

    public ObservableList<Group> stopList() {
        return stopList;
    }

    public ObservableList<Group> stepList() {
        return stepList;
    }

    private Group initializeCircleGroup(Stop stop) {
        Circle circle = new Circle(toUiUnit(stop.getX()), toUiUnit(stop.getY()), 3, Color.RED);
        Circle circleForEventTriggering = new Circle(toUiUnit(stop.getX()), toUiUnit(stop.getY()), 6, Color.TRANSPARENT);
        Text text = new Text(circle.getCenterX() - 5, circle.getCenterY() - 4, String.valueOf(stop.getQuantity()));
        text.setFont(Font.font("Arial", 9));
        text.setVisible(false);
        circleForEventTriggering.setOnMouseEntered(mouseEvent -> text.setVisible(true));
        circleForEventTriggering.setOnMouseExited(mouseEvent -> text.setVisible(false));
        return new Group(circle, circleForEventTriggering, text);
    }

    private void handleMouseEntered(Group labelGroup, Route selectedRoute) {
        labelGroup.setVisible(true);
        scope.selectedVehicleClientNumber().setValue(selectedRoute.getStepList().size() - 1);
        scope.selectedVehicleDistance().setValue((double) Math.round(selectedRoute.getCost() * 10) / 10);
        scope.selectedVehicleCharge().setValue((double) Math.round(selectedRoute.getQuantity() * 10) / 10);
        scope.selectedVehicleCapacity().setValue((double) Math.round(selectedRoute.getCapacity() * 10) / 10);
    }

    private void handleMouseExited(Group labelGroup) {
        labelGroup.setVisible(false);
        scope.selectedVehicleClientNumber().setValue(0);
        scope.selectedVehicleDistance().setValue(0);
        scope.selectedVehicleCharge().setValue(0);
        scope.selectedVehicleCapacity().setValue(0);
    }

    private double toUiUnit(double coordinate) {
        return (coordinate * UI_UNIT) + UI_UNIT;
    }
}
