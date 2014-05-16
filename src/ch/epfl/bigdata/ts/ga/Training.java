package ch.epfl.bigdata.ts.ga;

import ch.epfl.bigdata.ts.dataparser.Utils;
import ch.epfl.bigdata.ts.ga.crossover.CrossoverMethod;
import ch.epfl.bigdata.ts.ga.crossover.SinglePointCrossover;
import ch.epfl.bigdata.ts.ga.mutation.MutationMethod;
import ch.epfl.bigdata.ts.ga.mutation.UniformMutation;
import ch.epfl.bigdata.ts.ga.selection.RouletteWheelSelection;
import ch.epfl.bigdata.ts.ga.selection.SelectionMethod;
import ch.epfl.bigdata.ts.ga.util.Range;
import ch.epfl.bigdata.ts.pattern.fitness.FitnessFunction;
import ch.epfl.bigdata.ts.pattern.fitness.RandomFirtness;
import ch.epfl.bigdata.ts.pattern.fitness.Rectangle;

import java.io.*;
import java.util.*;

public class Training extends Thread {

    private static int NUM_OF_CHROMOSOMES = 100;
    public static int NUM_OF_ITERATIONS = 10;

    private List<Range> range = null;
    private List<Chromosome> bestChromosomes = new LinkedList<Chromosome>();


    private SelectionMethod selMethod = new RouletteWheelSelection();
    private CrossoverMethod crossMethod = new SinglePointCrossover();
    private MutationMethod mutatMethod = new UniformMutation();

    private FitnessFunction fitnessFunction = null;
    private int startForData;

    private long time;

    public Training(List<Range> range, FitnessFunction fitnessFunction, int startForData, long time) {
        this.range = range;
        this.fitnessFunction = fitnessFunction;
        this.startForData = startForData;
        this.time = time;
    }

    public void run() {
        FileWriter out = null;

        try {
            String fn = Utils.pathToTraining + getStrategy().getName() + "_training_" + time + ".txt";
            try {
                out = new FileWriter(fn, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            out.append("NUM_OF_CHROMOSOMES = " + NUM_OF_CHROMOSOMES + "\n");
            out.append("NUM_OF_GENERATIONS = " + GeneticAlgorithm.NUM_OF_GENERATIONS + "\n");
            out.append("NUM_OF_GENERATIONS_IN_WINDOW = " + GeneticAlgorithm.NUM_OF_GENERATIONS_IN_WINDOW + "\n");
            out.append("WINDOW_INCREASE = " + GeneticAlgorithm.WINDOW_MOVE + "\n\n");

            List<Chromosome> chromosomes = new ArrayList<Chromosome>();

            HashMap<String, Range> geneRange = new HashMap<String, Range>();
            if(!(fitnessFunction instanceof RandomFirtness)) {
                for (int k = 0; k < range.size(); k++) {
                    geneRange.put(Integer.toString(k), range.get(k));
                }
            } else {
                geneRange.put("gene1", new Range(0, 1));
                geneRange.put("gene2", new Range(0, 1));
            }

            for (int j = 0; j < NUM_OF_ITERATIONS; j++) {

                out.append("ITERATION #" + j + "\n");
                long startTime = System.currentTimeMillis();

                Random random = new Random();
                chromosomes.clear();

                for (int i = 0; i < NUM_OF_CHROMOSOMES; i++) {
                    List<Gene> genes = new LinkedList<Gene>();
                    if(!(fitnessFunction instanceof RandomFirtness)) {
                        for (int k = 0; k < range.size(); k++) {
                            double lower = range.get(k).getLower();
                            double upper = range.get(k).getUpper();
                            double val = lower + (upper - lower) * random.nextDouble();
                            Gene gene = new Gene(Integer.toString(k), val);
                            genes.add(gene);
                        }
                    } else {
                        genes.add(new Gene("gene1", random.nextDouble()));
                        genes.add(new Gene("gene2", random.nextDouble()));
                    }
                    Chromosome chr = new Chromosome(genes);
                    chromosomes.add(chr);
                }

                fitnessFunction.setStartForData(startForData);

                //out.println("Number of days for training: " + numOfDays + ", starting amount of money: " + startMoney + ", window for training: " + generationWindow + ", startData for trading: " + startData);
                Chromosome bestChromosome = GeneticAlgorithm.run(chromosomes, geneRange, fitnessFunction, selMethod, crossMethod, mutatMethod);
                bestChromosomes.add(bestChromosome);

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                out.append(bestChromosome + "\n");
                out.append("Fitness: " + bestChromosome.getFitness() + "\n");
                out.append("Number of transactions: " + bestChromosome.getNumberOfTransactions() + "\n");
                out.append("This took " + duration + " milliseconds" + "\n");
            }


        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public List<Chromosome> getBestChromosomes() {
        return bestChromosomes;
    }

    public FitnessFunction getStrategy() {
        return fitnessFunction;
    }
}
