package com.polytech.model.algorithm;

import com.polytech.model.Graph;
import com.polytech.model.Solution;
import com.polytech.ui.CustomerScope;

import java.util.ArrayList;
import java.util.List;

public class SimulatedAnnealing extends NeighborhoodAlgorithm {

    private static final int MAX_TEMPERATURE_CHANGE = 400;
    private static final int MAX_MOVE_AT_TEMPERATURE = 300;
    private static final double START_DECREASING_LAW = 0.95;
    private static final double END_DECREASING_LAW = 0.99;

    /**
     * Simulated annealing implementation
     * - The initial solution is given by {@code randomSolution()}
     * - The initial temperature is given by {@code initializeTemperature()}
     *
     * @param graph the graph where stops are loaded and where we have to set the solution
     * @param scope the scope we have to notify to update the view
     */
    @Override
    public void runAlgorithm(Graph graph, CustomerScope scope) {

        Solution currentSolution = CVRPAlgorithm.randomSolution(graph);
        Solution bestSolution = currentSolution;
        double mu = START_DECREASING_LAW;

        scope.totalIteration().setValue(MAX_TEMPERATURE_CHANGE);

        Double temperature = null;

        for (int i = 0; i < MAX_TEMPERATURE_CHANGE; i++) {
            for (int j = 0; j < MAX_MOVE_AT_TEMPERATURE; j++) {

                List<Solution> neighbours = getNeighbors(currentSolution, graph.getStopList());

                // We initialize the temperature
                if (temperature == null) {
                    temperature = initializeTemperature(currentSolution, neighbours);
                }

                int randomIndex = random.nextInt(neighbours.size());
                Solution selectedNeighbour = neighbours.get(randomIndex);

                if (selectedNeighbour.getFitness() <= currentSolution.getFitness()) {
                    currentSolution = selectedNeighbour;
                    if (selectedNeighbour.getFitness() < bestSolution.getFitness()) {
                        bestSolution = selectedNeighbour;
                    }
                } else {
                    double p = random.nextDouble();
                    if (p <= Math.exp((currentSolution.getFitness() - selectedNeighbour.getFitness()) / temperature)) {
                        currentSolution = selectedNeighbour;
                    }
                }
                graph.setRoutingSolution(currentSolution);
                scope.currentIteration().setValue(i + 1);
                scope.publish(ROUTE_LOADED);
            }
            temperature = temperature * mu;
            if (mu < END_DECREASING_LAW) {
                mu = mu + 0.001;
            }
        }

        graph.setRoutingSolution(bestSolution);
        scope.currentIteration().setValue(0);
        scope.publish(ROUTE_LOADED);
    }

    /**
     * Gets 20 fitness worse that initial solution's fitness and use the average to compute initial temperature
     *
     * @param initialSolution the initial solution
     * @param neighbours      neighbours of the initial solution
     * @return (initial fitness - average) / ln(0.8)
     */
    private static double initializeTemperature(Solution initialSolution, List<Solution> neighbours) {
        List<Double> tenWorseFitnessThanRandomSolution = new ArrayList<>();

        while (tenWorseFitnessThanRandomSolution.size() <= 20) {

            int randomIndex = random.nextInt(neighbours.size());
            Solution selectedNeighbour = neighbours.get(randomIndex);

            if (selectedNeighbour.getFitness() > initialSolution.getFitness()) {
                tenWorseFitnessThanRandomSolution.add(selectedNeighbour.getFitness());
            }
        }
        double worseSolutionAverageFitness = tenWorseFitnessThanRandomSolution.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(initialSolution.getFitness());

        return (initialSolution.getFitness() - worseSolutionAverageFitness) / Math.log(0.8);
    }
}
