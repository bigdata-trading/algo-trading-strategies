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


    private static List<Range> ranges = new LinkedList<Range>();
    {
        ranges.add(new Range(0, 0.4));
        ranges.add(new Range(0, 0.3));
        ranges.add(new Range(0, 0.2));
        ranges.add(new Range(0.4, 1));
        ranges.add(new Range(0.1, 0.4));
        ranges.add(new Range(0, 50));
    }

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
                return 1;
            } else if (lastPrice <= buyGain) {
                buy();
                return 1;
            }
        } else {
            if (shoulder1 == -1) {
                if (sp.getTrendStrength() >= chr.getGeneValue(GENE_TREND_STRENGTH)) {
                    shoulder1 = lastPrice;
                }
            } else if (bottom1 == -1) {
                if (lastPrice > shoulder1) {
                    shoulder1 = lastPrice;
                } else if (shoulder1 - lastPrice >= chr.getGeneValue(GENE_BOTTOM_SHOULDER)) {
                    bottom1 = lastPrice;
                    bottom1Ts = lastTs;
                }
            } else if(head == -1) {
                if(lastPrice < bottom1) {
                    bottom1 = lastPrice;
                    bottom1Ts = lastTs;
                } else if(lastPrice - shoulder1 >= chr.getGeneValue(GENE_SHOULDER_HEAD)) {
                    head = lastPrice;
                }
            } else if(bottom2 == -1) {
                if(lastPrice > head) {
                    head = lastPrice;
                } else if(lastPrice >= bottom1 && lastPrice - bottom1 <= chr.getGeneValue(GENE_MAX_DIFF_BOTTOMS)) {
                    bottom2 = lastPrice;
                    bottom2Ts = lastTs;
                } else {
                    initPattern();
                }
            } else if(shoulder2 == -1) {
                if(lastPrice < bottom2) {
                    if(lastPrice >= bottom1) {
                        bottom2 = lastPrice;
                        bottom2Ts = lastTs;
                    } else {
                        initPattern();
                    }
                } else if(lastPrice - head >= chr.getGeneValue(GENE_SHOULDER_HEAD)) {
                    shoulder2 = lastPrice;
                    necklinea = (bottom2 - bottom1) / (bottom2Ts - bottom1Ts);
                    necklineb = bottom2 - bottom2Ts * necklinea;
                }
            } else {
                    if(lastPrice > shoulder2) {
                        if(lastPrice - head >= chr.getGeneValue(GENE_SHOULDER_HEAD))  {
                            shoulder2 = lastPrice;
                        } else {
                            initPattern();
                        }
                    } else if(lastPrice <= lastTs * necklinea + necklineb) {
                        //sell
                        openPosition = true;
                        amount += numOfShares * lastPrice;
                        numOfShares = 0;
                        double avg = head - (bottom1 + bottom2) / 2;
                        buyLoss = lastPrice + chr.getGeneValue(GENE_PROTECT_BUY_LOSS) * avg;
                        buyGain = lastPrice - chr.getGeneValue(GENE_PROTECT_BUY_GAIN) * avg;
                        return 1;
                    }
            }
        }
        return 0;
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
        return ranges;
    }

    public String getName(){
        return "HeadAndShoulders";
    }
}
