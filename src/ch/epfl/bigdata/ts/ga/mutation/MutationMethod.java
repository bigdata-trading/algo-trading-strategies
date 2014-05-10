package ch.epfl.bigdata.ts.ga.mutation;

import ch.epfl.bigdata.ts.ga.Gene;
import ch.epfl.bigdata.ts.ga.util.Range;

public interface MutationMethod {
	void mutate(Gene gene, Range range);
}
