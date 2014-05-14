package ch.epfl.bigdata.ts.genalg;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.ga.Chromosome;
import ch.epfl.bigdata.ts.ga.util.Range;
import ch.epfl.bigdata.ts.pattern.fitness.StockParameters;

import java.util.LinkedList;
import java.util.List;

import java.util.Random;

public class Individual{

    public static final int GENE_BOTTOM_1 = 0;
    public static final int GENE_BOTTOM_2 = 1;
    public static final int GENE_PROTECT_SELL_GAIN = 2;
    public static final int GENE_PROTECT_SELL_LOSS = 3;
    public static final int GENE_TREND_STRENGTH = 4;


    protected int sell = DO_NOT_SELL;

    public static int DO_NOT_SELL = 11;
    public static int SELL_WITH_LOSS = 22;
    public static int SELL_WITH_GAIN = 33;

    boolean buy = false;

    private double[] genes = new double[Constants.NUMBER_OF_GENES];

    /*
        variables important for the trading strategy
     */
    private double bottom1;
    private double bottom2;
    private double top;

    private boolean openPosition = false;

    private double lastPrice;

    private double sellLoss;
    private double sellGain;

    private double amount;
    private int numOfShares;
    private int count = 0;


    private StockParameters sp;

    static Random r = new Random(System.currentTimeMillis());
    //Create a random individual
    public void generateIndividual(){
         for (int i=0; i<Constants.NUMBER_OF_GENES; i++){
             generate_gene(i);
         }
    }

    public double transform_gene(int index){
        double v = 0;
        for (int i= index*Constants.GENE_LENGTH; i< (index+1)*Constants.GENE_LENGTH; i++){
            v=v*2+genes[i];
        }
        List<Range> r = Constants.getGeneRanges();
        return r.get(index).getLower() + (r.get(index).getUpper()-r.get(index).getLower())*(v/Math.pow(2,Constants.GENE_LENGTH));
    }

    public void generate_gene(int index){
        genes[index] = r.nextInt(2);
    }


    public void reset(){
        bottom1 = -1;
        bottom2 = -1;
        top = -1;

        openPosition = false;

        lastPrice = 0;

        sellLoss = -1;
        sellGain = -1;

        sp = new StockParameters(true);
        amount = Constants.STARTING_MONEY;
    }

    /*Getters and setters*/
    public double getGene(int index){
        return genes[index];
    }



    public double getLastPrice(){return lastPrice;}

    public void setAmount(double value){
        amount = value;
    }


    public void setGene(int index, double value){
        genes[index] = value;
    }

    /*Public methods*/
    public int size(){
        return genes.length;
    }

    public double getFitness(){
        return amount + numOfShares*lastPrice;
    }


    /*
    * The implemented strategy:
     * for genes[0] time listen to prices, then calculate the avg
     * if the price is below genes[1]*avg, then buy with the last price
     * if the price is below genes[2]*avg, then sell
     * if the price is above genes[3]*avg, then sell
    * */

