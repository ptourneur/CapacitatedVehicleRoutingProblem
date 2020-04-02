package com.polytech.ui;

import com.polytech.model.CVRPGraph;
import com.polytech.model.Stop;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.stream.Collectors;

public class GraphViewModel implements ViewModel {

    private final ObservableList<Circle> stopList = FXCollections.observableArrayList();

    @InjectScope
    private CustomerScope scope;

    public void initialize() {
        scope.subscribe("LOADED", (key, payload) -> {
            stopList.clear();
            Stop depot = CVRPGraph.getDepot();
            stopList.add( new Circle((depot.getX()*3.5)+3.5,
                    (depot.getY()*3.5)+3.5, 3, Color.BLACK));
            stopList.addAll(CVRPGraph.getClientList().stream()
                    .map(stop -> new Circle((stop.getX()*3.5)+3.5,
                            (stop.getY()*3.5)+3.5, 3, Color.RED))
                    .collect(Collectors.toList()));
        });
    }

    public ObservableList<Circle> stopList() {
        return stopList;
    }
}
