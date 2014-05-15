package ch.epfl.bigdata.ts.pattern.fitness;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.ga.Chromosome;

import java.util.*;


public class RandomFirtness extends FitnessFunction {

    private Random rand = new Random();

    public RandomFirtness(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData, long time) {
        super(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData, time);
    }

    public void calcFitness(Chromosome chr, boolean logForViz) {

        init();
        int numberOfTransactions = 0;

        for (int i = 0; i < numOfDaysInGeneration; i++) {

            List<Tick> ticks1 = data.get(startForData + i);

            for (Tick tick : ticks1) {
                numberOfTransactions += trade(tick, chr, ticks1.size());
            }

        }

        chr.setFitness(amount + numOfShares * lastPrice);
        chr.setNumberOfTransactions(numberOfTransactions);
    }

    private int trade(Tick transaction, Chromosome chr, int tickssize) {
        lastPrice = transaction.getPrice();
        int rv = rand.nextInt(tickssize);
        boolean sb = rv <= 1 ? true : false;
        boolean sell = rand.nextBoolean();
        if (openPosition && sell) {
            if (sb) {
                sell();
                return 1;
            }
        } else {
            if (sb) {
                openPosition = true;
                numOfShares = (int) Math.floor(amount / lastPrice);
                amount -= numOfShares * lastPrice;
                return 1;
            }
        }

        return 0;
    }

    protected void init() {
        openPosition = false;

        lastPrice = 0;

        amount = startingAmountOfMoney;
    }

    protected int trade(Tick transaction, Chromosome chr, boolean logForViz, StringBuilder vizLog, int order) {
        return 0;
    }

    private void sell() {
        openPosition = false;
        amount += numOfShares * lastPrice;
        numOfShares = 0;
    }

    public String getName() {
        return "Random";
    }

    public FitnessFunction constructorWrapper(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData, long time) {
        return new RandomFirtness(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData, time);
    }
}