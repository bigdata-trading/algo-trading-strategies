package ch.epfl.bigdata.ts.pattern.fitness;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.dataparser.Utils;
import ch.epfl.bigdata.ts.ga.Chromosome;

import java.io.FileNotFoundException;
import java.util.*;


public class DoubleTop extends FitnessFunction {

    public static final int GENE_TOP_1 = 0;
    public static final int GENE_TOP_2 = 1;
    public static final int GENE_PROTECT_BUY_GAIN = 2;
    public static final int GENE_PROTECT_BUY_LOSS = 3;
    public static final int GENE_TREND_STRENGTH = 4;


    private double top1;
    private double top2;
    private double bottom;

    private boolean openPosition = false;

    private double lastPrice;

    private double buyLoss;
    private double buyGain;

    private double startingAmountOfMoney;
    private int startingAmountOfShares;
    private double amount;
    private int numOfShares;

    private int numOfDays;
    private int numOfDaysInGeneration;
    private int startForData;
    private boolean first = false;

    private Map<Integer, List<Tick>> data = new HashMap<Integer, List<Tick>>();

    public DoubleTop(int numOfDays, int startingAmountOfShares, int numOfDaysInGeneration, int startForData) {
        // Year 2014, month 1 (Feb), day 21
        // numofDays = 18
        this.numOfDays = numOfDays;
        this.numOfDaysInGeneration = numOfDaysInGeneration;
        this.startingAmountOfShares = startingAmountOfShares;
        this.startForData = startForData;
        for (int i = 0; i < numOfDays; i++) {
            try {
                List<Tick> ticks = Utils.readCSV(Utils.dataFileNames[this.startForData + i]);
                data.put(this.startForData + i, ticks);

            } catch (FileNotFoundException e) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void calcFitness(Chromosome chr) {

        init();

        int numberOfTransactions = 0;

        for (int i = 0; i < numOfDaysInGeneration; i++) {

            List<Tick> ticks1 = data.get(startForData + i);

            for (Tick tick : ticks1) {
                numberOfTransactions += trade(tick, chr);
            }
        }
//        numberOfTransactions++;
        //buy{}
        double profit = startingAmountOfMoney - amount + numOfShares * lastPrice;

        chr.setFitness(profit);
        chr.setNumberOfTransactions(numberOfTransactions);
    }

    @Override
    public void increaseDay() {
        startForData++;
    }

    @Override
    public FitnessFunction constructorWrapper(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        return new DoubleTop(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData);
    }

    private int trade(Tick transaction, Chromosome chr) {
        lastPrice = transaction.getPrice();
        if (!first) {
            startingAmountOfMoney = numOfShares * lastPrice;
        }

        if (openPosition) {
            if (lastPrice <= buyLoss) {
                top1 = lastPrice;
                buy();
                return 1;
            } else if (lastPrice >= buyGain) {
                top1 = top2;
                buy();
                return 1;
            }
        } else {
            if (top1 == -1) {
                top1 = lastPrice;
            } else if (bottom == -1) {
                if (lastPrice > top1) {
                    top1 = lastPrice;
                } else if ((top1 - lastPrice) >= chr.getGenes().get(GENE_TOP_1).getValue()) {
                    bottom = lastPrice;
                }
            } else if (top2 == -1) {
                if (lastPrice < bottom) {
                    bottom = lastPrice;
                } else if ((lastPrice - bottom) >= chr.getGenes().get(GENE_TOP_2).getValue()) {
                    top2 = lastPrice;

                    //sell
                    openPosition = true;
                    amount += numOfShares * lastPrice;
                    numOfShares = 0;
                    double avg = top1 - bottom + top2 - bottom;
                    avg /= 2;
                    buyLoss = lastPrice - chr.getGenes().get(GENE_PROTECT_BUY_LOSS).getValue() * avg;
                    buyGain = lastPrice + chr.getGenes().get(GENE_PROTECT_BUY_GAIN).getValue() * avg;
                    return 1;
                }
            }
        }
        return 0;
    }

    private void init() {
        top1 = -1;
        top2 = -1;
        bottom = -1;

        openPosition = false;

        lastPrice = 0;

        numOfShares = startingAmountOfShares;
        amount = 0;
        first = false;
    }

    private void buy() {
        openPosition = false;
        numOfShares = (int) Math.floor(amount / lastPrice);
        amount -= numOfShares * lastPrice;
        top2 = bottom = -1;
    }
}
