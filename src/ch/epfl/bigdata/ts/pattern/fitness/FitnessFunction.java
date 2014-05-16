package ch.epfl.bigdata.ts.pattern.fitness;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.dataparser.Utils;
import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.Population;

public abstract class FitnessFunction {

    protected boolean openPosition = false;
    protected boolean buy = false;
    protected int sell = DO_NOT_SELL;

    public static int DO_NOT_SELL = 11;
    public static int SELL_WITH_LOSS = 22;
    public static int SELL_WITH_GAIN = 33;

    protected double lastPrice;

    protected double startingAmountOfMoney;
    protected double amount;
    protected int numOfShares;

    protected int numOfDays;
    protected int numOfDaysInGeneration;
    protected int startForData;

    protected long time;

    protected StockParameters sp = new StockParameters(true);

    protected Map<Integer, List<Tick>> data = new HashMap<Integer, List<Tick>>();

    public FitnessFunction(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData, long time) {
        // Year 2014, month 1 (Feb), day 21
        // numofDays = 18
        this.numOfDays = numOfDays;
        this.numOfDaysInGeneration = numOfDaysInGeneration;
        this.startingAmountOfMoney = startingAmountOfMoney;
        this.startForData = startForData;
        this.time = time;
        for (int i = 0; i < numOfDays; i++) {
            try {
                List<Tick> ticks = Utils.readCSV(Utils.dataFileNames[this.startForData + i]);
                data.put(this.startForData + i, ticks);

            } catch (FileNotFoundException e) {
                System.out.println("File not found stacktrace: ");
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void evaluate(Population population) {
        List<Chromosome> chrs = population.getChromosomes();
        for (int i = 0; i < chrs.size(); i++) {
            // System.out.println("Started evaluating chr " + i);
            calcFitness(chrs.get(i), false);
            // System.out.println("Finished evaluating chr " + i);
        }
    }

    public void calcFitness(Chromosome chr, boolean logForViz) {

        init();
        int numberOfTransactions = 0;
        StringBuilder vizLog = new StringBuilder("");

        for (int i = 0; i < numOfDaysInGeneration; i++) {

            List<Tick> ticks1 = data.get(startForData + i);

            for (Tick tick : ticks1) {
                numberOfTransactions += trade(tick, chr, logForViz, vizLog, i);
            }
        }

        if (numberOfTransactions == 0) {
            chr.setFitness(0.000001);
        } else {
            chr.setFitness(amount + numOfShares * lastPrice);
        }
        chr.setNumberOfTransactions(numberOfTransactions);

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(Utils.pathToVisualisation + getName() + "_visualisation_" + time + ".csv");
            pw.write(vizLog.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) pw.close();
        }
    }

    public void setStartForData(int startForData) {
        this.startForData = startForData;
    }

    public void increaseDay(int increase) {
        startForData += increase;
    }

    protected abstract void init();

    protected abstract int trade(Tick transaction, Chromosome chr, boolean logForViz, StringBuilder vizLog, int order);

    public abstract String getName();

    public abstract FitnessFunction constructorWrapper(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData, long time);
}
