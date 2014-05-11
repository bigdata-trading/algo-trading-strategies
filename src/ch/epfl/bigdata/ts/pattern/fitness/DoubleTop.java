package ch.epfl.bigdata.ts.pattern.fitness;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Range;

import java.util.LinkedList;
import java.util.List;

public class DoubleTop extends FitnessFunction {

    public static final int GENE_TOP_1 = 0;
    public static final int GENE_TOP_2 = 1;
    public static final int GENE_PROTECT_BUY_GAIN = 2;
    public static final int GENE_PROTECT_BUY_LOSS = 3;
    public static final int GENE_TREND_STRENGTH = 4;

    private double top1;
    private double top2;
    private double bottom;

    private double buyLoss;
    private double buyGain;

    private boolean first = true;

    public DoubleTop(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        super(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData);
    }

    public String getName() {
        return "DoubleTop";
    }

    public FitnessFunction constructorWrapper(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        return new DoubleTop(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData);
    }

    protected int trade(Tick transaction, Chromosome chr, boolean logForViz, StringBuilder vizLog, int order) {
        lastPrice = transaction.getPrice();
        sp.calculate(transaction.getTimestamp(), lastPrice);

        if (first) {
            numOfShares = (int) Math.floor(startingAmountOfMoney / lastPrice);
            amount -= numOfShares * lastPrice;
            first = false;
        }

        if (openPosition) {
            if (lastPrice >= buyLoss) {
                top1 = lastPrice;
                buy();
                return 1;
            } else if (lastPrice <= buyGain) {
                top1 = top2;
                buy();
                bottom = lastPrice;
                return 1;
            }
        } else {
            if (top1 == -1) {
                if(sp.getTrendStrength() >= chr.getGenes().get(GENE_TREND_STRENGTH).getValue()) {
                    top1 = lastPrice;
                }
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
                    buyLoss = lastPrice + chr.getGenes().get(GENE_PROTECT_BUY_LOSS).getValue() * avg;
                    buyGain = lastPrice - chr.getGenes().get(GENE_PROTECT_BUY_GAIN).getValue() * avg;
                    return 1;
                }
            }
        }
        return 0;
    }

    protected void init() {
        top1 = -1;
        top2 = -1;
        bottom = -1;

        openPosition = false;

        lastPrice = 0;

        amount = startingAmountOfMoney;
        first = true;
    }

    private void buy() {
        openPosition = false;
        numOfShares = (int) Math.floor(amount / lastPrice);
        amount -= numOfShares * lastPrice;
        top2 = bottom = -1;
    }

    public static List<Range> getGeneRanges(){
        List<Range> ranges = new LinkedList<Range>();

        ranges.add(new Range(0, 0.1));
        ranges.add(new Range(0, 0.1));
        ranges.add(new Range(0.1, 0.4));
        ranges.add(new Range(0.1, 0.4));
        ranges.add(new Range(0, 40));

        /*ranges.add(new Range(0, 0.3));
        ranges.add(new Range(0, 0.3));
        ranges.add(new Range(0.3, 0.7));
        ranges.add(new Range(0.1, 0.3));
        ranges.add(new Range(20, 50));*/

        return ranges;
    }
}