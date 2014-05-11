package ch.epfl.bigdata.ts.pattern.fitness;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Range;

import java.util.*;

public class Rectangle extends FitnessFunction {

    public static final int GENE_DIST_EQUAL_LEVELS = 0;
    public static final int GENE_DIFF_TOPS = 1;
    public static final int GENE_PROTECT_SELL_GAIN = 2;
    public static final int GENE_PROTECT_SELL_LOSS = 3;
    public static final int GENE_TREND_STRENGTH = 4;

    private double bottom1, bottom2;
    private double top1, top2;

    private double sellLoss;
    private double sellGain;

    public Rectangle(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        super(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData);
    }

    public String getName() {
        return "Rectangle";
    }

    public FitnessFunction constructorWrapper(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        return new Rectangle(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData);
    }

    protected int trade(Tick transaction, Chromosome chr, boolean logForViz, StringBuilder vizLog, int order) {
        lastPrice = transaction.getPrice();

        sp.calculate(transaction.getTimestamp(), lastPrice);
        boolean wellEstablishedTrend = sp.getTrendStrength() >= chr.getGenes().get(GENE_TREND_STRENGTH).getValue();

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
                        /*if(wellEstablishedTrend) { //TODO: check if needed
                            top1 = lastPrice;
                        }*/
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

    protected void init() {
        initPattern();

        openPosition = false;
        lastPrice = 0;
        amount = startingAmountOfMoney;
        numOfShares = 0;
    }


    private void sell() {
        openPosition = false;
        amount += numOfShares * lastPrice;
        numOfShares = 0;
        initPattern();
    }

    public static List<Range> getGeneRanges() {
        List<Range> ranges = new LinkedList<Range>();

        ranges.add(new Range(0.25, 0.4));
        ranges.add(new Range(0.3, 0.6));
        ranges.add(new Range(0.5, 1));
        ranges.add(new Range(0.1, 0.4));
        ranges.add(new Range(0, 50));

        return ranges;
    }
}