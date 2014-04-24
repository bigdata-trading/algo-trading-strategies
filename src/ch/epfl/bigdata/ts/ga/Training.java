package ch.epfl.bigdata.ts.ga;

import ch.epfl.bigdata.ts.ga.crossover.CrossoverMethod;
import ch.epfl.bigdata.ts.ga.crossover.SinglePointCrossover;
import ch.epfl.bigdata.ts.ga.mutation.MutationMethod;
import ch.epfl.bigdata.ts.ga.mutation.UniformMutation;
import ch.epfl.bigdata.ts.ga.selection.RouletteWheelSelection;
import ch.epfl.bigdata.ts.ga.selection.SelectionMethod;
import ch.epfl.bigdata.ts.ga.util.Util;
import ch.epfl.bigdata.ts.pattern.fitness.DoubleBottom;
import ch.epfl.bigdata.ts.pattern.fitness.FitnessFunctionTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Training {

    public static int NUM_OF_CHROMOSOMES = 50;

    public static int DOUBLE_BOT_GENE_BOT1_RANGE = 2;

    public static void main(String[] args) {



        List<Chromosome> chromosomes = new ArrayList<Chromosome>();

        //double [] values = {7, 5, 3, 1};
        //addChromosome(chromosomes, values);

        Random random = new Random();

        for(int i = 0; i < NUM_OF_CHROMOSOMES; i++) {

            double bot1 = random.nextDouble() + random.nextDouble();
            double bot2 = random.nextDouble() + random.nextDouble();
            double protSellGain = 0.25 + (0.5 - 0.25)*random.nextDouble();//range between 25 and 50 %
            double protSellLoss = 0.15 + (0.4 - 0.15)*random.nextDouble();//range between 15 and 40 %


            //double [] values0 = {12 + i, 5 + i, 23 + i, 8 + i};
            //addChromosome(chromosomes, values0);

            double [] values = {bot1, bot2, protSellGain, protSellLoss};
            addChromosome(chromosomes, values);
        }

        HashMap<String, Util.Range> geneRange = new HashMap<String, Util.Range>();
        geneRange.put("a", new Util.Range(0, 2));
        geneRange.put("b", new Util.Range(0, 2));
        geneRange.put("c", new Util.Range(0.25,0.5));
        geneRange.put("d", new Util.Range(0.15, 0.4));

        SelectionMethod selMethod = new RouletteWheelSelection(); //new RankSelection();
        CrossoverMethod crossMethod = new SinglePointCrossover(); //TwoPointCrossover(); UniformCrossover();
        MutationMethod mutatMethod = new UniformMutation();

        Chromosome best = GeneticAlgorithm.run(chromosomes, geneRange, new DoubleBottom(15, 3000), selMethod, crossMethod, mutatMethod);
        System.out.println(best);
        System.out.println("Fitness: " + best.getFitness());
    }

    private static void addChromosome(List<Chromosome> chromosomes, double [] values) {
        List<Chromosome.Gene> genes = new ArrayList<Chromosome.Gene>();
        Chromosome chr = new Chromosome(genes);

        genes.add(chr.new Gene("a", values[0]));
        genes.add(chr.new Gene("b", values[1]));
        genes.add(chr.new Gene("c", values[2]));
        genes.add(chr.new Gene("d", values[3]));

        chromosomes.add(chr);
    }
}
