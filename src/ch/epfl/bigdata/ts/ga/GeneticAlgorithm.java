package ch.epfl.bigdata.ts.ga;

import java.util.HashMap;
import java.util.List;

import ch.epfl.bigdata.ts.ga.crossover.CrossoverMethod;
import ch.epfl.bigdata.ts.ga.mutation.MutationMethod;
import ch.epfl.bigdata.ts.ga.selection.SelectionMethod;
import ch.epfl.bigdata.ts.ga.util.Util;
import ch.epfl.data.bigdata.algorithmictrading.pattern.fitness.FitnessFunction;

public class GeneticAlgorithm {
	private static int NUM_OF_GENERATIONS = 100;
	
	public static Chromosome run(List<Chromosome> chromosomes, HashMap<String, Util.Range> geneRange, FitnessFunction fitnessFunc, SelectionMethod selMethod,
			CrossoverMethod crossMethod, MutationMethod mutatMethod) {
		
		Population population = new Population(chromosomes, selMethod, crossMethod, mutatMethod, geneRange);
		fitnessFunc.evaluate(population);
		for(int i = 0; i < NUM_OF_GENERATIONS; i++) {
			population.selection();
			System.out.println("Iteration " + i + ", best chr: " + population.bestChromosome().getFitness());
			population.crossover();
			population.mutation();
			fitnessFunc.evaluate(population);
			//System.out.println("Population size " + population.getChromosomes().size());
		}
		
		return population.bestChromosome();
	}
}