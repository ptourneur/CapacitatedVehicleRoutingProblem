package com.polytech.ui;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ParamView implements FxmlView<ParamViewModel>, Initializable {

    @FXML
    private Label clientNumberValue;
    @FXML
    private Label vehicleNumberValue;
    @FXML
    private Label totalDistanceValue;
    @FXML
    private Button launchButton;

    @FXML
    public void loadData() {
        paramViewModel.loadData();
    }
    @FXML
    public void launchSimulation() {
        paramViewModel.launchSimulation();
    }

    @InjectViewModel
    private ParamViewModel paramViewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientNumberValue.textProperty().bind(paramViewModel.clientNumber().asString());
        vehicleNumberValue.textProperty().bind(paramViewModel.vehicleNumber().asString());
        totalDistanceValue.textProperty().bind(paramViewModel.totalDistance().asString());
        launchButton.disableProperty().bind(paramViewModel.dataLoaded().not());

        paramViewModel.subscribe(ParamViewModel.ERROR_ALERT, (key, payload) -> {
            String message = String.valueOf(payload[0]);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(message);
            alert.setContentText(message);
            alert.show();
        });
    }
}
