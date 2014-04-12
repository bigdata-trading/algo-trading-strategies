package ch.epfl.bigdata.ts.ga.selection;

import java.util.List;

import ch.epfl.bigdata.ts.ga.Chromosome;

public class RankSelection extends SelectionMethod {	
	public boolean populationSorted() {
		return true;
	}
	
	public List<Chromosome> select(List<Chromosome> population, int n) {		
		int fitness = population.size();
		int sum = fitness * (fitness + 1) / 2;
		for(int i = 0; i < population.size(); i++) {
			population.get(i).setFitnessSelection(fitness--);
		}
		
		return throwMarble(population, n, sum);
	}
}
