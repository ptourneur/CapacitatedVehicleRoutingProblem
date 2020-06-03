package com.polytech.model.algorithm;

import com.polytech.model.Graph;
import com.polytech.model.Solution;
import com.polytech.model.exception.NoNeighborException;
import com.polytech.ui.CustomerScope;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;

public class TabuSearch extends NeighborhoodAlgorithm {

    private static final int TABU_LIST_SIZE = 2000;
    private static final int MAX_ITERATION = 10000;

    /**
     * Tabu search implementation
     * - The initial solution is given by {@code randomSolution()}
     * - The initial temperature is given by {@code initializeTemperature()}
     *
     * @param graph the graph where stops are loaded and where we have to set the solution
     * @param optionalScope the scope we have to notify to update the view
     */
    @Override
    public void runAlgorithm(Graph graph, Optional<CustomerScope> optionalScope) {

        optionalScope.ifPresent(scope -> scope.totalIteration().setValue(MAX_ITERATION));

        Solution currentSolution = CVRPAlgorithm.randomSolution(graph);
        Solution bestSolution = currentSolution;

        LinkedList<Double> tabuList = new LinkedList<>();
        tabuList.add(currentSolution.getFitness());

        for (int i = 0; i < MAX_ITERATION; i++) {

            Solution bestNeighbor = getNeighbors(currentSolution, graph.getStopList()).stream()
                    .filter(neighbor -> !tabuList.contains(neighbor.getFitness()))
                    .min(Comparator.comparingDouble(Solution::getFitness))
                    .orElseThrow(() -> new NoNeighborException("Can't find any neighbor"));

            if (bestNeighbor.getFitness() >= currentSolution.getFitness()) {
                tabuList.addLast(currentSolution.getFitness());
                if (tabuList.size() > TABU_LIST_SIZE) {
                    tabuList.removeFirst();
                }
            }

            currentSolution = bestNeighbor;
            if (currentSolution.getFitness() < bestSolution.getFitness()) {
                bestSolution = currentSolution;
            }

            graph.setRoutingSolution(currentSolution);

            if (optionalScope.isPresent()) {
                optionalScope.get().currentIteration().setValue(i + 1);
                optionalScope.get().publish(ROUTE_LOADED);
            }
        }

        graph.setRoutingSolution(bestSolution);
        if (optionalScope.isPresent()) {
            optionalScope.get().currentIteration().setValue(0);
            optionalScope.get().publish(ROUTE_LOADED);
        }
    }
}
