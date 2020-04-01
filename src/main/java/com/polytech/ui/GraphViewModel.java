package com.polytech.ui;

import com.polytech.model.Customer;
import com.polytech.model.CustomerList;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.stream.Collectors;

public class GraphViewModel implements ViewModel {

    private final ObservableList<Circle> customerList = FXCollections.observableArrayList();

    @InjectScope
    private CustomerScope scope;

    public void initialize() {
        scope.subscribe("LOADED", (key, payload) -> {
            customerList.clear();
            List<Customer> test = CustomerList.getCustomerList();
            customerList.addAll(test.stream()
                    .map(customer ->
                            new Circle(customer.getX()*3.5, customer.getY()*3.5, 3,(customer.isDepot() ? Color.BLACK : Color.RED)))
                    .collect(Collectors.toList()));
        });
    }

    public ObservableList<Circle> customerList() {
        return customerList;
    }
}
