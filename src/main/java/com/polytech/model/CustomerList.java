package com.polytech.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CustomerList {

    private CustomerList() {}

    private static List<Customer> customers = new ArrayList<>();

    public static List<Customer> getCustomerList() {
        return customers;
    }

    public static void loadDataFile(String path) {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {

            customers = stream
                    .filter(line -> !line.equals("i;x;y;q"))
                    .map(CustomerList::stringToCustomer)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Customer stringToCustomer(String csvLine) {
        String[] csvLineArray = csvLine.split(";");
        return new Customer(Integer.parseInt(csvLineArray[0]), Double.parseDouble(csvLineArray[1]),
                Double.parseDouble(csvLineArray[2]), Integer.parseInt(csvLineArray[3]));
    }
}
