package ch.epfl.bigdata.ts.ga.crossover;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Range;

public class UniformCrossover implements CrossoverMethod {
	public Chromosome cross(Chromosome chr1, Chromosome chr2) {
		Chromosome offspring = new Chromosome(chr1.getNumGenesBits(), chr1.getRangeList());
		for(int i = 0; i < chr1.getNumGenesBits(); i++) {
			double pickParentGene = Range.R.nextDouble();
			if(pickParentGene < 0.5) {
				//pick chr1's gene
                offspring.setGeneBit(i, chr1.getGenesBits()[i]);
			} else {
				//pick chr2's gene
                offspring.setGeneBit(i, chr2.getGenesBits()[i]);
			}
		}
		return offspring;
	}
}