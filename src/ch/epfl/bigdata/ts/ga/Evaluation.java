package ch.epfl.bigdata.ts.ga;

import ch.epfl.bigdata.ts.pattern.fitness.FitnessFunction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Evaluation extends Thread{

    public static int NUM_OF_CHROMOSOMES = 50;

    private String strategyName;
    private FitnessFunction strategy;
    private List<Chromosome> chrsToEval;

    public Evaluation(String strategyName, FitnessFunction fitnessFunction, List<Chromosome> chrs){
        this.strategy = fitnessFunction;
        this.strategyName = strategyName;
        this.chrsToEval = chrs;
    }

    public void run(){

        PrintStream out = null;

        try {
            out = new PrintStream(new FileOutputStream(strategyName + "_evaluation_" + (new Date()).getTime()) + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        long startTime = System.currentTimeMillis();

        List<Chromosome> evalResults = new ArrayList<Chromosome>();

        for (int i=0; i<chrsToEval.size(); i++) {
            Chromosome chr = evaluateChromosome(chrsToEval.get(i));

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            evalResults.add(chr);

            out.println("Evaluation phase");
            out.println(chr);
            out.println("Fitness: " + chr.getFitness());
            out.println("Number of transactions: " + chr.getNumberOfTransactions());
            out.println("This took " + duration + " milliseconds");
            out.println("EBD OF ITERATION #" + i);
            out.println();
        }

        double sum = 0;

        for (int i = 0; i < evalResults.size(); i++) {
            sum += evalResults.get(i).getFitness();
        }

        out.println("AVERAGE CHROMOSOME FITNESS FOR " + chrsToEval.size() + " iterations is: " + sum / evalResults.size());

        if (out != null) out.close();
    }

    public Chromosome evaluateChromosome(Chromosome chromosome) {

        strategy.calcFitness(chromosome);

        return chromosome;
    }

}
