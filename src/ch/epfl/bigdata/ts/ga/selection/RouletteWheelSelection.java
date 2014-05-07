package ch.epfl.bigdata.ts.ga.selection;

import java.util.List;

import ch.epfl.bigdata.ts.ga.Chromosome;

public class RouletteWheelSelection extends SelectionMethod {
	
	public List<Chromosome> select(List<Chromosome> population, int n) {
		double sum = 0;
		for(int i = 0; i < population.size(); i++) {
            Chromosome chr = population.get(i);
            chr.setFitnessSelection(chr.getFitness());
			sum += population.get(i).getFitnessSelection();
		}
		
		return throwMarble(population, n, sum);
	}

}
