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
import ch.epfl.bigdata.ts.pattern.fitness.Rectangle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Training {

    public static int NUM_OF_CHROMOSOMES = 200;

    public static int DOUBLE_BOT_GENE_BOT1_RANGE = 2;
    public static String fileOut = "doubleBottom.txt";
    public static int ITERATIONS = 10;


    public static void main(String[] args) {

        PrintStream out;

        try {
            out = new PrintStream(new FileOutputStream(fileOut));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        out.println("NUM_OF_CHROMOSOMES = " + NUM_OF_CHROMOSOMES);
        int numOfDays = 30;
        int startMoney = 3000;
        int generationWindow = 5;
        int startData = 0;

        int evalNumOfDays = 12;
        int evalStartMoney = 3000;
        int evalGenerationWindow = 12;
        int evalStartData = 30;

        List<Chromosome> chromosomes = new ArrayList<Chromosome>();
        List<Chromosome> evalResults = new ArrayList<Chromosome>();
        List<Chromosome> trainResults = new ArrayList<Chromosome>();

        //double [] values = {7, 5, 3, 1};
        //addChromosome(chromosomes, values);
        for (int j = 0; j < ITERATIONS; j++) {

            out.println("ITERATION #" + j);
            long startTime = System.currentTimeMillis();

            Random random = new Random(System.currentTimeMillis());
            chromosomes.clear();

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
            // DoubleBottom range
            geneRange.put("a", new Util.Range(0, 1));
            geneRange.put("b", new Util.Range(0, 1));
            geneRange.put("c", new Util.Range(0.1, 0.5));
            geneRange.put("d", new Util.Range(0.1, 0.3));

            //Rectangle range
//        geneRange.put("a", new Util.Range(0, 5));
//        geneRange.put("b", new Util.Range(0, 3));
//        geneRange.put("c", new Util.Range(0.1, 0.5));
//        geneRange.put("d", new Util.Range(0.1, 0.3));


            SelectionMethod selMethod = new RouletteWheelSelection();//new RouletteWheelSelection(); //new RankSelection();
            CrossoverMethod crossMethod = new SinglePointCrossover(); //new SinglePointCrossover(); //TwoPointCrossover(); UniformCrossover();
            MutationMethod mutatMethod = new UniformMutation();

//        Chromosome best = GeneticAlgorithm.run(chromosomes, geneRange, new DoubleBottom(30, 3000, 5, 0), selMethod, crossMethod, mutatMethod);
//        Chromosome best = GeneticAlgorithm.run(chromosomes, geneRange, new DoubleBottom(15, 100, 5, 0), selMethod, crossMethod, mutatMethod);


            out.println("Number of days for training: " + numOfDays + ", starting amount of money: " + startMoney + ", window for training: " + generationWindow + ", startData for trading: " + startData);
            Chromosome best = GeneticAlgorithm.run(chromosomes, geneRange, new Rectangle(numOfDays, startMoney, generationWindow, startData), selMethod, crossMethod, mutatMethod);
//        Chromosome best = GeneticAlgorithm.run(chromosomes, geneRange, new FitnessFunctionTest(), selMethod, crossMethod, mutatMethod);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            trainResults.add(best);
            out.println(best);
            out.println("Fitness: " + best.getFitness());
            out.println("Number of transactions: " + best.getNumberOfTransactions());
            out.println("This took " + duration + " milliseconds");
//        System.out.println(best);
//        System.out.println("Fitness: " + best.getFitness());
//        System.out.println("Number of transactions: " + best.getNumberOfTransactions());
//        System.out.println("This took " + duration + " milliseconds");

            double[] evalGenes = new double[4];

            evalGenes[0] = best.getGenes().get(0).getValue();
            evalGenes[1] = best.getGenes().get(1).getValue();
            evalGenes[2] = best.getGenes().get(2).getValue();
            evalGenes[3] = best.getGenes().get(3).getValue();

            startTime = System.currentTimeMillis();

            best = Evaluate.evaluateChromosome(evalGenes, evalNumOfDays, evalStartMoney, evalGenerationWindow, evalStartData);

            endTime = System.currentTimeMillis();
            duration = endTime - startTime;

            evalResults.add(best);
            out.println("Evaluation phase");
            out.println(best);
            out.println("Fitness: " + best.getFitness());
            out.println("Number of transactions: " + best.getNumberOfTransactions());
            out.println("This took " + duration + " milliseconds");
            out.println("EBD OF ITERATION #" + j);
            out.println();
        }
        double sum = 0;

        for (int i = 0; i < evalResults.size(); i++) {
            sum += evalResults.get(i).getFitness();
        }

        out.println("AVERAGE CHROMOSOME FITNESS FOR " + ITERATIONS + " iterations is: " + sum / evalResults.size());


//        Evaluate.val2[0] = best.getGenes().get(0).getValue();
//        Evaluate.val2[1] = best.getGenes().get(1).getValue();
//        Evaluate.val2[2] = best.getGenes().get(2).getValue();
//        Evaluate.val2[3] = best.getGenes().get(3).getValue();
//        Evaluate.main(null);

        out.close();
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
