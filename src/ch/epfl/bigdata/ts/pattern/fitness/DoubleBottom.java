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

    private Map<String, List<Tick>> data = new HashMap<String, List<Tick>>();

    public DoubleBottom(int numOfDays, int startingAmountOfMoney) {
        this.numOfDays = numOfDays;
        this.startingAmountOfMoney = startingAmountOfMoney;
        Calendar calendar = new GregorianCalendar();
        calendar.set(Utils.STARTING_YEAR, Utils.STARTING_MONTH, Utils.STARTING_DAY);
        for (int i = 0; i < numOfDays; ) {
            try {
                List<Tick> ticks = Utils.readCSV(Utils.SDF.format(calendar.getTime()));
                data.put(Utils.SDF.format(calendar.getTime()), ticks);
                i++;

            } catch (FileNotFoundException e) {

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                calendar.add(Calendar.DATE, 1);
            }
        }
    }

    public DoubleBottom(int numOfDays, int startingAmountOfMoney, int startingYear, int startingMonth, int startingDay) {
        // Year 2014, month 1 (Feb), day 21
        // numofDays = 18
        this.numOfDays = numOfDays;
        this.startingAmountOfMoney = startingAmountOfMoney;
        Calendar calendar = new GregorianCalendar();
        calendar.set(startingYear, startingMonth, startingDay);
        for (int i = 0; i < numOfDays; ) {
            try {
                List<Tick> ticks = Utils.readCSV(Utils.SDF.format(calendar.getTime()));
                data.put(Utils.SDF.format(calendar.getTime()), ticks);
                i++;

            } catch (FileNotFoundException e) {

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                calendar.add(Calendar.DATE, 1);
            }
        }
    }

    @Override
    public void calcFitness(Chromosome chr) {

        init();

        Calendar calendar = new GregorianCalendar();
        calendar.set(Utils.STARTING_YEAR, Utils.STARTING_MONTH, Utils.STARTING_DAY);
        for (int i = 0; i < numOfDays; ) {
            //System.out.println(i);

            List<Tick> ticks1 = data.get(Utils.SDF.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
            if (ticks1 == null) {
                continue;
            }
//            System.out.println("Day "+Utils.SDF.format(calendar.getTime())+", amount "+amount);

            for (Tick tick : ticks1) {
                trade(tick, chr);
            }

            //sell everything
            sell();
            bottom1 = -1;
            i++;

        }

        chr.setFitness(amount);
    }


    private void trade(Tick transaction, Chromosome chr) {
        lastPrice = transaction.getPrice();

        if (openPosition) {
            if (lastPrice <= sellLoss) {
                bottom1 = lastPrice;
                sell();
            } else if (lastPrice >= sellGain) {
                bottom1 = bottom2;
                sell();
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

                    //buy
                    openPosition = true;
                    numOfShares = (int) Math.floor(amount / lastPrice);
                    amount -= numOfShares * lastPrice;
                    double avg = top - bottom1 + top - bottom2;
                    avg /= 2;
                    sellLoss = lastPrice - chr.getGenes().get(GENE_PROTECT_SELL_LOSS).getValue() * avg;
                    sellGain = lastPrice + chr.getGenes().get(GENE_PROTECT_SELL_GAIN).getValue() * avg;
                }
            }
        }
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