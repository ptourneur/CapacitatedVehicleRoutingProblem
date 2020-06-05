package com.polytech.model.io;

import com.polytech.model.Graph;
import com.polytech.model.Stop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CVRPFileReader {

    private static final String FOLDER_PATH = "src/main/resources/data/";

    private CVRPFileReader() {}

    public static Graph loadDataFile(String fileName) throws IOException {
        Stop depot = null;
        List<Stop> stopList;

        try (Stream<String> stream = Files.lines(Paths.get(FOLDER_PATH + fileName))) {
            stopList = stream
                    .skip(1)
                    .map(CVRPFileReader::initializeStop)
                    .collect(Collectors.toList());

            for (Stop stop: stopList) {
                if (stop.isDepot()) {
                    depot = stop;
                }
            }
            stopList.remove(depot);
        }
        return new Graph(depot, stopList);
    }

    private static Stop initializeStop(String csvLine) {
        String[] csvLineArray = csvLine.split(";");
        return new Stop(Integer.parseInt(csvLineArray[0]), Double.parseDouble(csvLineArray[1]),
                Double.parseDouble(csvLineArray[2]), Integer.parseInt(csvLineArray[3]));
    }
}
