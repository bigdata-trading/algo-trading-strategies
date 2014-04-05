package ch.epfl.bigdata.ts.genalg;

/**
 * Created by dorwi on 05.04.14.
 */
public class Individual{
    /*
        Genes:
            1. the time interval for tracking the average
            2. the percentage using on average to generate the buy
            3. percentage for protecting loose
            4. percentage for protecting gain
    */
    static int defaultGeneLength = 64;
    private byte[] genes = new byte[defaultGeneLength];
    // Cache
    private int fitness = 0;

    //Create a random individual
    public void generateIndividual(){
        for (int i=0; i<size(); i++){
            byte gene = (byte) Math.round(Math.random());
            genes[i] = gene;
        }
    }

    /*Getters and setters*/
    public static void setDefaultGeneLength(int length){
        defaultGeneLength = length;
    }

    public byte getGene(int index){
        return genes[index];
    }

    public void setGene(int index, byte value){
        genes[index] = value;
        fitness = 0;
    }

    /*Public methods*/
    public int size(){
        return genes.length;
    }

    public int getFitness(){
        if (fitness == 0){
            fitness = FitnessCalc.getFitness(this);
        }
        return fitness;
    }

    @Override
    public String toString(){
        String geneString = "";
        for (int i=0; i<size();i++){
            geneString += getGene(i);
        }
        return geneString;
    }
}
