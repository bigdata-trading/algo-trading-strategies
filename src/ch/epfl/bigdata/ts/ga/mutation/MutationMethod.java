package ch.epfl.bigdata.ts.ga.mutation;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Util;

public interface MutationMethod {
	void mutate(Chromosome.Gene gene, Util.Range range);
}
