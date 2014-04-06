package ch.epfl.bigdata.ts.genalg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.logging.FileHandler;

/**
 * Created by dorwi on 05.04.14.
 */
public class GA {



    // Create an initial population
    public static Population population = new Population(Constants.MAX_INDIVIDUALS, true);
    public static StockParameters stock_parameters = new StockParameters();


    public static void send_values(long time,double price){

        for (int i=0; i<population.size(); i++){
            population.getIndividual(i).trade(time, price);
        }
        stock_parameters.calculate(time,price);
        //System.out.println(stock_parameters);
    }

    public static void main(String[] args) {

        long time;
        double price;

        for (int j=0;j<=Constants.OVERFIT_VALUE;j++) {
            for (int generationCount = 0; generationCount < Constants.MAX_GENERATIONS; generationCount++) {
                try {
                    for (int i = 0; i < Constants.MAX_INDIVIDUALS; i++) {
                        population.getIndividual(i).reset();
                    }

                    String name = "day_ticks.txt" + generationCount;
                    FileReader file = new FileReader(name);
                    Scanner sc = new Scanner(file);
                    while (sc.hasNext()) {
                        time = sc.nextLong();
                        price = sc.nextDouble();
                        send_values(time, price);
                    }

                    System.out.println("Generation: " + generationCount + " Fittest: " + population.getFittest().getFitness());
                    //System.out.println(population.getFittest());

                    population = Algorithm.evolvePopulation(population);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Individual indiv = population.getFittest();

        System.out.println("The best parameters are");
        System.out.println("Genes:");
        System.out.println(indiv);

        /* testing the profit of the best individual */
        double profit = 0;
        for (int generationCount = 50;generationCount<Constants.MAX_GENERATIONS+50;generationCount++){
            try {
                indiv.reset();
                String name = "day_ticks.txt" + generationCount;
                FileReader file = new FileReader(name);
                Scanner sc = new Scanner(file);
                while (sc.hasNext()){
                    time = sc.nextLong();
                    price = sc.nextDouble();
                    indiv.trade(time,price);
                }

                //System.out.println("Generation: " + generationCount + " Fittest: " + indiv.getFitness());
                profit +=indiv.getFitness() - Constants.STARTING_MONEY;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(profit);
    }
}
