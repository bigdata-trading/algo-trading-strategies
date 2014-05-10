package ch.epfl.bigdata.ts.pattern.fitness;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.dataparser.Utils;
import ch.epfl.bigdata.ts.ga.Chromosome;

import java.io.FileNotFoundException;
import java.util.*;


public class RandomFirtness extends FitnessFunction {


    private int startForData;
    private boolean openPosition = false;

    private double lastPrice;

    private int startingAmountOfMoney;
    private int amount;
    private int numOfShares;

    private int numOfDays;

    private Random rand = new Random();

    private Map<Integer, List<Tick>> data = new HashMap<Integer, List<Tick>>();


    public RandomFirtness(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        this.numOfDays = numOfDays;
        this.startingAmountOfMoney = startingAmountOfMoney;
        this.startForData = startForData;
        for (int i = 0; i < numOfDays; i++) {
            try {
                List<Tick> ticks = Utils.readCSV(Utils.dataFileNames[startForData + i]);
                data.put(startForData + i, ticks);

            } catch (FileNotFoundException e) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void calcFitness(Chromosome chr) {

        init();

        for (int i = 0; i < numOfDays; i++) {

            List<Tick> ticks1 = data.get(startForData + i);

            for (Tick tick : ticks1) {
              trade(tick, chr, ticks1.size());
            }

            i++;

        }
        sell();
    }


    private void trade(Tick transaction, Chromosome chr, int tickssize) {
        lastPrice = transaction.getPrice();
        int rv = rand.nextInt(tickssize);
        boolean sb = rv <= 25 ? true : false;
        boolean sell = rand.nextBoolean();
        if (openPosition && sell) {
            if (sb) {
                sell();
            }
        } else {
            if (sb) {
                openPosition = true;
                numOfShares = (int) Math.floor(amount / lastPrice);
                amount -= numOfShares * lastPrice;
            }
        }
    }

    private void init() {
        openPosition = false;

        lastPrice = 0;

        amount = startingAmountOfMoney;
    }

    private void sell() {
        openPosition = false;
        amount += numOfShares * lastPrice;
        numOfShares = 0;
    }

    @Override
    public void increaseDay() {

        startForData++;
    }

    @Override
    public FitnessFunction constructorWrapper(int numOfDays, int startingAmountOfMoney, int numOfDaysInGeneration, int startForData) {
        return new RandomFirtness(numOfDays, startingAmountOfMoney, numOfDaysInGeneration, startForData);
    }
}