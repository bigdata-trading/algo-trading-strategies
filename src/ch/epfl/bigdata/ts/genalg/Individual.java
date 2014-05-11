package ch.epfl.bigdata.ts.genalg;

import java.util.Random;

public class Individual{

    private static int NUM_OF_CHROMOSOMES = 200;
    public static int NUM_OF_ITERATIONS = 10;


    public static final int GENE_BOTTOM_1 = 0;
    public static final int GENE_BOTTOM_2 = 1;
    public static final int GENE_PROTECT_SELL_GAIN = 2;
    public static final int GENE_PROTECT_SELL_LOSS = 3;
    public static final int GENE_TREND_STRENGTH = 4;

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




    static Random r = new Random(System.currentTimeMillis());
    //Create a random individual
    public void generateIndividual(){
         for (int i=0; i<Constants.NUMBER_OF_GENES; i++){
             generate_gene(i);
         }
    }

    public void generate_gene(int index){
        switch (index) {
            case 0: genes[0] = Constants.BOT1_MIN + (Constants.BOT1_MAX-Constants.BOT1_MIN)*r.nextDouble();
                break;
            case 1: genes[1] = Constants.TOP_MIN + (Constants.TOP_MAX-Constants.TOP_MIN)*r.nextDouble();
                break;
            case 2: genes[2] = Constants.GAIN_PERCENTAGE_MIN + (Constants.GAIN_PERCENTAGE_MAX-Constants.GAIN_PERCENTAGE_MIN)*r.nextDouble();
                break;
            case 3: genes[3] = Constants.LOSS_PERCENTAGE_MIN + (Constants.LOSS_PERCENTAGE_MAX-Constants.LOSS_PERCENTAGE_MIN)*r.nextDouble();
                break;
        }
    }

    public void reset(){
        bottom1 = -1;
        bottom2 = -1;
        top = -1;

        openPosition = false;

        lastPrice = 0;

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

     public void trade(long time,double price){
        lastPrice = price;

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
            if (bottom1 == -1) {
                bottom1 = lastPrice;
            } else if (top == -1) {
                if (lastPrice < bottom1) {
                    bottom1 = lastPrice;
                } else if ((lastPrice - bottom1) >= genes[GENE_BOTTOM_1]) {
                    top = lastPrice;
                }
            } else if (bottom2 == -1) {
                if (lastPrice > top) {
                    top = lastPrice;
                } else if ((top - lastPrice) >= genes[GENE_BOTTOM_2]) {
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
                    sellLoss = lastPrice - genes[GENE_PROTECT_SELL_LOSS] * avg;
                    sellGain = lastPrice + genes[GENE_PROTECT_SELL_GAIN] * avg;
                    //return 1;
                }
            }
        }

    }

    /*
    public void buy(){
        openPosition = true;
        numberOfShares = (int)Math.floor(amount/price);
        amount -= numberOfShares*price;
    }
    */

    public void sell(){
        openPosition = false;
        amount += numOfShares * lastPrice;
        numOfShares = 0;
        bottom2 = top = -1;
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
