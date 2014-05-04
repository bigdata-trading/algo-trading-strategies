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

    private int startingAmountOfMoney;
    private int amount;
    private int numOfShares;

    private int numOfDays;

    private Map<String, List<Tick>> data = new HashMap<String, List<Tick>>();

    public DoubleTop(int numOfDays, int startingAmountOfShares) {
        this.numOfDays = numOfDays;
        this.numOfShares = startingAmountOfShares;
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

    public DoubleTop(int numOfDays, int startingAmountOfShares, int startingYear, int startingMonth, int startingDay) {
        // Year 2014, month 1 (Feb), day 21
        // numofDays = 18
        this.numOfDays = numOfDays;
        this.numOfShares = startingAmountOfShares;
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

            //buy everything
            //buy();
            //top1 = -1
            i++;

        }
//        buy();

        //the fitness is the amount of money we have and the shares current price
        chr.setFitness(amount + numOfShares * lastPrice);
    }

    private void trade(Tick transaction, Chromosome chr) {
        lastPrice = transaction.getPrice();

        if (openPosition) {
            if (lastPrice <= buyLoss) {
                top1 = lastPrice;
                buy();
            } else if (lastPrice >= buyGain) {
                top1 = top2;
                buy();
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
                }
            }
        }
    }

    private void init() {
        top1 = -1;
        top2 = -1;
        bottom = -1;

        openPosition = false;

        lastPrice = 0;

        amount = startingAmountOfMoney;
    }

    private void buy() {
        openPosition = false;
        numOfShares = (int) Math.floor(amount / lastPrice);
        amount -= numOfShares * lastPrice;
        top2 = bottom = -1;
    }
}
