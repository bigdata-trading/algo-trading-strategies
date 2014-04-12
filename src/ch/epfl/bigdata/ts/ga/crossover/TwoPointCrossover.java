package ch.epfl.bigdata.ts.ga.crossover;

import java.util.ArrayList;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Util;

public class TwoPointCrossover implements CrossoverMethod {
	public Chromosome cross(Chromosome chr1, Chromosome chr2) {
		double pickFirstChr = Util.r.nextDouble();
		int crossoverPoint1 = 1 + Util.r.nextInt(chr1.getNumGenes() - 1);
		int crossoverPoint2 = 1 + Util.r.nextInt(chr1.getNumGenes() - 1);
		
		while(crossoverPoint2 == crossoverPoint1) {
			crossoverPoint2 = 1 + Util.r.nextInt(chr1.getNumGenes() - 1);
		}
		
		if(crossoverPoint1 > crossoverPoint2) {
			int tmp = crossoverPoint1;
			crossoverPoint1 = crossoverPoint2;
			crossoverPoint2 = tmp;
		}
		
		Chromosome offspring = new Chromosome(new ArrayList<Chromosome.Gene>());
		if(pickFirstChr < 0.5) {
			//first is chr1
			chr1.copyGenes(0, crossoverPoint1, offspring);
			chr2.copyGenes(crossoverPoint1, crossoverPoint2, offspring);
			chr1.copyGenes(crossoverPoint2, chr1.getNumGenes(), offspring);
		} else {
			//first is chr2
			chr2.copyGenes(0, crossoverPoint1, offspring);
			chr1.copyGenes(crossoverPoint1, crossoverPoint2, offspring);
			chr2.copyGenes(crossoverPoint2, chr2.getNumGenes(), offspring);
		}
		return offspring;
	}
}
