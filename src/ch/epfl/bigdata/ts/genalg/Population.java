package ch.epfl.bigdata.ts.genalg;

/**
 * Created by dorwi on 05.04.14.
 */
public class Population{
    Individual[] individuals;

    //create a population
    public Population(int populationSize,boolean initialise){
        individuals = new Individual[populationSize];
        //initialising population
        if (initialise){
            //Loop and create individuals
            for (int i=0;i<size();i++){
                Individual newIndividual = new Individual();
                newIndividual.generateIndividual();
                saveIndividual(i,newIndividual);
            }
        }
    }

    /* Getters */
    public Individual getIndividual(int index){
        return individuals[index];
    }

    public Individual getFittest() {
        Individual fittest = individuals[0];
        for (int i=1; i<size(); i++){
            if (fittest.getFitness() <= getIndividual(i).getFitness()){
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    /*Public methods*/
    public int size(){
        return individuals.length;
    }

    public void saveIndividual(int index,Individual indiv){
        individuals[index] = indiv;
    }
}
