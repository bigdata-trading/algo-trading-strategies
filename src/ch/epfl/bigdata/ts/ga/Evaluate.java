package ch.epfl.bigdata.ts.ga;

import ch.epfl.bigdata.ts.ga.crossover.CrossoverMethod;
import ch.epfl.bigdata.ts.ga.crossover.SinglePointCrossover;
import ch.epfl.bigdata.ts.ga.mutation.MutationMethod;
import ch.epfl.bigdata.ts.ga.mutation.UniformMutation;
import ch.epfl.bigdata.ts.ga.selection.RouletteWheelSelection;
import ch.epfl.bigdata.ts.ga.selection.SelectionMethod;
import ch.epfl.bigdata.ts.ga.util.Util;
import ch.epfl.bigdata.ts.pattern.fitness.DoubleBottom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Evaluate {

    public static int NUM_OF_CHROMOSOMES = 50;

    public static int DOUBLE_BOT_GENE_BOT1_RANGE = 2;

    public static void main(String[] args) {


        List<Chromosome> chromosomes = new ArrayList<Chromosome>();

        double [] values = {0.13371815297609935, 0.2504361017996899, 0.16035202077633004, 0.10817642168462034};
        addChromosome(chromosomes, values);
        long startTime = System.currentTimeMillis();

        DoubleBottom doubleBottom = new DoubleBottom(15, 3000, 15, 15);

        doubleBottom.calcFitness(chromosomes.get(0));

        Chromosome best = chromosomes.get(0);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println(best);
        System.out.println("Fitness: " + best.getFitness());
        System.out.println("Number of transactions: " + best.getNumberOfTransactions());
        System.out.println("This took " + duration + " milliseconds");
    }

    private static void addChromosome(List<Chromosome> chromosomes, double[] values) {
        List<Chromosome.Gene> genes = new ArrayList<Chromosome.Gene>();
        Chromosome chr = new Chromosome(genes);

        genes.add(chr.new Gene("a", values[0]));
        genes.add(chr.new Gene("b", values[1]));
        genes.add(chr.new Gene("c", values[2]));
        genes.add(chr.new Gene("d", values[3]));

        chromosomes.add(chr);
    }
}
