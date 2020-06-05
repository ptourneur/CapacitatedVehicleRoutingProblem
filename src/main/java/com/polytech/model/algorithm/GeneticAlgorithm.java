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
import java.util.stream.Collectors;

public class GeneticAlgorithm extends CVRPAlgorithm {

    private static final int POPULATION_SIZE = 100;
    private static final int SELECTED_SOLUTION_PER_SELECTION = POPULATION_SIZE;
    private static final double MUTATION_PROBABILITY = 0.1;

    /**
     * Genetic algorithm implementation
     *
     * @param graph the graph where stops are loaded and where we have to set the solution
     * @param scope the scope we have to notify to update the view
     */
    @Override
    public void runAlgorithm(Graph graph, CustomerScope scope) {

        scope.currentIteration().set(ProgressBar.INDETERMINATE_PROGRESS);

        Solution bestSolution = null;

        LinkedList<Solution> population = new LinkedList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(CVRPAlgorithm.randomSolution(graph, 0.9));
        }
        while (population.stream().distinct().count() > 1) {
            // We select individuals for crossover
            List<Solution> selection = selection(population);

            for (int i = 0; i < selection.size() / 2; i = i + 2) {

                Solution solution1 = selection.get(i);
                Solution solution2 = selection.get(i + 1);
                // We crossover solutions two by two
                Tuple2<Solution> crossoverSolutions = crossover(solution1, solution2);

                Solution newSolution1 = crossoverSolutions.getT1();
                Solution newSolution2 = crossoverSolutions.getT2();

                if (newSolution1 != solution1) {
                    population.add(newSolution1);
                    population.sort(Comparator.comparingDouble(Solution::getFitness));
                    population.removeLast();
                }
                if (newSolution2 != solution2) {
                    population.add(newSolution2);
                    population.sort(Comparator.comparingDouble(Solution::getFitness));
                    population.removeLast();
                }
            }

            Solution currentBestSolution = population.stream()
                    .min(Comparator.comparingDouble(Solution::getFitness))
                    .orElseThrow();

            if (bestSolution == null || currentBestSolution.getFitness() < bestSolution.getFitness()) {
                bestSolution = currentBestSolution;
            }

            graph.setRoutingSolution(currentBestSolution);
            scope.publish(ROUTE_LOADED);
        }

        graph.setRoutingSolution(bestSolution);

        scope.currentIteration().setValue(0);
        scope.publish(ROUTE_LOADED);

    }

    /**
     * Roulette wheel selection
     * - We first initialize an array where we add for each individual of the population a probability depending of
     *   its fitness over the fitness sum of each individual
     * - We do the draw
     *
     * @param population initial population
     * @return the new population after selection
     */
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

        // We do the draw
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

    /**
     * Crossovers solution by switch genes of chromosome's solutions around two pivot
     * After mutate the two new solutions
     *
     * @param solution1 first solution to cross
     * @param solution2 second solution to cross
     * @return a Tuple containing new solutions or their parents if they are not valid
     */
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

        if (!newSolution1.isValid(solution1.getStopList().size())) {
            newSolution1 = solution1;
        }
        if (!newSolution2.isValid(solution2.getStopList().size())) {
            newSolution2 = solution2;
        }
        return new Tuple2<>(newSolution1, newSolution2);
    }

    /**
     * For each stop, picks a random number an change the route of this stop if the number is lower than the mutation probability
     *
     * @param chromosome chromosome of a solution
     */
    private void mutate(Map<Stop, Integer> chromosome) {
        List<Integer> routeIdList = chromosome.values().stream()
                .distinct()
                .collect(Collectors.toList());

        for (Stop stop : chromosome.keySet()) {
            if (random.nextDouble() < MUTATION_PROBABILITY) {
                int randomRouteIdListIndex = random.nextInt(routeIdList.size());
                chromosome.replace(stop, routeIdList.get(randomRouteIdListIndex));
            }
        }
    }
}
