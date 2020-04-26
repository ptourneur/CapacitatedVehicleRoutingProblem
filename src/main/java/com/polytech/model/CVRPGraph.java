package com.polytech.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CVRPGraph {

    private static Solution bestSolution = new Solution(new ArrayList<>());

    private static final List<Stop> clientList = new ArrayList<>();

    private static Stop depot = null;

    public static void loadDataFile(String path) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            clientList.clear();
            stream.skip(1).forEach(CVRPGraph::initializeClient);
        }
    }

    private static void initializeClient(String csvLine) {
        String[] csvLineArray = csvLine.split(";");
        if (Integer.parseInt(csvLineArray[0]) == 0) {
            depot = new Stop(Integer.parseInt(csvLineArray[0]), Double.parseDouble(csvLineArray[1]),
                    Double.parseDouble(csvLineArray[2]), Integer.parseInt(csvLineArray[3]));
        } else {
            clientList.add(new Stop(Integer.parseInt(csvLineArray[0]), Double.parseDouble(csvLineArray[1]),
                    Double.parseDouble(csvLineArray[2]), Integer.parseInt(csvLineArray[3])));
        }
    }

    public static List<Stop> getClientList() {
        return clientList;
    }

    public static Stop getDepot() {
        return depot;
    }

    public static void setRoutingSolution(List<Route> routingSolution) {
        bestSolution = new Solution(routingSolution);
    }

    public static Solution getBestSolution() {
        return bestSolution;
    }

    public static void reinitializeRoutingSolution() {
        clientList.forEach(stop -> stop.setRouted(false));
    }

    public static void swapTwoStop(Stop stop1, Stop stop2) {
        Step stepBeforeStop1 = null;
        Step stepAfterStop1 = null;
        Step stepBeforeStop2 = null;
        Step stepAfterStop2 = null;
        for (Route route : getBestSolution().getRoutingSolution()) {
            for (Step step: route.getStepList()) {
                if (step.getDepartureStop().equals(stop1)) {
                    stepAfterStop1 = step;
                }
                if (step.getArrivalStop().equals(stop1)) {
                    stepBeforeStop1 = step;
                }
                if (step.getDepartureStop().equals(stop2)) {
                    stepAfterStop2 = step;
                }
                if (step.getArrivalStop().equals(stop2)) {
                    stepBeforeStop2 = step;
                }
            }
        }
        if (stepBeforeStop1 != null && stepAfterStop1 != null && stepBeforeStop2 != null && stepAfterStop2 != null) {
            stepBeforeStop1.setArrivalStop(stop2);
            stepAfterStop1.setDepartureStop(stop2);
            stepBeforeStop2.setArrivalStop(stop1);
            stepAfterStop2.setDepartureStop(stop1);
        }
    }
}
