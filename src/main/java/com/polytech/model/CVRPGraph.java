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

    public static Solution getRoutingSolution() {
        return bestSolution;
    }

    public static void reinitializeRoutingSolution() {
        clientList.forEach(stop -> stop.setRouted(false));
    }
}
