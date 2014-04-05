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

    public static int MAX_GENERATIONS = 100;
    public static int MAX_INDIVIDUALS = 50;

    // Create an initial population
    public static Population population = new Population(MAX_INDIVIDUALS, true);


    public static void send_values(long time,double price){
        for (int i=0; i<population.size(); i++){
            population.getIndividual(i).trade(time, price);
        }
    }

    public static void main(String[] args) {

        long time;
        double price;

        for (int generationCount = 0;generationCount<MAX_GENERATIONS;generationCount++){
            try {
                String name = "day_ticks.txt" + generationCount;
                FileReader file = new FileReader(name);
                Scanner sc = new Scanner(file);
                while (sc.hasNext()){
                    time = sc.nextLong();
                    price = sc.nextDouble();
                    send_values(time,price);
                }

                System.out.println("Generation: " + generationCount + " Fittest: " + population.getFittest().getFitness());

                for (int i=0; i<MAX_INDIVIDUALS; i++){
                    population.getIndividual(i).reset();
                }

                population = Algorithm.evolvePopulation(population);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // Evolve our population until we reach an optimum solution
        int generationCount = 0;
        while (generationCount < MAX_GENERATIONS) {
            generationCount++;
        }
        System.out.println("The best parameters are");
        System.out.println("Genes:");
        System.out.println(population.getFittest());

    }
}
