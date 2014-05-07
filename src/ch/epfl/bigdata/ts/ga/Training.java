package ch.epfl.bigdata.ts.ga;

import ch.epfl.bigdata.ts.ga.crossover.CrossoverMethod;
import ch.epfl.bigdata.ts.ga.crossover.SinglePointCrossover;
import ch.epfl.bigdata.ts.ga.crossover.TwoPointCrossover;
import ch.epfl.bigdata.ts.ga.crossover.UniformCrossover;
import ch.epfl.bigdata.ts.ga.mutation.MutationMethod;
import ch.epfl.bigdata.ts.ga.mutation.UniformMutation;
import ch.epfl.bigdata.ts.ga.selection.RankSelection;
import ch.epfl.bigdata.ts.ga.selection.RouletteWheelSelection;
import ch.epfl.bigdata.ts.ga.selection.SelectionMethod;
import ch.epfl.bigdata.ts.ga.util.Util;
import ch.epfl.bigdata.ts.pattern.fitness.DoubleBottom;
import ch.epfl.bigdata.ts.pattern.fitness.DoubleTop;
import ch.epfl.bigdata.ts.pattern.fitness.FitnessFunctionTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Training {

    public static int NUM_OF_CHROMOSOMES = 200;

    public static int DOUBLE_BOT_GENE_BOT1_RANGE = 2;

    public static void main(String[] args) {


        List<Chromosome> chromosomes = new ArrayList<Chromosome>();

        //double [] values = {7, 5, 3, 1};
        //addChromosome(chromosomes, values);
        long startTime = System.currentTimeMillis();

        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < NUM_OF_CHROMOSOMES; i++) {

            double bot1 = random.nextDouble();
            double bot2 = random.nextDouble();
            double protSellGain = 0.1 + (0.5 - 0.1) * random.nextDouble();//range between 10 and 50 %
            double protSellLoss = 0.1 + (0.3 - 0.1) * random.nextDouble();//range between 10 and 40 %


            //double [] values0 = {12 + i, 5 + i, 23 + i, 8 + i};
            //addChromosome(chromosomes, values0);

            double[] values = {bot1, bot2, protSellGain, protSellLoss};
            addChromosome(chromosomes, values);
        }

        HashMap<String, Util.Range> geneRange = new HashMap<String, Util.Range>();
        geneRange.put("a", new Util.Range(0, 1));
        geneRange.put("b", new Util.Range(0, 1));
        geneRange.put("c", new Util.Range(0.1, 0.5));
        geneRange.put("d", new Util.Range(0.1, 0.3));

        SelectionMethod selMethod = new RouletteWheelSelection();//new RouletteWheelSelection(); //new RankSelection();
        CrossoverMethod crossMethod = new SinglePointCrossover(); //new SinglePointCrossover(); //TwoPointCrossover(); UniformCrossover();
        MutationMethod mutatMethod = new UniformMutation();

        Chromosome best = GeneticAlgorithm.run(chromosomes, geneRange, new DoubleBottom(25, 3000, 1, 0), selMethod, crossMethod, mutatMethod);
//        Chromosome best = GeneticAlgorithm.run(chromosomes, geneRange, new DoubleTop(15, 100), selMethod, crossMethod, mutatMethod);

//        Chromosome best = GeneticAlgorithm.run(chromosomes, geneRange, new FitnessFunctionTest(), selMethod, crossMethod, mutatMethod);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println(best);
        System.out.println("Fitness: " + best.getFitness());
        System.out.println("Number of transactions: " + best.getNumberOfTransactions());
        System.out.println("This took " + duration + " milliseconds");
        Evaluate.val2[0] = best.getGenes().get(0).getValue();
        Evaluate.val2[1] = best.getGenes().get(1).getValue();
        Evaluate.val2[2] = best.getGenes().get(2).getValue();
        Evaluate.val2[3] = best.getGenes().get(3).getValue();
        Evaluate.main(null);
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
