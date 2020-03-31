package com.polytech.ui;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AppView implements FxmlView<AppViewModel>, Initializable {

    @FXML
    private VBox appMainBox;

    @InjectViewModel
    private AppViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
