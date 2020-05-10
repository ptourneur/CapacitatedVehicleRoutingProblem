module CapacitatedVehicleRoutingProblem {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.saxsys.mvvmfx;
    requires slf4j.api;

    opens com.polytech.ui to de.saxsys.mvvmfx, javafx.fxml;
    exports com.polytech;
}