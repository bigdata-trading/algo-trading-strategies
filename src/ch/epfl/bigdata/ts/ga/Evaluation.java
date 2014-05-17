package ch.epfl.bigdata.ts.ga;

import ch.epfl.bigdata.ts.dataparser.Utils;
import ch.epfl.bigdata.ts.pattern.fitness.FitnessFunction;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Evaluation extends Thread {

    public static int NUM_OF_CHROMOSOMES = 50;

    private FitnessFunction strategy;
    private List<Chromosome> chrsToEval;
    private long time;

    private Chromosome bestChromosome;

    public Evaluation(FitnessFunction fitnessFunction, List<Chromosome> chrs, long time) {
        this.strategy = fitnessFunction;
        this.chrsToEval = chrs;
        this.time = time;
    }

    public void run() {

        FileWriter out = null;

        try {
            out = new FileWriter(Utils.pathToEvaluation + strategy.getName() + "_evaluation_" + time + ".txt", true);
            out.append("NUM_OF_CHROMOSOMES = " + Training.NUM_OF_CHROMOSOMES + "\n");
            out.append("NUM_OF_GENERATIONS = " + GeneticAlgorithm.NUM_OF_GENERATIONS + "\n");
            out.append("NUM_OF_GENERATIONS_IN_WINDOW = " + GeneticAlgorithm.NUM_OF_GENERATIONS_IN_WINDOW + "\n");
            out.append("WINDOW_INCREASE = " + GeneticAlgorithm.WINDOW_MOVE + "\n");
            out.append("Generations in one window = " + Main.generationWindow + "\n\n");



            long startTime = System.currentTimeMillis();

            List<Chromosome> evalResults = new ArrayList<Chromosome>();

            for (int i = 0; i < chrsToEval.size(); i++) {
                Chromosome chr = evaluateChromosome(chrsToEval.get(i));

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                evalResults.add(chr);

                out.append("Evaluation phase" + "\n");
                out.append(chr + "\n");
                out.append("Fitness: " + chr.getFitness() + "\n");
                out.append("Number of transactions: " + chr.getNumberOfTransactions() + "\n");
                out.append("This took " + duration + " milliseconds" + "\n");
                out.append("END OF ITERATION #" + i + "\n\n");
            }

            double sum = 0;
            double sumTransactions = 0;
            double bestFitness = evalResults.get(0).getFitness();
            bestChromosome = evalResults.get(0);

            for (int i = 0; i < evalResults.size(); i++) {
                sum += evalResults.get(i).getFitness();
                sumTransactions += evalResults.get(i).getNumberOfTransactions();
                if (bestFitness < evalResults.get(i).getFitness()) {
                    bestFitness = evalResults.get(i).getFitness();
                    bestChromosome = evalResults.get(i);
                }
            }

            out.append("AVERAGE CHROMOSOME FITNESS FOR " + chrsToEval.size() + " iterations is: " + sum / evalResults.size() + "\n");
            out.append("AVERAGE CHROMOSOME TRANSACTIONS FOR " + chrsToEval.size() + " iterations is: " + sumTransactions / evalResults.size() + "\n");

            if (out != null) out.close();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public Chromosome evaluateChromosome(Chromosome chromosome) {

        strategy.calcFitness(chromosome, false);

        return chromosome;
    }

    public Chromosome bestChromosome() {
        return bestChromosome;
    }

}
