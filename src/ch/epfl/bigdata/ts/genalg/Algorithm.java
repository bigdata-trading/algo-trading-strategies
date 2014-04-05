package ch.epfl.bigdata.ts.genalg;

/**
 * Created by dorwi on 05.04.14.
 */
public class Algorithm{
    /*GA parameters*/
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;

	/* Public methods */

    public static Population evolvePopulation(Population pop){
        Population newPopulation = new Population(pop.size(),false);

        //keep our best individual
        if (elitism){
            newPopulation.saveIndividual(0,pop.getFittest());
        }

        //Crossover population
        int elitismOffset;
        if (elitism){
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        //create new population with crossover
        for (int i = elitismOffset; i<pop.size(); i++){
            Individual indiv1 = tournamentSelection(pop);
            Individual indiv2 = tournamentSelection(pop);
            Individual newIndiv = crossover(indiv1,indiv2);
            newPopulation.saveIndividual(i, newIndiv);
        }

        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    //Crossover individuals
    private static Individual crossover(Individual indiv1,Individual indiv2){
        Individual newSol = new Individual();
        // Loop through genes
        for (int i=0; i< indiv1.size(); i++){
            //Crossover
            if (Math.random() <= uniformRate){
                newSol.setGene(i,indiv1.getGene(i));
            } else {
                newSol.setGene(i,indiv2.getGene(i));
            }
        }
        return newSol;
    }

    //mutate and individual
    private static void mutate(Individual indiv){
        //loop through genes
        for (int i=0; i<indiv.size(); i++){
            if (Math.random() <= mutationRate){
                //create randim gene
                byte gene = (byte) Math.round(Math.random());
                indiv.setGene(i,gene);
            }
        }
    }

    //Select individuals for crossover
    private static Individual tournamentSelection(Population pop){
        //Create a tournament population
        Population tournament = new Population(tournamentSize,false);
        //for each place get a random individual
        for (int i=0; i< tournamentSize; i++){
            int randomId = (int) (Math.random()*pop.size());
            tournament.saveIndividual(i,pop.getIndividual(randomId));
        }
        Individual fittest = tournament.getFittest();
        return fittest;
    }
}