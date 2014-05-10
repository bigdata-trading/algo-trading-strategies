package ch.epfl.bigdata.ts.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
	
	private int genesPerChr;
	
	public Population(List<Chromosome> population, SelectionMethod selMethod, CrossoverMethod crossMethod, MutationMethod mutatMethod, HashMap<String, Range> geneRange) {
		//initialize chromosomes before creating Population
		this.population = population;
		this.selMethod = selMethod;
		this.crossMethod = crossMethod;
		this.mutatMethod = mutatMethod;
        this.maxPopulationSize = population.size();
		this.geneRange = geneRange;
		
		genesPerChr = population.get(0).getNumGenes();
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
	
	public void crossover() {
		List<Chromosome> parents = new ArrayList<Chromosome>();
		int numOfCrossovers = (int) Math.floor(GeneticAlgorithm.CROSSOVER_PROBABILITY * population.size());
		for(int i = 0; i < numOfCrossovers; i++) {
			int parent = Range.R.nextInt(population.size());
			parents.add(population.remove(parent));
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
		int numOfGenes = population.size() * genesPerChr;
		int numOfMutations = (int) Math.floor(GeneticAlgorithm.MUTATION_PROBABILITY * numOfGenes);
		for(int i = 0; i < numOfMutations; i++) {
			int totalGenePos = Range.R.nextInt(numOfGenes);
			int chrPos = totalGenePos / genesPerChr;
			int genePos = totalGenePos % genesPerChr;
			
			Chromosome chr = population.get(chrPos);
			if(elitePopulation.contains(chr)) {
				population.add(new Chromosome(chr));
			}
			Gene gene = chr.getGenes().get(genePos);
			mutatMethod.mutate(gene, geneRange.get(gene.getName()));
		}
	}
	
	public Chromosome bestChromosome() {
		sortPopulation();
		return population.get(0);
	}
}
