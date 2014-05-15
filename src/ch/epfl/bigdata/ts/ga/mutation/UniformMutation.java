package ch.epfl.bigdata.ts.ga.mutation;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Range;

public class UniformMutation implements MutationMethod {
	public void mutate(Chromosome chr, int ind) {
		chr.setGeneBit(ind, (byte) Range.R.nextInt(2));
	}
}