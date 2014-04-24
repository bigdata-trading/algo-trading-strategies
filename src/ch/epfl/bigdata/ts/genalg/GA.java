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
    public static StockParameters stock_parameters = new StockParameters();


    public static void send_values(long time, double price) {

        for (int i = 0; i < population.size(); i++) {
            population.getIndividual(i).trade(time, price);
        }
        stock_parameters.calculate(time, price);
        //System.out.println(stock_parameters);
    }

    public static void main(String[] args) {

        long time;
        double price;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = new GregorianCalendar();
        calendar.set(2014, 0, 30);

        for (int j = 0; j <= Constants.OVERFIT_VALUE; j++) {
            for (int generationCount = 0; generationCount < Constants.MAX_GENERATIONS; ) {
                try {
                    for (int i = 0; i < Constants.MAX_INDIVIDUALS; i++) {
                        population.getIndividual(i).reset();
                    }

                    List<Tick> ticks = Utils.readCSV(sdf.format(calendar.getTime()));
                    System.out.println(sdf.format(calendar.getTime()));
                    for (Tick tick : ticks) {
                        send_values(tick.getTimestamp(), tick.getPrice());
                    }
//                    String name = "day_ticks.txt" + generationCount;
//                    FileReader file = new FileReader(name);
//                    Scanner sc = new Scanner(file);
//                    while (sc.hasNext()) {
//                        time = sc.nextLong();
//                        price = sc.nextDouble();
//                        send_values(time, price);
//                    }

                    System.out.println("Generation: " + generationCount + " Fittest: " + population.getFittest().getFitness());
                    //System.out.println(population.getFittest());

                    population = Algorithm.evolvePopulation(population);

                    generationCount++;
                   // System.out.println(sdf.format(calendar.getTime()));

                } catch (FileNotFoundException e) {
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    calendar.add(Calendar.DATE, 1);
                }
            }
        }
        Individual indiv = population.getFittest();

        System.out.println("The best parameters are");
        System.out.println("Genes:");
        System.out.println(indiv);

        /* testing the profit of the best individual */
        double profit = 0;
        for (int generationCount = 21; generationCount < Constants.MAX_GENERATIONS + 12; ) {
            try {
                indiv.reset();
                List<Tick> ticks = Utils.readCSV(sdf.format(calendar.getTime()));
//                System.out.println(sdf.format(calendar.getTime()));
                for (Tick tick : ticks) {
                    indiv.trade(tick.getTimestamp(), tick.getPrice());
                }

//                String name = "day_ticks.txt" + generationCount;
//                FileReader file = new FileReader(name);
//                Scanner sc = new Scanner(file);
//                while (sc.hasNext()){
//                    time = sc.nextLong();
//                    price = sc.nextDouble();
//                    indiv.trade(time,price);
//                }

                //System.out.println("Generation: " + generationCount + " Fittest: " + indiv.getFitness());
                profit += indiv.getFitness() - Constants.STARTING_MONEY;

                generationCount++;

            } catch (FileNotFoundException e) {
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                calendar.add(Calendar.DATE, 1);
            }
        }
        System.out.println(profit);
    }
}
