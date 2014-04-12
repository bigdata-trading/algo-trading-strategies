package ch.epfl.bigdata.ts.pattern.fitness;

import java.util.List;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.Chromosome.Gene;

public class FitnessFunctionTest extends FitnessFunction {
	private static int A = 0, B = 1, C = 2, D = 3;
	protected void calcFitness(Chromosome chr) {
		List<Gene> genes = chr.getGenes();
		chr.setFitness(1.0 / (1 + Math.abs(genes.get(A).getValue() + 2 * genes.get(B).getValue()
				+ 3 * genes.get(C).getValue() + 4 * genes.get(D).getValue() - 30)));
	}	
}