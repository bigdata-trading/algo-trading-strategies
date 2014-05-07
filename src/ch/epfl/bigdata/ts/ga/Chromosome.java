package ch.epfl.bigdata.ts.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chromosome {
	public class Gene {
		private String name;
		private double value;

		public Gene(String name, double value) {
			this.name = name;
			this.value = value;
		}

		public Gene(Gene gene) {
			this(gene.name, gene.value);
		}

		public String getName() {
			return name;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public String toString() {
			return name + " " + value + " ";
		}
	}

	private List<Gene> genes; //TODO: check if HashMap of genes would be more suitable
	private double fitness = 0, fitnessSelection = 0;
    private int numberOfTransactions=0;

	public Chromosome(List<Gene> genes) { //TODO: organize Gene and Chromosome
		this.genes = genes;
	}

	public Chromosome(Chromosome chr) {
		genes = new ArrayList<Gene>();
		for(int i = 0; i < chr.genes.size(); i++) {
			genes.add(this.new Gene(chr.genes.get(i)));
		}
	}

	public double getFitness() {
        if (numberOfTransactions==0) return 2000*(new Random(System.currentTimeMillis()).nextDouble());
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public double getFitnessSelection() {
		return fitnessSelection;
	}

	public void setFitnessSelection(double fitnessSelection) {
		this.fitnessSelection = fitnessSelection;
	}

	public int getNumGenes() {
		return genes.size();
	}

	public List<Gene> getGenes() {
		return genes;
	}

    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(int numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }


	public void copyGenes(int startPoint, int endPoint, Chromosome child) {
		for(int i = startPoint; i < endPoint; i++) {
			child.genes.add(genes.get(i));
		}
	}

	public void addGene(Gene gene) {
		genes.add(gene);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < genes.size(); i++) {
			sb.append(genes.get(i));
		}
		return sb.toString();
	}
}