    protected int trade(Tick transaction/*, Chromosome chr, boolean logForViz, StringBuilder vizLog, int order*/) {

        int toRet = 0;
/*
        if (logForViz) {
            vizLog.append(order + "" + transaction.getTimestamp());
            vizLog.append("," + transaction.getPrice());
        }
*/
        lastPrice = transaction.getPrice();
        sp.calculate(transaction.getTimestamp(), lastPrice);
/*
        int sold = 0;
        int bought = 0;
        long b1ts = -1;
        long b2ts = -1;
        long tts = -1;
*/
        if (sell == SELL_WITH_LOSS){
            sell();
  //          sold = 1;
            toRet = 1;
    //        b2ts = tts = -1;
    //        b1ts = transaction.getTimestamp();
        } else if (sell == SELL_WITH_GAIN){
            double tmp = top;
            sell();
 //           sold = 1;
            toRet = 1;
 //           b2ts = tts = -1;
 //           b1ts = transaction.getTimestamp();
            top = tmp;
        }

        if (openPosition) {
            if (buy) {
                buy = false;
                numOfShares = (int) Math.floor(amount / lastPrice);
                amount -= numOfShares * lastPrice;
                double avg = top - bottom1 + top - bottom2;
                avg /= 2;
                sellLoss = lastPrice - transform_gene(GENE_PROTECT_SELL_LOSS) * avg;
                sellGain = lastPrice + transform_gene(GENE_PROTECT_SELL_GAIN) * avg;
                toRet = 1;
//                bought = 1;

            } else {
                if (lastPrice <= sellLoss) {
                    bottom1 = lastPrice;
                    sell = SELL_WITH_LOSS;

                } else if (lastPrice >= sellGain) {
                    sell = SELL_WITH_GAIN;
                    bottom1 = bottom2;
                    if ((lastPrice - bottom1) >= transform_gene(GENE_BOTTOM_1)) {
                        top = lastPrice;
//                        tts = transaction.getTimestamp();
                    }
                }
            }
        } else {
            if (bottom1 == -1) {
                //if (-sp.getTrendStrength() >= transform_gene(GENE_TREND_STRENGTH)) {
                    bottom1 = lastPrice;
//                    b1ts = transaction.getTimestamp();
                //}
            } else if (top == -1) {
                if (lastPrice < bottom1) {
                    bottom1 = lastPrice;
//                    b1ts = transaction.getTimestamp();
                } else if ((lastPrice - bottom1) >= transform_gene(GENE_BOTTOM_1)) {
                    top = lastPrice;
//                    tts = transaction.getTimestamp();
                }
            } else if (bottom2 == -1) {
                if (lastPrice > top) {
                    top = lastPrice;
//                    tts = transaction.getTimestamp();
                } else if ((top - lastPrice) >= transform_gene(GENE_BOTTOM_2)) {
                    bottom2 = lastPrice;
//                    openPosition = true;
 //                   buy = true;

//                    b2ts = transaction.getTimestamp();
                }
            } else {
                if (lastPrice < bottom2) {
                    bottom2 = lastPrice;
//                    b2ts = transaction.getTimestamp();
                } else {
                    //buy
                    openPosition = true;
                    buy = true;
                }
            }
        }
/*
        if (logForViz) {
            if (b1ts > 0) vizLog.append("," + order + "" + b1ts);
            else vizLog.append("," + b1ts);
            if (b2ts > 0) vizLog.append("," + order + "" + b2ts);
            else vizLog.append("," + b2ts);
            if (tts > 0) vizLog.append("," + order + "" + tts);
            else vizLog.append("," + tts);
            vizLog.append("," + bought);
            vizLog.append("," + sold);
            vizLog.append("," + sellGain);
            vizLog.append("," + sellLoss);
            if (sold == 1 || bought == 1 || order == 0) {
                vizLog.append("," + amount + "," + numOfShares);
            }
            vizLog.append("\n");
        }
*/
        return toRet;
    }

/*
    public void trade(long time,double price){
        lastPrice = price;
        count ++;

        if (openPosition) {
            if (lastPrice <= sellLoss) {
                bottom1 = lastPrice;
                sell();
                //return 1;
            } else if (lastPrice >= sellGain) {
                bottom1 = bottom2;
                sell();
                //return 1;
            }
        } else {
            if ((bottom1 == -1) ){
                bottom1 = lastPrice;
                count = 0;
            } else if (top == -1) {
                if (lastPrice < bottom1) {
                    bottom1 = lastPrice;
                    count = 0;
                } else if ((lastPrice - bottom1) >= transform_gene(GENE_BOTTOM_1)) {
                    top = lastPrice;
                }
            } else if (bottom2 == -1) {
                if (lastPrice > top) {
                    top = lastPrice;
                } else if ((top - lastPrice) >= transform_gene(GENE_BOTTOM_2)) {
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
                    sellLoss = lastPrice - transform_gene(GENE_PROTECT_SELL_LOSS) * avg;
                    sellGain = lastPrice + transform_gene(GENE_PROTECT_SELL_GAIN) * avg;
                    //return 1;
                }
            }
        }

    }
*/
    /*
    public void buy(){
        openPosition = true;
        numberOfShares = (int)Math.floor(amount/price);
        amount -= numberOfShares*price;
    }
    */
private void sell() {
    openPosition = false;
    sell = DO_NOT_SELL;
    amount += numOfShares * lastPrice;
    numOfShares = 0;
    bottom2 = top = -1;

    sellLoss = -1;
    sellGain = -1;
}

    @Override
    public String toString(){
        String geneString = "";
/*
        for (int i=0; i<size();i++){
            geneString += getGene(i);
            geneString += " ";
        }
*/
        for (int i=0; i<Constants.numberOfUnits();i++){
            geneString += transform_gene(i);
            geneString += " ";
        }

        return geneString;
    }
}
