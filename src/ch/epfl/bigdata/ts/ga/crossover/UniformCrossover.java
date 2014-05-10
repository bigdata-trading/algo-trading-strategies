package ch.epfl.bigdata.ts.ga.crossover;

import java.util.ArrayList;
import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.Gene;
import ch.epfl.bigdata.ts.ga.util.Range;
public class UniformCrossover implements CrossoverMethod {
	public Chromosome cross(Chromosome chr1, Chromosome chr2) {
		Chromosome offspring = new Chromosome(new ArrayList<Gene>());
		for(int i = 0; i < chr1.getNumGenes(); i++) {
			double pickParentGene = Range.R.nextDouble();
			if(pickParentGene < 0.5) {
				//pick chr1's gene
				offspring.addGene(new Gene(chr1.getGenes().get(i)));
			} else {
				//pick chr2's gene
				offspring.addGene(new Gene(chr2.getGenes().get(i)));
			}
		}
		return offspring;
	}
}
