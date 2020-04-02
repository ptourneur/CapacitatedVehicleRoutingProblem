package com.polytech.ui;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphView implements FxmlView<GraphViewModel>, Initializable {

    @FXML
    private Group group;
    @InjectViewModel
    private GraphViewModel graphViewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graphViewModel.stopList().addListener((ListChangeListener<? super Circle>) change -> {
            group.getChildren().clear();
            group.getChildren().addAll(change.getList());
        });
    }
}
