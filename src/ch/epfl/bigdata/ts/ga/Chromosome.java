package ch.epfl.bigdata.ts.ga;

import ch.epfl.bigdata.ts.ga.util.Range;

import java.util.List;

public class Chromosome {

	private byte[] genesBits;
	private double fitness = 0, fitnessSelection = 0;
    private int numberOfTransactions = 0;

    private List<Range> rangeList;

    public Chromosome(int numGenesBits, List<Range> rangeList) {
        genesBits = new byte[numGenesBits];
        this.rangeList = rangeList;
    }

	public Chromosome(byte[] genesBits, List<Range> rangeList) {
		this.genesBits = genesBits;
        this.rangeList = rangeList;
	}

	public Chromosome(Chromosome chr) {
		genesBits = new byte[chr.genesBits.length];
		for(int i = 0; i < genesBits.length; i++) {
			genesBits[i] = chr.genesBits[i];
		}
        rangeList = chr.rangeList;
	}

    public List<Range> getRangeList() {
        return rangeList;
    }

	public double getFitness() {
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

	public int getNumGenesBits() {
		return genesBits.length;
	}

	public byte[] getGenesBits() {
		return genesBits;
	}

    public void setGeneBit(int ind, byte bit) {
        genesBits[ind] = bit;
    }

    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(int numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

	public void copyGenesBits(int startPoint, int endPoint, Chromosome child) {
		for(int i = startPoint; i < endPoint; i++) {
			child.genesBits[i] = genesBits[i];
		}
	}

    public double getGeneValue(int geneInd) {
        double val = 0;
        int start = geneInd * GeneticAlgorithm.GENE_LENGTH;
        int end = start + GeneticAlgorithm.GENE_LENGTH;

        for (int i = start; i < end; i++) {
            val = val * 2 + genesBits[i];
        }

        Range range = rangeList.get(geneInd);
        return range.getLower() + (range.getUpper() - range.getLower()) * (val / (Math.pow(2, GeneticAlgorithm.GENE_LENGTH) - 1));
    }

	/*public void addGene(Chromosome from, int geneInd) {
        int start = geneInd * GeneticAlgorithm.GENE_LENGTH;
        int end = start + GeneticAlgorithm.GENE_LENGTH;
        for(int i = start; i < end; i++) {
            genesBits[i] = from.genesBits[i];
        }
	}*/

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < genesBits.length / GeneticAlgorithm.GENE_LENGTH; i++) {
			sb.append(getGeneValue(i));
		}
		return sb.toString();
	}
}