package ch.epfl.bigdata.ts.ga.selection;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Util;

public abstract class SelectionMethod {
	
	public abstract List<Chromosome> select(List<Chromosome> population, int n);
	
	protected List<Chromosome> throwMarble(List<Chromosome> population, int n, double sum) {
		for(int i = 1; i < population.size(); i++) {
			Chromosome prev = population.get(i - 1), cur = population.get(i);
			cur.setFitnessSelection(cur.getFitnessSelection() + prev.getFitnessSelection());
		}
		
		List<Chromosome> result = new ArrayList<Chromosome>();
		for(int i = 0; i < n; i++) {
			double rVal = Util.r.nextDouble() * sum;
			
			for(int j = 0; j < population.size(); j++) {
				Chromosome cur = population.get(j);
				if(cur.getFitnessSelection() > rVal) {
					result.add(cur);
					break;
				}
			}
			
		}
		return result;
	}
}
