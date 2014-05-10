package ch.epfl.bigdata.ts.ga.mutation;

import ch.epfl.bigdata.ts.ga.Gene;
import ch.epfl.bigdata.ts.ga.util.Range;

public class UniformMutation implements MutationMethod {
	public void mutate(Gene gene, Range range) {
		double mutatedVal = range.getLower() + Range.R.nextDouble() * (range.getUpper() - range.getLower());
		gene.setValue(mutatedVal);
	}
}
