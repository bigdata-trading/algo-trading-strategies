package ch.epfl.bigdata.ts.ga.test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.GeneticAlgorithm;
import ch.epfl.bigdata.ts.ga.Chromosome.Gene;
import ch.epfl.bigdata.ts.ga.crossover.CrossoverMethod;
import ch.epfl.bigdata.ts.ga.crossover.SinglePointCrossover;
import ch.epfl.bigdata.ts.ga.mutation.MutationMethod;
import ch.epfl.bigdata.ts.ga.mutation.UniformMutation;
import ch.epfl.bigdata.ts.ga.selection.RouletteWheelSelection;
import ch.epfl.bigdata.ts.ga.selection.SelectionMethod;
import ch.epfl.bigdata.ts.ga.util.Util.Range;
import ch.epfl.bigdata.ts.pattern.fitness.FitnessFunctionTest;


public class Test {

	public static void main(String[] args) {
		
		List<Chromosome> chromosomes = new ArrayList<Chromosome>();
		
		//double [] values = {7, 5, 3, 1};
		//addChromosome(chromosomes, values);
		
		for(int i = 0; i < 4; i++) {
		
			//double [] values0 = {12 + i, 5 + i, 23 + i, 8 + i};
			//addChromosome(chromosomes, values0);
			
			double [] values1 = {2 + i, 21 + i, 18 + i, 3 + i};
			addChromosome(chromosomes, values1);		
			
			double [] values2 = {10 + i, 4 + i, 13 + i, 14 + i};
			addChromosome(chromosomes, values2);
			
			double [] values3 = {20 + i, 1 + i, 10 + i, 6 + i};
			addChromosome(chromosomes, values3);
			
			double [] values4 = {1 + i, 4 + i, 13 + i, 19 + i};
			addChromosome(chromosomes, values4);
			
			double [] values5 = {20 + i, 5 + i, 17 + i, 1 + i};
			addChromosome(chromosomes, values5);
			
			//double [] values6 = {11 + i, 12 + i, 13 + i, 14 + i};
			//addChromosome(chromosomes, values6);
		}
		
		HashMap<String, Range> geneRange = new HashMap<String, Range>();
		geneRange.put("a", new Range(0, 30));
		geneRange.put("b", new Range(0, 30));
		geneRange.put("c", new Range(0, 30));
		geneRange.put("d", new Range(0, 30));
		
		SelectionMethod selMethod = new RouletteWheelSelection(); //new RankSelection();
		CrossoverMethod crossMethod = new SinglePointCrossover(); //TwoPointCrossover(); UniformCrossover();
		MutationMethod mutatMethod = new UniformMutation();
		
		Chromosome best = GeneticAlgorithm.run(chromosomes, geneRange, new FitnessFunctionTest(), selMethod, crossMethod, mutatMethod);
		System.out.println(best);
		System.out.println("Fitness: " + best.getFitness());
	}
	
	private static void addChromosome(List<Chromosome> chromosomes, double [] values) {
		List<Gene> genes = new ArrayList<Gene>();
		Chromosome chr = new Chromosome(genes);
		
		genes.add(chr.new Gene("a", values[0])); 
		genes.add(chr.new Gene("b", values[1])); 
		genes.add(chr.new Gene("c", values[2]));
		genes.add(chr.new Gene("d", values[3]));
		
		chromosomes.add(chr);
	}

}
