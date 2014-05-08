package ch.epfl.bigdata.ts.pattern.fitness;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.dataparser.Utils;
import ch.epfl.bigdata.ts.ga.Chromosome;

import java.io.FileNotFoundException;
import java.util.*;

public class Rectangle extends FitnessFunction {

    public static final int GENE_DIST_EQUAL_LEVELS = 0;
    public static final int GENE_DIFF_TOPS = 1;
    public static final int GENE_PROTECT_SELL_GAIN = 2;
    public static final int GENE_PROTECT_SELL_LOSS = 3;
    public static final int GENE_TREND_STRENGTH = 4;

    private double bottom1, bottom2;
    private double top1, top2;


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
    private boolean localUpwardTrend, globalUpwardTrend;
    private double localStartPrice, globalStartPrice;
    private int globalNumTx;
    private boolean wellEstablishedTrend;

    public Rectangle(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration) {
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

    public Rectangle(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startingDay) {
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

    public void increaseDay() {
        startForData++;
    }

    private int trade(Tick transaction, Chromosome chr) {
        lastPrice = transaction.getPrice();

        updateTrend();
        if (openPosition) {
            if (lastPrice <= sellLoss) {
                sell();
                return 1;
            } else if (lastPrice >= sellGain) {
                sell();
                if(wellEstablishedTrend) {
                    top1 = lastPrice;
                }
                return 1;
            }
        } else {
            if (top1 == -1) {
                if (wellEstablishedTrend) {
                    top1 = lastPrice;
                }
            } else if (bottom1 == -1) {
                if (wellEstablishedTrend && lastPrice > top1) {
                    top1 = lastPrice;
                } else if (top1 - lastPrice >= chr.getGenes().get(GENE_DIST_EQUAL_LEVELS).getValue()) {
                    bottom1 = lastPrice;
                }
            } else if (top2 == -1) {
                if (lastPrice < bottom1) {
                    bottom1 = lastPrice;
                } else if (Math.abs(top1 - lastPrice) <= chr.getGenes().get(GENE_DIFF_TOPS).getValue()) {
                    top2 = lastPrice;
                }
            } else if (bottom2 == -1) {
                if (lastPrice > top2) {
                    if (Math.abs(top1 - lastPrice) <= chr.getGenes().get(GENE_DIFF_TOPS).getValue()) {
                        top2 = lastPrice;
                    } else {
                        initPattern();
                        if (wellEstablishedTrend) {
                            top1 = lastPrice;
                        }
                    }
                } else if (Math.abs(bottom1 - lastPrice) <= chr.getGenes().get(GENE_DIFF_TOPS).getValue()) {
                    bottom2 = lastPrice;
                }
            } else {
                if (lastPrice < bottom2) {
                    if (Math.abs(bottom2 - lastPrice) <= chr.getGenes().get(GENE_DIFF_TOPS).getValue()) {
                        bottom2 = lastPrice;
                    } else {
                        initPattern();
                        //                    if(wellEstablishedTrend) { //TODO: check if needed
                        //                        top1 = lastPrice;
                        //                    }
                    }
                } else if (lastPrice >= top1 || lastPrice >= top2) {
                    //buy
                    openPosition = true;
                    numOfShares = (int) Math.floor(amount / lastPrice);
                    amount -= numOfShares * lastPrice;
                    double avg = top1 - bottom1 + top2 - bottom2;
                    avg /= 2;
                    sellLoss = lastPrice - chr.getGenes().get(GENE_PROTECT_SELL_LOSS).getValue() * avg;
                    sellGain = lastPrice + chr.getGenes().get(GENE_PROTECT_SELL_GAIN).getValue() * avg;
                    return 1;
                }
            }
        }
        return 0;
    }

    private void initPattern() {
        bottom1 = bottom2 = -1;
        top1 = top2 = -1;
    }

    private void init() {
        initPattern();

        openPosition = false;
        lastPrice = 0;
        amount = startingAmountOfMoney;
        numOfShares = 0;

        globalNumTx = 0;
        localUpwardTrend = globalUpwardTrend = false;
        localStartPrice = globalStartPrice = 0;

        wellEstablishedTrend = false;
    }

    private void updateTrend() {
        wellEstablishedTrend = true;
    }

    private void sell() {
        openPosition = false;
        amount += numOfShares * lastPrice;
        numOfShares = 0;
        initPattern();
    }
}