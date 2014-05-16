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

    public DoubleTop(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData, long time) {
        super(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData, time);
    }

    public String getName() {
        return "DoubleTop";
    }

    public FitnessFunction constructorWrapper(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData, long time) {
        return new DoubleTop(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData, time);
    }

    protected int trade(Tick transaction, Chromosome chr, boolean logForViz, StringBuilder vizLog, int order) {

        int toRet = 0;

        if (logForViz) {
            vizLog.append(order + "" + transaction.getTimestamp());
            vizLog.append("," + transaction.getPrice());
        }

        lastPrice = transaction.getPrice();
        sp.calculate(transaction.getTimestamp(), lastPrice);

        int sold = 0;
        int bought = 0;
        long t1ts = -1;
        long bts = -1;
        long t2ts = -1;

        if (first) {
            numOfShares = (int) Math.floor(startingAmountOfMoney / lastPrice);
            amount = startingAmountOfMoney - numOfShares * lastPrice;
            first = false;
        }

        if (openPosition) {
            if (lastPrice >= buyLoss) {
                top1 = lastPrice;
                buy();
                toRet = 1;
                t2ts = bts = -1;
                t1ts = transaction.getTimestamp();
                bought = 1;
            } else if (lastPrice <= buyGain) {
                top1 = top2;
                buy();
                toRet = 1;
                t2ts = bts = -1;
                if ((top1 - lastPrice) >= chr.getGenes().get(GENE_TOP_1).getValue()) {
                    bottom = lastPrice;
                    bts = transaction.getTimestamp();
                }
                t1ts = transaction.getTimestamp();
                bought = 1;
            }
        } else {
            if (top1 == -1) {
                if (sp.getTrendStrength() >= chr.getGenes().get(GENE_TREND_STRENGTH).getValue()) {
                    top1 = lastPrice;
                    t1ts = transaction.getTimestamp();
                }
            } else if (bottom == -1) {
                if (lastPrice > top1) {
                    top1 = lastPrice;
                    t1ts = transaction.getTimestamp();
                } else if ((top1 - lastPrice) >= chr.getGenes().get(GENE_TOP_1).getValue()) {
                    bottom = lastPrice;
                    bts = transaction.getTimestamp();
                }
            } else if (top2 == -1) {
                if (lastPrice < bottom) {
                    bottom = lastPrice;
                    bts = transaction.getTimestamp();
                } else if ((lastPrice - bottom) >= chr.getGenes().get(GENE_TOP_2).getValue()) {
                    top2 = lastPrice;
                    t2ts = transaction.getTimestamp();

                    //sell
                    openPosition = true;
                    sold = 1;
                    toRet = 1;
                    amount += numOfShares * lastPrice;
                    numOfShares = 0;
                    double avg = top1 - bottom + top2 - bottom;
                    avg /= 2;
                    buyLoss = lastPrice + chr.getGenes().get(GENE_PROTECT_BUY_LOSS).getValue() * avg;
                    buyGain = lastPrice - chr.getGenes().get(GENE_PROTECT_BUY_GAIN).getValue() * avg;
                }
            }
        }

        if (logForViz) {
            if (t1ts > 0) vizLog.append("," + order + "" + t1ts);
            else vizLog.append("," + t1ts);

            if (bts > 0) vizLog.append("," + order + "" + bts);
            else vizLog.append("," + bts);

            if (t2ts > 0) vizLog.append("," + order + "" + t2ts);
            else vizLog.append("," + t2ts);
            vizLog.append("," + bought);
            vizLog.append("," + sold);
            vizLog.append("," + buyGain);
            vizLog.append("," + buyLoss);
            if (sold == 1 || bought == 1 || order == 0) {
                vizLog.append("," + amount + "," + numOfShares);
            }
            vizLog.append("\n");
        }
        return toRet;
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

    public static List<Range> getGeneRanges() {
        List<Range> ranges = new LinkedList<Range>();

        ranges.add(new Range(0.1, 1));
        ranges.add(new Range(0.1, 1));
        ranges.add(new Range(0.1, 0.4));
        ranges.add(new Range(0.1, 0.4));
        ranges.add(new Range(0, 50));

        /*ranges.add(new Range(0, 0.3));
        ranges.add(new Range(0, 0.3));
        ranges.add(new Range(0.3, 0.7));
        ranges.add(new Range(0.1, 0.3));
        ranges.add(new Range(20, 50));*/

        return ranges;
    }
}