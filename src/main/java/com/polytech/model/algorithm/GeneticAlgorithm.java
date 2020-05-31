package com.polytech.model.algorithm;

import com.polytech.model.Graph;
import com.polytech.model.Solution;
import com.polytech.ui.CustomerScope;
import com.polytech.util.Tuple2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class GeneticAlgorithm extends CVRPAlgorithm {

    private static final int POPULATION_SIZE = 20;
    private static final int SELECTED_SOLUTION_PER_SELECTION = 4;
    private static final double MUTATION_PROBABILITY = 0.005;

    /**
     * Genetic algorithm implementation
     *
     * @param graph the graph where stops are loaded and where we have to set the solution
     * @param optionalScope the scope we have to notify to update the view
     */
    @Override
    public void runAlgorithm(Graph graph, Optional<CustomerScope> optionalScope) {

        LinkedList<Solution> population = new LinkedList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(CVRPAlgorithm.randomSolution(graph));
        }


        while (population.stream().distinct().count() > 1) {
            List<Solution> selection = selection(population);

            for (int i = 0; i < selection.size()/2; i = i + 2) {

                Solution firstSolution = selection.get(i);
                Solution secondSolution = selection.get(i + 1);
                Tuple2<Solution> crossoverSolutions = crossover(firstSolution, secondSolution);

                Solution newFirstSolution = crossoverSolutions.getT1();
                Solution newSecondSolution = crossoverSolutions.getT2();

                if (random.nextDouble() < MUTATION_PROBABILITY) {
                    newFirstSolution = mutate(newFirstSolution);
                }
                if (random.nextDouble() < MUTATION_PROBABILITY) {
                    newSecondSolution = mutate(newSecondSolution);
                }

                population.add(newFirstSolution);
                population.add(newSecondSolution);
                population.sort(Comparator.comparingDouble(Solution::getFitness));
                population.removeLast();
                population.removeLast();
            }
        }
    }

    private List<Solution> selection(List<Solution> population) {
        List<Solution> selection = new ArrayList<>();
        double fitnessSum = population.stream().mapToDouble(Solution::getFitness).sum();
        double[] probabilityArray = new double[population.size()];
        double currentProbability = 0.0;

        // We initialize the probability array
        for (int i = 0; i < probabilityArray.length; i++) {
            probabilityArray[i] = currentProbability + (population.get(i).getFitness() / fitnessSum);
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

    private Tuple2<Solution> crossover(Solution firstSolution, Solution secondSolution) {
        return new Tuple2<>(firstSolution, secondSolution);
    }

    private Solution mutate(Solution solution) {
        return solution;
    }
}
