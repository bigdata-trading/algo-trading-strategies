package ch.epfl.bigdata.ts.ga;

import java.util.*;

import ch.epfl.bigdata.ts.ga.crossover.CrossoverMethod;
import ch.epfl.bigdata.ts.ga.mutation.MutationMethod;
import ch.epfl.bigdata.ts.ga.selection.SelectionMethod;
import ch.epfl.bigdata.ts.ga.util.Range;

public class Population {
	
	private SelectionMethod selMethod;
	private CrossoverMethod crossMethod;
	private MutationMethod mutatMethod;

    private int maxPopulationSize;

	private HashMap<String, Range> geneRange;
	
	private List<Chromosome> population;
	private List<Chromosome> elitePopulation;
	
	private int geneBitsPerChr;
	
	public Population(List<Chromosome> population, SelectionMethod selMethod, CrossoverMethod crossMethod, MutationMethod mutatMethod, HashMap<String, Range> geneRange) {
		//initialize chromosomes before creating Population
		this.population = population;
		this.selMethod = selMethod;
		this.crossMethod = crossMethod;
		this.mutatMethod = mutatMethod;
        this.maxPopulationSize = population.size();
		this.geneRange = geneRange;
		
		geneBitsPerChr = geneRange.size();
	}
	
	public List<Chromosome> getChromosomes() {
		return population;
	}
	
	private void sortPopulation() {
		Collections.sort(population, new Comparator<Chromosome>() {
			public int compare(Chromosome c1, Chromosome c2) {
				if(c1.getFitness() < c2.getFitness()) {
					return 1;
				} else if(c1.getFitness() == c2.getFitness()) {
					return 0;
				} else {
					return -1;
				}
			}
		});
	}
	
	public void selection() {		
		List<Chromosome> newPopulation, newElitePopulation;

        sortPopulation();
        //remove extra chromosomes
        for(int i = population.size() - 1; i >= maxPopulationSize; i--) {
            population.remove(i);
        }

		newPopulation = selMethod.select(population, (int) Math.floor(population.size() * GeneticAlgorithm.SELECTIVITY) - GeneticAlgorithm.NUM_ELITE);
		
		newElitePopulation = new ArrayList<Chromosome>();
		for(int i = 0; i < GeneticAlgorithm.NUM_ELITE; i++) {
			Chromosome cur = population.get(i);
			newPopulation.add(cur);
			newElitePopulation.add(new Chromosome(cur));
		}
		
		population = newPopulation;
		elitePopulation = newElitePopulation;
	}

    private Chromosome tournamentSelection() {
        Chromosome fittest = null;
        for (int i=0; i< GeneticAlgorithm.TOURNAMENT_SIZE; i++){
            int randomId = (int) (Range.R.nextDouble() * population.size());
            Chromosome cur = population.get(randomId);
            if(fittest == null || fittest.getFitness() < cur.getFitness()) {
                fittest = cur;
            }
        }
        return fittest;
    }
	
	public void crossover() {
		List<Chromosome> parents = new ArrayList<Chromosome>();
		int numOfCrossovers = (int) Math.floor(GeneticAlgorithm.CROSSOVER_PROBABILITY * population.size());
		for(int i = 0; i < numOfCrossovers; i++) {
			Chromosome parent = tournamentSelection();
			population.remove(parent);
            parents.add(parent);
		}
		population.addAll(elitePopulation);

		if(parents.size() == 1) {
			population.add(parents.get(0));
		} else {
			for(int i = 0; i < parents.size() - 1; i++) {
				for(int j = i + 1; j < parents.size(); j++) {
					population.add(crossMethod.cross(parents.get(i), parents.get(j)));
				}
			}
		}

		//System.out.println("Size crossover " + population.size());
	}
	
	public void mutation() {
		int numOfGeneBits = population.size() * geneBitsPerChr;
		int numOfMutations = (int) Math.floor(GeneticAlgorithm.MUTATION_PROBABILITY * numOfGeneBits);
		for(int i = 0; i < numOfMutations; i++) {
			int totalGeneBitsPos = Range.R.nextInt(numOfGeneBits);
			int chrPos = totalGeneBitsPos / geneBitsPerChr;
			int geneBitPos = totalGeneBitsPos % geneBitsPerChr;
			
			Chromosome chr = population.get(chrPos);
			if(elitePopulation.contains(chr)) {
				population.add(new Chromosome(chr));
			}

			mutatMethod.mutate(chr, geneBitPos);
		}
	}
	
	public Chromosome bestChromosome() {
		sortPopulation();
		return population.get(0);
	}
}