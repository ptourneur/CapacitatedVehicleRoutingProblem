module CapacitatedVehicleRoutingProblem {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.polytech to javafx.fxml;
    exports com.polytech;
}