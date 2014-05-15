package ch.epfl.bigdata.ts.ga;

import java.util.HashMap;
import java.util.List;

import ch.epfl.bigdata.ts.ga.crossover.CrossoverMethod;
import ch.epfl.bigdata.ts.ga.mutation.MutationMethod;
import ch.epfl.bigdata.ts.ga.selection.SelectionMethod;
import ch.epfl.bigdata.ts.ga.util.Range;
import ch.epfl.bigdata.ts.pattern.fitness.FitnessFunction;

public class GeneticAlgorithm {
    //TODO: find the best values
    static final int NUM_OF_GENERATIONS = 100 ;
    static final int NUM_OF_GENERATIONS_IN_WINDOW = 4;

    static final double SELECTIVITY = 0.5;//0.5;
    static final int NUM_ELITE = 9; //7;
    static final int TOURNAMENT_SIZE = 10;
    static final double CROSSOVER_PROBABILITY = 0.8;//0.6;
    static final double MUTATION_PROBABILITY = 0.015;//0.01;

    public static final int GENE_LENGTH = 10;

    public static Chromosome run(List<Chromosome> chromosomes, HashMap<String, Range> geneRange, FitnessFunction fitnessFunc, SelectionMethod selMethod,
                                 CrossoverMethod crossMethod, MutationMethod mutatMethod) {
        Population population = new Population(chromosomes, selMethod, crossMethod, mutatMethod, geneRange);
        System.out.println("size first " + population.getChromosomes().size());
        fitnessFunc.evaluate(population);
        for (int i = 1; i < NUM_OF_GENERATIONS; i++) {
            System.out.println("size before " + population.getChromosomes().size());
            population.selection();
            System.out.println("Iteration " + i + ", best chr: " + population.bestChromosome().getFitness() + ", number of transactions: " + population.bestChromosome().getNumberOfTransactions());
            population.crossover();
            population.mutation();
            System.out.println("size after " + population.getChromosomes().size());
            fitnessFunc.evaluate(population);
            if ((i + 1) % NUM_OF_GENERATIONS_IN_WINDOW == 0) {
                fitnessFunc.increaseDay();
            }
            //System.out.println("Population size " + population.getChromosomes().size());
        }

        return population.bestChromosome();
    }
}