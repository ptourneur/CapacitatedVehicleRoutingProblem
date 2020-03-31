module CapacitatedVehicleRoutingProblem {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.saxsys.mvvmfx;

    opens com.polytech.ui to de.saxsys.mvvmfx, javafx.fxml;
    exports com.polytech;
}