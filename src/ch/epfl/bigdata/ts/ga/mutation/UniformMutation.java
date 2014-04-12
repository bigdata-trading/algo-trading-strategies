package ch.epfl.bigdata.ts.ga.mutation;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Util;

public class UniformMutation implements MutationMethod {
	public void mutate(Chromosome.Gene gene, Util.Range range) {
		double mutatedVal = range.getLower() + Util.r.nextDouble() * (range.getUpper() - range.getLower());
		gene.setValue(Math.ceil(mutatedVal));
	}
}
