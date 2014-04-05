package ch.epfl.bigdata.ts.genalg;

import java.util.Random;

/**
 * Created by dorwi on 05.04.14.
 */
public class Individual{
    /*
        Genes:
            0. the time interval for tracking the average
                - [600..6000s]
            1. the percentage using on average to generate the buy
                - [50..150%]
            2. percentage for protecting loose
                - [50..150%]
            3. percentage for protecting gain
                - [50..150%]
    */
    static double INTERVAL_MIN = 600;
    static double INTERVAL_MAX = 6000;
    static double AVG_PERCENTAGE_MIN = 0.5;
    static double AVG_PERCENTAGE_MAX = 1.5;
    static double GAIN_PERCENTAGE_MIN = 0.5;
    static double GAIN_PERCENTAGE_MAX = 1.5;
    static double LOSS_PERCENTAGE_MIN = 0.5;
    static double LOSS_PERCENTAGE_MAX = 1.5;

    static int NUMBER_OF_GENES = 4;
    private double[] genes = new double[NUMBER_OF_GENES];
    double amount;

    /*
        variables important for the trading strategy
     */

    boolean openedPosition = false;
    boolean finishedInterval = false;
    boolean calculatedInterval = false;
    double lastPrice;
    double priceBuy;
    long intervalBegin;
    long intervalEnd;
    double avg;
    long count;
    double sum;
    double sellLoss;
    double sellGain;
    long numberOfShares=0;

    //Create a random individual
    public void generateIndividual(){
        Random r = new Random(System.currentTimeMillis());
        genes[0] = INTERVAL_MIN + (INTERVAL_MAX-INTERVAL_MIN)*r.nextDouble();
        genes[1] = AVG_PERCENTAGE_MIN + (AVG_PERCENTAGE_MAX-AVG_PERCENTAGE_MIN)*r.nextDouble();
        genes[2] = GAIN_PERCENTAGE_MIN + (GAIN_PERCENTAGE_MAX-GAIN_PERCENTAGE_MIN)*r.nextDouble();
        genes[3] = LOSS_PERCENTAGE_MIN + (LOSS_PERCENTAGE_MAX-LOSS_PERCENTAGE_MIN)*r.nextDouble();
        //initially 10.000$
        amount = 10000;
    }

    public void reset(){
        amount = 10000;
        numberOfShares = 0;
        openedPosition = false;
        finishedInterval = false;
        calculatedInterval = false;
    }

    /*Getters and setters*/
    public static void setNumberOfGenes(int length){
        NUMBER_OF_GENES = length;
    }

    public double getGene(int index){
        return genes[index];
    }

    public double getAmount(){return amount;}

    public long getNumberOfShares(){return numberOfShares;}

    public double getLastPrice(){return lastPrice;}

    public void setAmount(double value){
        amount = value;
    }

    public void setNumberOfShares(int value){
        numberOfShares = value;
    }

    public void setGene(int index, double value){
        genes[index] = value;
    }

    /*Public methods*/
    public int size(){
        return genes.length;
    }

    public double getFitness(){
        return FitnessCalc.getFitness(this);
    }

    /*
    * The implemented strategy:
     * for genes[0] time listen to prices, then calculate the avg
     * if the price is below genes[1]*avg, then buy with the last price
     * if the price is below genes[2]*avg, then sell
     * if the price is above genes[3]*avg, then sell
    * */
    public void trade(long time,double price){
        lastPrice = price;
        if (openedPosition){
            if (price<sellLoss){
                sell(price);
            }
            if (price>sellGain){
                sell(price);
            }
        } else{
            if (!calculatedInterval){
                calculatedInterval = true;
                finishedInterval = false;
                intervalBegin = time;
                intervalEnd = time + (int)Math.floor(genes[0]);
                count = 1;
                sum = price;
            } else if (finishedInterval){
                if (price<priceBuy){
                    buy(price);
                }
            } else if (time > intervalEnd){
                finishedInterval = true;
                avg = sum/count;
                priceBuy = genes[1]*avg;
            } else {
                count++;
                sum+=price;
            }
        }
    }

    public void buy(double price){
        openedPosition = true;
        sellLoss = genes[2]*avg;
        sellGain = genes[3]*avg;
        numberOfShares = (int)Math.floor(amount/price);
        amount -= numberOfShares*price;
    }

    public void sell(double price){
        openedPosition = false;
        calculatedInterval = false;
        amount += numberOfShares*price;
        numberOfShares = 0;
    }

    @Override
    public String toString(){
        String geneString = "";
        for (int i=0; i<size();i++){
            geneString += getGene(i);
            geneString += " ";
        }
        return geneString;
    }
}
