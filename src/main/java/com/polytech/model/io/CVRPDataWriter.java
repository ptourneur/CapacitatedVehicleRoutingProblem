package com.polytech.model.io;

import com.polytech.model.Graph;
import com.polytech.model.Solution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public final class CVRPDataWriter {

    private static final String FOLDER_PATH = "src/main/resources/analyse/";
    private static final DateTimeFormatter filenameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private CVRPDataWriter() {}

    public static void writeData(String analyseFilename, Graph graph, String algorithm, String fileName, Duration duration) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();

        System.out.println("-- " + fileName + " with " + algorithm + " finished in " + duration.toMinutes() + ":" + duration.toSecondsPart() + " --");

        int stopNumber = graph.getStopList().size();
        Solution solution = graph.getRoutingSolution();

        List<String> rowData = Arrays.asList(algorithm,
                fileName,
                Integer.toString(stopNumber),
                Integer.toString(solution.getRouteList().size()),
                Double.toString(solution.getFitness()).replace(".", ","),
                Long.toString(duration.toNanos()),
                duration.toMinutes() + ":" + duration.toSecondsPart(),
                dtf.format(now));
        String row = String.join(";", rowData) + "\n";

        Files.write(Paths.get(FOLDER_PATH + analyseFilename), row.getBytes(), StandardOpenOption.APPEND);
    }

    public static String initializeFile() throws IOException {
        String fileHeader = "Algorithm;File;Client number;Route number;Fitness;Time (nanoseconds);Time minutes:seconds;Execution date\n";
        String fileName = "execution_" + LocalDateTime.now().format(filenameFormatter) + ".csv";
        Files.write(Paths.get(FOLDER_PATH + fileName), fileHeader.getBytes());
        return fileName;
    }
}
