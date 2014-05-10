package ch.epfl.bigdata.ts.ga.crossover;

import java.util.ArrayList;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.Gene;
import ch.epfl.bigdata.ts.ga.util.Range;

public class SinglePointCrossover implements CrossoverMethod {

	public Chromosome cross(Chromosome chr1, Chromosome chr2) {
		double pickFirstChr = Range.R.nextDouble();
		int crossoverPoint = 1 + Range.R.nextInt(chr1.getNumGenes() - 1);
		
		Chromosome offspring = new Chromosome(new ArrayList<Gene>());
		if(pickFirstChr < 0.5) {
			//first is chr1
			chr1.copyGenes(0, crossoverPoint, offspring);
			chr2.copyGenes(crossoverPoint, chr2.getNumGenes(), offspring);
		} else {
			//first is chr2
			chr2.copyGenes(0, crossoverPoint, offspring);
			chr1.copyGenes(crossoverPoint, chr2.getNumGenes(), offspring);
		}
		return offspring;
	}
}
