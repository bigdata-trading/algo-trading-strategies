package ch.epfl.bigdata.ts.ga.mutation;

import ch.epfl.bigdata.ts.ga.Chromosome;

public interface MutationMethod {
	void mutate(Chromosome chr, int ind);
}
