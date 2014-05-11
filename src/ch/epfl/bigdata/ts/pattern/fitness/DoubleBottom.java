package ch.epfl.bigdata.ts.pattern.fitness;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.dataparser.Utils;
import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Range;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;


public class DoubleBottom extends FitnessFunction {

    public static final int GENE_BOTTOM_1 = 0;
    public static final int GENE_BOTTOM_2 = 1;
    public static final int GENE_PROTECT_SELL_GAIN = 2;
    public static final int GENE_PROTECT_SELL_LOSS = 3;
    public static final int GENE_TREND_STRENGTH = 4;


    private double bottom1;
    private double bottom2;
    private double top;

    private boolean openPosition = false;

    private double lastPrice;

    private double sellLoss;
    private double sellGain;

    private double startingAmountOfMoney;
    private double amount;
    private int numOfShares;

    private int numOfDays;
    private int numOfDaysInGeneration;

    private int startForData;

    private Map<Integer, List<Tick>> data = new HashMap<Integer, List<Tick>>();


    public DoubleBottom(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        // Year 2014, month 1 (Feb), day 21
        // numofDays = 18
        this.numOfDays = numOfDays;
        this.numOfDaysInGeneration = numOfDaysInGeneration;
        this.startingAmountOfMoney = startingAmountOfMoney;
        this.startForData = startForData;
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

        sell();

        chr.setFitness(amount);
        chr.setNumberOfTransactions(numberOfTransactions);

        PrintWriter pw = null;
        try {
            pw = new PrintWriter("db-eval-log-viz.csv");
            pw.write(vizLog.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) pw.close();
        }


    }

    @Override
    public void increaseDay() {
        startForData++;
    }


    private int trade(Tick transaction, Chromosome chr, boolean logForViz, StringBuilder vizLog, int order) {

        int toRet = 0;

        if (logForViz) {
            vizLog.append(order + "" + transaction.getTimestamp());
            vizLog.append("," + transaction.getPrice());
        }

        lastPrice = transaction.getPrice();
        int sold = 0;
        int bought = 0;
        long b1ts = -1;
        long b2ts = -1;
        long tts = -1;

        if (openPosition) {
            if (lastPrice <= sellLoss) {
                bottom1 = lastPrice;
                sell();
                sold = 1;
                toRet = 1;
                b2ts = tts = -1;
                b1ts = transaction.getTimestamp();
            } else if (lastPrice >= sellGain) {
                bottom1 = bottom2;
                sell();
                sold = 1;
                toRet = 1;
                b2ts = tts = -1;
                b1ts = transaction.getTimestamp();
            }
        } else {
            if (bottom1 == -1) {
                bottom1 = lastPrice;
                b1ts = transaction.getTimestamp();
            } else if (top == -1) {
                if (lastPrice < bottom1) {
                    bottom1 = lastPrice;
                    b1ts = transaction.getTimestamp();
                } else if ((lastPrice - bottom1) >= chr.getGenes().get(GENE_BOTTOM_1).getValue()) {
                    top = lastPrice;
                    tts = transaction.getTimestamp();
                }
            } else if (bottom2 == -1) {
                if (lastPrice > top) {
                    top = lastPrice;
                    tts = transaction.getTimestamp();
                } else if ((top - lastPrice) >= chr.getGenes().get(GENE_BOTTOM_2).getValue()) {
                    bottom2 = lastPrice;
                    b2ts = transaction.getTimestamp();
                }
            } else {
                if (lastPrice < bottom2) {
                    bottom2 = lastPrice;
                    b2ts = transaction.getTimestamp();
                } else {
                    //buy
                    openPosition = true;
                    numOfShares = (int) Math.floor(amount / lastPrice);
                    amount -= numOfShares * lastPrice;
                    double avg = top - bottom1 + top - bottom2;
                    avg /= 2;
                    sellLoss = lastPrice - chr.getGenes().get(GENE_PROTECT_SELL_LOSS).getValue() * avg;
                    sellGain = lastPrice + chr.getGenes().get(GENE_PROTECT_SELL_GAIN).getValue() * avg;
                    toRet = 1;
                    bought = 1;
                }
            }
        }

        if (logForViz) {
            if (b1ts>0) vizLog.append("," + order + "" + b1ts); else vizLog.append(","  + b1ts);
            if (b2ts>0) vizLog.append("," + order + "" + b2ts); else vizLog.append(","  + b2ts);
            if (tts>0) vizLog.append("," + order + "" + tts); else vizLog.append(","  + tts);
            vizLog.append("," + bought);
            vizLog.append("," + sold);
            vizLog.append("," + sellGain);
            vizLog.append("," + sellLoss);
            vizLog.append("\n");
        }

        return  toRet;
    }

    private void init() {
        bottom1 = -1;
        bottom2 = -1;
        top = -1;

        openPosition = false;

        lastPrice = 0;

        amount = startingAmountOfMoney;

        sellLoss = -1;
        sellGain = -1;

    }

    private void sell() {
        openPosition = false;
        amount += numOfShares * lastPrice;
        numOfShares = 0;
        bottom2 = top = -1;

        sellLoss = -1;
        sellGain = -1;
    }


    public static List<Range> getGeneRanges(){
        List<Range> ranges = new LinkedList<Range>();

        ranges.add(new Range(0, 1));
        ranges.add(new Range(0, 1));
        ranges.add(new Range(0.1, 0.5));
        ranges.add(new Range(0.1, 0.3));

        return ranges;
    }


    public String getName(){
        return "DoubleBottom";
    }

    public FitnessFunction constructorWrapper(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData){
        return new DoubleBottom(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData);
    }
}