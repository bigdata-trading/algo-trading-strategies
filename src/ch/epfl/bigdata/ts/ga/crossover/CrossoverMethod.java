package ch.epfl.bigdata.ts.ga.crossover;

import ch.epfl.bigdata.ts.ga.Chromosome;

public interface CrossoverMethod {
	Chromosome cross(Chromosome chr1, Chromosome chr2);
}
