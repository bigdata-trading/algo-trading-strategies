package ch.epfl.bigdata.ts.genalg;

import ch.epfl.bigdata.ts.dataparser.Tick;
import ch.epfl.bigdata.ts.dataparser.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;

/**
 * Created by dorwi on 05.04.14.
 */
public class GA {

    // Create an initial population
    public static Population population = new Population(Constants.MAX_INDIVIDUALS, true);
    //public static StockParameters stock_parameters = new StockParameters();


    public static void send_values(long time, double price) {

        for (int i = 0; i < population.size(); i++) {
            population.getIndividual(i).trade(time, price);
        }
    }

    public static void main(String[] args) {
        double money = 0;
        int NUM_OF_ITERATIONS = 10;

        for (int j = 0; j < NUM_OF_ITERATIONS; j++) {
            System.out.println("ITERATION #" + j);

            for (int generationCount = 0; generationCount < 25; generationCount++) {
                try {
                    for (int i = 0; i < Constants.MAX_INDIVIDUALS; i++) {
                        population.getIndividual(i).reset();
                    }

                    for (int i = 0; i < 5; i++) {
                        List<Tick> ticks = Utils.readCSV(Utils.dataFileNames[generationCount+i]);
                        for (Tick tick : ticks) {
                            send_values(tick.getTimestamp(), tick.getPrice());
                        }
                    }

                    System.out.println("Generation: " + generationCount + " Fittest: " + population.getFittest().getFitness());
                    //System.out.println(population.getFittest());

                    population = Algorithm.evolvePopulation(population);

                } catch (FileNotFoundException e) {
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            Individual indiv = population.getFittest();

            System.out.println("The best parameters are");
            System.out.println("Genes:");
            System.out.println(indiv);



            /* testing the profit of the best individual */

            indiv.reset();
            for (int generationCount = 30; generationCount < 42; generationCount++) {
                try {
                    List<Tick> ticks = Utils.readCSV(Utils.dataFileNames[generationCount]);
                    for (Tick tick : ticks) {
                        indiv.trade(tick.getTimestamp(), tick.getPrice());
                    }

                    //profit += indiv.getFitness() - Constants.STARTING_MONEY;

                } catch (FileNotFoundException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(indiv.getFitness());
            money += indiv.getFitness();
        }
        System.out.println(money/NUM_OF_ITERATIONS);
    }
}
