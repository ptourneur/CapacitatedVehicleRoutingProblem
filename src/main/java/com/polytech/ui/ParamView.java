package com.polytech.ui;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ResourceBundle;

public class ParamView implements FxmlView<ParamViewModel>, Initializable {

    @FXML
    private Label vehicleNumberLabel;
    @FXML
    private Slider vehicleNumberSlider;
    @FXML
    private Button launchButton;

    @InjectViewModel
    private ParamViewModel paramViewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vehicleNumberLabel.textProperty().bind(paramViewModel.vehicleNumber().asString());
        vehicleNumberSlider.valueProperty().bindBidirectional(paramViewModel.vehicleNumber());
    }

    @FXML
    public void loadData() {
        paramViewModel.loadData();
    }

    // TODO
    @FXML
    public void launchSimulation() { }

}
