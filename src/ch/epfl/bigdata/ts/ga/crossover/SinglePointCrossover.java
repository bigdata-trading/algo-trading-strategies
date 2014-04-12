package ch.epfl.bigdata.ts.ga.crossover;

import java.util.ArrayList;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Util;

public class SinglePointCrossover implements CrossoverMethod {

	public Chromosome cross(Chromosome chr1, Chromosome chr2) {
		double pickFirstChr = Util.r.nextDouble();
		int crossoverPoint = 1 + Util.r.nextInt(chr1.getNumGenes() - 1);
		
		Chromosome offspring = new Chromosome(new ArrayList<Chromosome.Gene>());
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
