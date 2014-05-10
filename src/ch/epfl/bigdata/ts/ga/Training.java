package ch.epfl.bigdata.ts.ga;

import ch.epfl.bigdata.ts.ga.crossover.CrossoverMethod;
import ch.epfl.bigdata.ts.ga.crossover.SinglePointCrossover;
import ch.epfl.bigdata.ts.ga.mutation.MutationMethod;
import ch.epfl.bigdata.ts.ga.mutation.UniformMutation;
import ch.epfl.bigdata.ts.ga.selection.RouletteWheelSelection;
import ch.epfl.bigdata.ts.ga.selection.SelectionMethod;
import ch.epfl.bigdata.ts.ga.util.Range;
import ch.epfl.bigdata.ts.pattern.fitness.FitnessFunction;
import ch.epfl.bigdata.ts.pattern.fitness.Rectangle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class Training extends Thread {

    private static int NUM_OF_CHROMOSOMES = 200;
    public static int NUM_OF_ITERATIONS = 10;


    private String strategyName;
    private List<Range> range = null;
    private List<Chromosome> bestChromosomes = new LinkedList<Chromosome>();


    private SelectionMethod selMethod = new RouletteWheelSelection();
    private CrossoverMethod crossMethod = new SinglePointCrossover();
    private MutationMethod mutatMethod = new UniformMutation();

    private FitnessFunction fitnessFunction = null;

    public Training(String strategyName, List<Range> range, FitnessFunction fitnessFunction) {
        this.strategyName = strategyName;
        this.range = range;
        this.fitnessFunction = fitnessFunction;
    }

    public void run() {
        PrintStream out = null;

        try {
            out = new PrintStream(new FileOutputStream(strategyName + "_training_" + (new Date()).getTime()) + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        out.println("NUM_OF_CHROMOSOMES = " + NUM_OF_CHROMOSOMES);


        List<Chromosome> chromosomes = new ArrayList<Chromosome>();

        for (int j = 0; j < NUM_OF_ITERATIONS; j++) {

            out.println("ITERATION #" + j);
            long startTime = System.currentTimeMillis();

            Random random = new Random();
            chromosomes.clear();

            for (int i = 0; i < NUM_OF_CHROMOSOMES; i++) {

                List<Gene> genes = new LinkedList<Gene>();
                for (int k=0; k<range.size(); k++) {
                    double lower = range.get(k).getLower();
                    double upper = range.get(k).getUpper();
                    double val = lower + (upper - lower) * random.nextDouble();
                    Gene gene = new Gene(Integer.toString(k), val);
                    genes.add(gene);
                }
                Chromosome chr = new Chromosome(genes);
                chromosomes.add(chr);
            }

            HashMap<String, Range> geneRange = new HashMap<String, Range>();
            for (int k=0; k<range.size(); k++) {
                geneRange.put(Integer.toString(k), range.get(k));
            }

            //out.println("Number of days for training: " + numOfDays + ", starting amount of money: " + startMoney + ", window for training: " + generationWindow + ", startData for trading: " + startData);
            Chromosome bestChromosome = GeneticAlgorithm.run(chromosomes, geneRange, fitnessFunction, selMethod, crossMethod, mutatMethod);
            bestChromosomes.add(bestChromosome);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            out.println(bestChromosome);
            out.println("Fitness: " + bestChromosome.getFitness());
            out.println("Number of transactions: " + bestChromosome.getNumberOfTransactions());
            out.println("This took " + duration + " milliseconds");
        }

        if (out != null) out.close();
    }

    public void setSelectionMethod(SelectionMethod m) {
        selMethod = m;
    }

    public void setCrossoverMethod(CrossoverMethod m) {
        crossMethod = m;
    }

    public void setMutationMethod(MutationMethod m) {
        mutatMethod = m;
    }

    public List<Chromosome> getBestChromosome() {
        return bestChromosomes;
    }

    public FitnessFunction getStrategy() {
        return fitnessFunction;
    }
}
