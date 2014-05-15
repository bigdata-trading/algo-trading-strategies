package ch.epfl.bigdata.ts.ga.crossover;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Range;

public class TwoPointCrossover implements CrossoverMethod {
	public Chromosome cross(Chromosome chr1, Chromosome chr2) {
		double pickFirstChr = Range.R.nextDouble();
		int crossoverPoint1 = 1 + Range.R.nextInt(chr1.getNumGenesBits() - 1);
		int crossoverPoint2 = 1 + Range.R.nextInt(chr1.getNumGenesBits() - 1);
		
		while(crossoverPoint2 == crossoverPoint1) {
			crossoverPoint2 = 1 + Range.R.nextInt(chr1.getNumGenesBits() - 1);
		}
		
		if(crossoverPoint1 > crossoverPoint2) {
			int tmp = crossoverPoint1;
			crossoverPoint1 = crossoverPoint2;
			crossoverPoint2 = tmp;
		}
		
		Chromosome offspring = new Chromosome(chr1.getNumGenesBits(), chr1.getRangeList());
		if(pickFirstChr < 0.5) {
			//first is chr1
			chr1.copyGenesBits(0, crossoverPoint1, offspring);
			chr2.copyGenesBits(crossoverPoint1, crossoverPoint2, offspring);
			chr1.copyGenesBits(crossoverPoint2, chr1.getNumGenesBits(), offspring);
		} else {
			//first is chr2
			chr2.copyGenesBits(0, crossoverPoint1, offspring);
			chr1.copyGenesBits(crossoverPoint1, crossoverPoint2, offspring);
			chr2.copyGenesBits(crossoverPoint2, chr2.getNumGenesBits(), offspring);
		}
		return offspring;
	}
}