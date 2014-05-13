package ch.epfl.bigdata.ts.genalg;

/**
 * Created by dorwi on 05.04.14.
 */
import ch.epfl.bigdata.ts.ga.util.Range;

import java.util.LinkedList;
import java.util.List;

public class Constants {

         /* boundaries for parameters */
    public static List<Range> getGeneRanges(){
             List<Range> ranges = new LinkedList<Range>();

             ranges.add(new Range(0, 1));       /* first bottom-top difference */
             ranges.add(new Range(0, 1));       /* second top-bottom difference */
             ranges.add(new Range(0.1, 0.5));   /* percentage of protection gain */
             ranges.add(new Range(0.1, 0.3));   /* percentage of protection loose */
             //ranges.add(new Range(0,10000));    /* the number of ticks to keep bottom1 */
             //ranges.add(new Range(20, 50));

             return ranges;
    }
    public static int numberOfUnits(){
        List<Range> r = getGeneRanges();
        return r.size();
    }
               /*GA parameters*/
    public static final int GENE_LENGTH = 20;
    public static final double UNIFORM_RATE = 0.5;
    public static final double MUTATION_RATE = 0.015;
    public static final int TOURNAMENT_SIZE = 10;
    public static final boolean ELITISM = true;

        /* population specific parameters */
    public static final int MAX_INDIVIDUALS = 200;
    public static final int NUMBER_OF_GENES = GENE_LENGTH*numberOfUnits();
    public static final int STARTING_MONEY = 3000;

        /* parameters for calculating the stock parameters */
    public static final int STOCK_INTERVAL_LENGTH = 50;

        /* for testing purposes */
}
