package com.polytech.ui;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphView implements FxmlView<GraphViewModel>, Initializable {

    @FXML
    private Group stopGroup;
    @FXML
    private Group stepGroup;
    @InjectViewModel
    private GraphViewModel graphViewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graphViewModel.stopList().addListener((ListChangeListener<? super Group>) change -> {
            stopGroup.getChildren().clear();
            stopGroup.getChildren().addAll(change.getList());
        });
        graphViewModel.stepList().addListener((ListChangeListener<? super Line>) change -> {
            stepGroup.getChildren().clear();
            stepGroup.getChildren().addAll(change.getList());
        });
    }
}
