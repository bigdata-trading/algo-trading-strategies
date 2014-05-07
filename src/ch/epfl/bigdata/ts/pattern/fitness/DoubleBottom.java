package ch.epfl.bigdata.ts.pattern.fitness;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.dataparser.Utils;
import ch.epfl.bigdata.ts.ga.Chromosome;

import java.io.FileNotFoundException;
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

    private int startingAmountOfMoney;
    private int amount;
    private int numOfShares;

    private int numOfDays;
    private int numOfDaysInGeneration;

    private int startingYear;
    private int startingMonth;
    private int startingDay;
    private int startForData;
    Calendar calendar = new GregorianCalendar();

    private Map<Integer, List<Tick>> data = new HashMap<Integer, List<Tick>>();

    public DoubleBottom(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration) {
        this.numOfDays = numOfDays;
        this.numOfDaysInGeneration = numOfDaysInGeneration;
        this.startingAmountOfMoney = startingAmountOfMoney;
        calendar.set(Utils.STARTING_YEAR, Utils.STARTING_MONTH, Utils.STARTING_DAY);
        this.startingYear = Utils.STARTING_YEAR;
        this.startingMonth = Utils.STARTING_MONTH;
        this.startingDay = Utils.STARTING_DAY;
        this.startForData = 0;
        for (int i = 0; i < numOfDays; i++) {
            try {
                List<Tick> ticks = Utils.readCSV(Utils.dataFileNames[i]);
                data.put(i, ticks);

            } catch (FileNotFoundException e) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public DoubleBottom(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startingDay) {
        // Year 2014, month 1 (Feb), day 21
        // numofDays = 18
        this.numOfDays = numOfDays;
        this.numOfDaysInGeneration = numOfDaysInGeneration;
        this.startingAmountOfMoney = startingAmountOfMoney;
        this.startForData = startingDay;
        for (int i = 0; i < numOfDays; i++) {
            try {
                List<Tick> ticks = Utils.readCSV(Utils.dataFileNames[startForData + i]);
                data.put(startForData + i, ticks);

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
            //System.out.println(i);

            List<Tick> ticks1 = data.get(startForData + i);
//            if (ticks1 == null) {
//                continue;
//            }
//            System.out.println("Day "+Utils.SDF.format(calendar.getTime())+", amount "+amount);

            for (Tick tick : ticks1) {
                numberOfTransactions += trade(tick, chr);
            }

            //sell everything
            //sell();
            //bottom1 = -1;
            i++;

        }
//        numberOfTransactions++;
        sell();

        chr.setFitness(amount);
        chr.setNumberOfTransactions(numberOfTransactions);
    }

    @Override
    public void increaseDay() {

        startForData++;
    }


    private int trade(Tick transaction, Chromosome chr) {
        lastPrice = transaction.getPrice();

        if (openPosition) {
            if (lastPrice <= sellLoss) {
                bottom1 = lastPrice;
                sell();
                return 1;
            } else if (lastPrice >= sellGain) {
                bottom1 = bottom2;
                sell();
                return 1;
            }
        } else {
            if (bottom1 == -1) {
                bottom1 = lastPrice;
            } else if (top == -1) {
                if (lastPrice < bottom1) {
                    bottom1 = lastPrice;
                } else if ((lastPrice - bottom1) >= chr.getGenes().get(GENE_BOTTOM_1).getValue()) {
                    top = lastPrice;
                }
            } else if (bottom2 == -1) {
                if (lastPrice > top) {
                    top = lastPrice;
                } else if ((top - lastPrice) >= chr.getGenes().get(GENE_BOTTOM_2).getValue()) {
                    bottom2 = lastPrice;
                }
            } else {
                if (lastPrice < bottom2) {
                    bottom2 = lastPrice;
                } else {
                    //buy
                    openPosition = true;
                    numOfShares = (int) Math.floor(amount / lastPrice);
                    amount -= numOfShares * lastPrice;
                    double avg = top - bottom1 + top - bottom2;
                    avg /= 2;
                    sellLoss = lastPrice - chr.getGenes().get(GENE_PROTECT_SELL_LOSS).getValue() * avg;
                    sellGain = lastPrice + chr.getGenes().get(GENE_PROTECT_SELL_GAIN).getValue() * avg;
                    return 1;
                }
            }
        }
        return 0;
    }

    private void init() {
        bottom1 = -1;
        bottom2 = -1;
        top = -1;

        openPosition = false;

        lastPrice = 0;

        amount = startingAmountOfMoney;
    }

    private void sell() {
        openPosition = false;
        amount += numOfShares * lastPrice;
        numOfShares = 0;
        bottom2 = top = -1;
    }
}