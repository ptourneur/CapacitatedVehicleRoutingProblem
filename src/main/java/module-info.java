module CapacitatedVehicleRoutingProblem {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.saxsys.mvvmfx;
    requires static lombok;
    requires static org.mapstruct.processor;

    opens com.polytech.ui to de.saxsys.mvvmfx, javafx.fxml;
    exports com.polytech;
}