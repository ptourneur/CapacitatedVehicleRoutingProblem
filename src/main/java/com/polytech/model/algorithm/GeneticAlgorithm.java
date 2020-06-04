package com.polytech.model.algorithm;

import com.polytech.model.Graph;
import com.polytech.model.Solution;
import com.polytech.model.Stop;
import com.polytech.ui.CustomerScope;
import com.polytech.util.Tuple2;
import javafx.scene.control.ProgressBar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GeneticAlgorithm extends CVRPAlgorithm {

    private static final int POPULATION_SIZE = 100;
    private static final int SELECTED_SOLUTION_PER_SELECTION = 100;
    private static final double MUTATION_PROBABILITY = 0.1;

    /**
     * Genetic algorithm implementation
     *
     * @param graph         the graph where stops are loaded and where we have to set the solution
     * @param optionalScope the scope we have to notify to update the view
     */
    @Override
    public void runAlgorithm(Graph graph, Optional<CustomerScope> optionalScope) {

        optionalScope.ifPresent(scope -> scope.currentIteration().set(ProgressBar.INDETERMINATE_PROGRESS));

        Solution bestSolution = null;

        LinkedList<Solution> population = new LinkedList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(CVRPAlgorithm.randomSolution(graph, 0.5));
        }
            while (population.stream().distinct().count() > 1) {
                List<Solution> selection = selection(population);

                for (int i = 0; i < selection.size() / 2; i = i + 2) {

                    Solution solution1 = selection.get(i);
                    Solution solution2 = selection.get(i + 1);
                    Tuple2<Solution> crossoverSolutions = crossover(solution1, solution2);

                    Solution newSolution1 = crossoverSolutions.getT1();
                    Solution newSolution2 = crossoverSolutions.getT2();

                    population.add(newSolution1);
                    population.add(newSolution2);
                    population.sort(Comparator.comparingDouble(Solution::getFitness));
                    population.removeLast();
                    population.removeLast();

                    Solution currentBestSolution = population.stream()
                            .min(Comparator.comparingDouble(Solution::getFitness))
                            .orElseThrow();

                    if (bestSolution == null || currentBestSolution.getFitness() < bestSolution.getFitness()) {
                        bestSolution = currentBestSolution;
                    }

                    graph.setRoutingSolution(currentBestSolution);
                    optionalScope.ifPresent(scope -> scope.publish(ROUTE_LOADED));
                }
            }

            graph.setRoutingSolution(bestSolution);
            if (optionalScope.isPresent()) {
                optionalScope.get().currentIteration().setValue(0);
                optionalScope.get().publish(ROUTE_LOADED);
            }
    }

    private List<Solution> selection(List<Solution> population) {
        List<Solution> selection = new ArrayList<>();
        double fitnessSum = population.stream().mapToDouble(Solution::getFitness).sum();
        double[] probabilityArray = new double[population.size()];
        double currentProbability = 0.0;

        // We initialize the probability array
        for (int i = 0; i < probabilityArray.length; i++) {
            currentProbability = currentProbability + (population.get(i).getFitness() / fitnessSum);
            probabilityArray[i] = currentProbability;
        }

        for (int i = 0; i < SELECTED_SOLUTION_PER_SELECTION; i++) {
            double randomProbability = random.nextDouble();
            for (int j = 0; j < probabilityArray.length; j++) {
                if (probabilityArray[j] > randomProbability) {
                    selection.add(population.get(j));
                    break;
                }
            }
        }
        return selection;
    }

    private Tuple2<Solution> crossover(Solution solution1, Solution solution2) {
        Map<Stop, Integer> chromosome1 = solution1.getChromosome();
        Map<Stop, Integer> chromosome2 = solution2.getChromosome();

        int pivot1 = random.nextInt(chromosome1.size() - 1);
        int pivot2 = pivot1 + random.nextInt(chromosome1.size() - pivot1);

        int i = 0;
        for (Map.Entry<Stop, Integer> entry : chromosome1.entrySet()) {
            if (i >= pivot1 && (i <= pivot2 || pivot1 == pivot2)) {
                int tempValue = entry.getValue();
                chromosome1.replace(entry.getKey(), chromosome2.get(entry.getKey()));
                chromosome2.replace(entry.getKey(), tempValue);
            }
            i++;
        }

        mutate(chromosome1);
        mutate(chromosome2);

        Solution newSolution1 = new Solution(chromosome1, solution1.getDepot(), VEHICLE_CAPACITY);
        Solution newSolution2 = new Solution(chromosome2, solution2.getDepot(), VEHICLE_CAPACITY);

        if (newSolution1.isValid(solution1.getStopList().size()) && newSolution2.isValid(solution2.getStopList().size())) {
            return new Tuple2<>(newSolution1, newSolution2);
        } else {
            return new Tuple2<>(solution1, solution2);
        }
    }

    private void mutate(Map<Stop, Integer> chromosome) {
        int routeCount = (int) chromosome.values().stream().distinct().count();

        for (Stop stop : chromosome.keySet()) {
            if (random.nextDouble() < MUTATION_PROBABILITY) {
                int randomRouteIndex = random.nextInt(routeCount);
                chromosome.replace(stop, randomRouteIndex);
            }
        }
    }
}
