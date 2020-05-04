package com.polytech.ui;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ParamView implements FxmlView<ParamViewModel>, Initializable {

    @FXML
    private Label totalClientNumber;
    @FXML
    private Label totalDistance;
    @FXML
    private Label totalVehicleNumber;

    @FXML
    private ImageView infoIcon;
    @FXML
    private Label selectedVehicleClientNumber;
    @FXML
    private Label selectedVehicleDistance;
    @FXML
    private Label selectedVehicleCharge;
    @FXML
    private Label selectedVehicleCapacity;

    private final ToggleGroup radioButtonGroup = new ToggleGroup();
    @FXML
    private RadioButton greedySolutionButton;
    @FXML
    private RadioButton simulatedAnnealingSolutionButton;
    @FXML
    private RadioButton tabuSolutionButton;

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
        totalClientNumber.textProperty().bind(paramViewModel.totalClientNumber().asString());
        totalDistance.textProperty().bind(paramViewModel.totalDistance().asString());
        totalVehicleNumber.textProperty().bind(paramViewModel.totalVehicleNumber().asString());

        Tooltip infoIconTooltip = new Tooltip("Passez votre souris sur un trajet du graphique");
        infoIconTooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(infoIcon, infoIconTooltip);
        selectedVehicleClientNumber.textProperty().bind(paramViewModel.selectedVehicleClientNumber().asString());
        selectedVehicleDistance.textProperty().bind(paramViewModel.selectedVehicleDistance().asString());
        selectedVehicleCharge.textProperty().bind(paramViewModel.selectedVehicleCharge().asString());
        selectedVehicleCapacity.textProperty().bind(paramViewModel.selectedVehicleCapacity().asString());

        greedySolutionButton.setToggleGroup(radioButtonGroup);
        paramViewModel.greedySolution().bind(greedySolutionButton.selectedProperty());
        simulatedAnnealingSolutionButton.setToggleGroup(radioButtonGroup);
        paramViewModel.simulatedAnnealingSolution().bind(simulatedAnnealingSolutionButton.selectedProperty());
        tabuSolutionButton.setToggleGroup(radioButtonGroup);
        paramViewModel.tabuSolution().bind(tabuSolutionButton.selectedProperty());

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
