package ch.epfl.bigdata.ts.ga.crossover;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Range;

public class SinglePointCrossover implements CrossoverMethod {

	public Chromosome cross(Chromosome chr1, Chromosome chr2) {
		double pickFirstChr = Range.R.nextDouble();
		int crossoverPoint = 1 + Range.R.nextInt(chr1.getNumGenesBits() - 1);
		
		Chromosome offspring = new Chromosome(chr1.getNumGenesBits(), chr1.getRangeList());
		if(pickFirstChr < 0.5) {
			//first is chr1
			chr1.copyGenesBits(0, crossoverPoint, offspring);
			chr2.copyGenesBits(crossoverPoint, chr2.getNumGenesBits(), offspring);
		} else {
			//first is chr2
			chr2.copyGenesBits(0, crossoverPoint, offspring);
			chr1.copyGenesBits(crossoverPoint, chr2.getNumGenesBits(), offspring);
		}
		return offspring;
	}
}