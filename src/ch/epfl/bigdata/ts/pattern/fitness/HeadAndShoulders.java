package ch.epfl.bigdata.ts.pattern.fitness;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Range;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ena, Poly on 5/10/2014.
 */

public class HeadAndShoulders extends FitnessFunction {

    public static final int GENE_BOTTOM_SHOULDER = 0;
    public static final int GENE_SHOULDER_HEAD = 1;
    public static final int GENE_MAX_DIFF_BOTTOMS = 2;
    public static final int GENE_PROTECT_BUY_GAIN = 3;
    public static final int GENE_PROTECT_BUY_LOSS = 4;
    public static final int GENE_TREND_STRENGTH = 5;

    private double shoulder1, shoulder2;
    private double head;
    private double bottom1, bottom2;
    private double bottom1Ts, bottom2Ts;
    private double necklinea, necklineb;

    private double buyLoss;
    private double buyGain;

    private boolean first = true;

    public HeadAndShoulders(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        super(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData);
    }

    public FitnessFunction constructorWrapper(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        return new HeadAndShoulders(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData);
    }

    protected int trade(Tick transaction, Chromosome chr, boolean logForViz, StringBuilder vizLog, int order) {

        int toRet = 0;

        if (logForViz) {
            vizLog.append(order + "" + transaction.getTimestamp());
            vizLog.append("," + transaction.getPrice());
        }


        int sold = 0;
        int bought = 0;
        long sh1ts = -1;
        long b1ts = -1;
        long hts = -1;
        long b2ts = -1;
        long sh2ts = -1;

        lastPrice = transaction.getPrice();
        long lastTs = transaction.getTimestamp();

        sp.calculate(lastTs, lastPrice);
        if (first) {
            numOfShares = (int) Math.floor(startingAmountOfMoney / lastPrice);
            amount -= numOfShares * lastPrice;
            first = false;
        }

        if (openPosition) {
            if (lastPrice >= buyLoss) {
                buy(); //TODO: check if another pattern should start somewhere
                bought = 1;
                sh1ts = b1ts = hts = b2ts = sh2ts = -1;
                toRet = 1;
            } else if (lastPrice <= buyGain) {
                buy();
                sh1ts = b1ts = hts = b2ts = sh2ts = -1;
                toRet = 1;
            }
        } else {
            if (shoulder1 == -1) {
                if (sp.getTrendStrength() >= chr.getGenes().get(GENE_TREND_STRENGTH).getValue()) {
                    shoulder1 = lastPrice;
                    sh1ts = transaction.getTimestamp();
                }
            } else if (bottom1 == -1) {
                if (lastPrice > shoulder1) {
                    shoulder1 = lastPrice;
                    sh1ts = transaction.getTimestamp();
                } else if (shoulder1 - lastPrice >= chr.getGenes().get(GENE_BOTTOM_SHOULDER).getValue()) {
                    bottom1 = lastPrice;
                    bottom1Ts = lastTs;
                    b1ts = transaction.getTimestamp();
                }
            } else if (head == -1) {
                if (lastPrice < bottom1) {
                    bottom1 = lastPrice;
                    bottom1Ts = lastTs;
                    b1ts = transaction.getTimestamp();
                } else if (lastPrice - shoulder1 >= chr.getGenes().get(GENE_SHOULDER_HEAD).getValue()) {
                    head = lastPrice;
                    hts = transaction.getTimestamp();
                }
            } else if (bottom2 == -1) {
                if (lastPrice > head) {
                    head = lastPrice;
                    hts = transaction.getTimestamp();
                } else if (lastPrice >= bottom1 && lastPrice - bottom1 <= chr.getGenes().get(GENE_MAX_DIFF_BOTTOMS).getValue()) {
                    bottom2 = lastPrice;
                    bottom2Ts = lastTs;
                    b2ts = transaction.getTimestamp();
                } else {
                    initPattern();
                }
            } else if (shoulder2 == -1) {
                if (lastPrice < bottom2) {
                    if (lastPrice >= bottom1) {
                        bottom2 = lastPrice;
                        bottom2Ts = lastTs;
                        b2ts = transaction.getTimestamp();
                    } else {
                        initPattern();
                    }
                } else if (lastPrice - head >= chr.getGenes().get(GENE_SHOULDER_HEAD).getValue()) {
                    shoulder2 = lastPrice;
                    necklinea = (bottom2 - bottom1) / (bottom2Ts - bottom1Ts);
                    necklineb = bottom2 - bottom2Ts * necklinea;
                    sh2ts = transaction.getTimestamp();
                }
            } else {
                    if (lastPrice > shoulder2) {
                        if (lastPrice < head) {
                            shoulder2 = lastPrice;
                            sh2ts = transaction.getTimestamp();
                        } else {
                            initPattern();
                        }
                    } else if(lastPrice <= lastTs * necklinea + necklineb) {
                        //sell
                        openPosition = true;
                        amount += numOfShares * lastPrice;
                        numOfShares = 0;
                        double avg = head - (bottom1 + bottom2) / 2;
                        buyLoss = lastPrice + chr.getGenes().get(GENE_PROTECT_BUY_LOSS).getValue() * avg;
                        buyGain = lastPrice - chr.getGenes().get(GENE_PROTECT_BUY_GAIN).getValue() * avg;
                        toRet = 1;
                        sold = 1;
                    }
            }
        }

        if (logForViz) {

            if (sh1ts > 0) vizLog.append("," + order + "" + sh1ts);
            else vizLog.append("," + sh1ts);

            if (b1ts > 0) vizLog.append("," + order + "" + b1ts);
            else vizLog.append("," + b1ts);

            if (hts > 0) vizLog.append("," + order + "" + hts);
            else vizLog.append("," + hts);

            if (b2ts > 0) vizLog.append("," + order + "" + b2ts);
            else vizLog.append("," + b2ts);

            if (sh2ts > 0) vizLog.append("," + order + "" + sh2ts);
            else vizLog.append("," + sh2ts);
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

    private void initPattern() {
        shoulder1 = shoulder2 = -1;
        head = -1;
        bottom1 = bottom2 = -1;
    }

    protected void init() {
        initPattern();

        openPosition = false;

        lastPrice = 0;

        amount = startingAmountOfMoney;
        first = true;
    }

    private void buy() {
        openPosition = false;
        numOfShares = (int) Math.floor(amount / lastPrice);
        amount -= numOfShares * lastPrice;
        initPattern();
    }

    public static List<Range> getGeneRanges() {
        List<Range> ranges = new LinkedList<Range>();

        ranges.add(new Range(0, 0.4));
        ranges.add(new Range(0, 0.3));
        ranges.add(new Range(0, 0.2));
        ranges.add(new Range(0.4, 1));
        ranges.add(new Range(0.1, 0.4));
        ranges.add(new Range(0, 50));

        return ranges;
    }

    public String getName() {
        return "HeadAndShoulders";
    }
}