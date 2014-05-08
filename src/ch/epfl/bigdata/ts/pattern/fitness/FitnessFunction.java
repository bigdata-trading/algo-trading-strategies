package ch.epfl.bigdata.ts.pattern.fitness;

import java.util.List;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.Population;

public abstract class FitnessFunction {


	public void evaluate(Population population) {
		List<Chromosome> chrs = population.getChromosomes();
		for(int i = 0; i < chrs.size(); i++) {
           // System.out.println("Started evaluating chr " + i);
			calcFitness(chrs.get(i));
           // System.out.println("Finished evaluating chr " + i);
		}
	}
	protected abstract void calcFitness(Chromosome chr);

    public abstract void increaseDay();
}
