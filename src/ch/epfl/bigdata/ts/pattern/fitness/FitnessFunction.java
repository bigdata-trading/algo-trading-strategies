package ch.epfl.bigdata.ts.pattern.fitness;

import java.util.List;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.Population;

public abstract class FitnessFunction {
	public void evaluate(Population population) {
		List<Chromosome> chrs = population.getChromosomes();
		for(int i = 0; i < chrs.size(); i++) {
			calcFitness(chrs.get(i));
		}
	}
	protected abstract void calcFitness(Chromosome chr);
}
