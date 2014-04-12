package ch.epfl.bigdata.ts.genalg;

/**
 * Created by dorwi on 05.04.14.
 */
public class Constants {

         /* boundaries for parameters */
    public static final double INTERVAL_MIN = 60;
    public static final double INTERVAL_MAX = 140;
    public static final double AVG_PERCENTAGE_MIN = 0.5;
    public static final double AVG_PERCENTAGE_MAX = 1.5;
    public static final double GAIN_PERCENTAGE_MIN = 1;
    public static final double GAIN_PERCENTAGE_MAX = 1.5;
    public static final double LOSS_PERCENTAGE_MIN = 0.5;
    public static final double LOSS_PERCENTAGE_MAX = 1;

               /*GA parameters*/
    public static final double UNIFORM_RATE = 0.5;
    public static final double MUTATION_RATE = 0.015;
    public static final int TOURNAMENT_SIZE = 5;
    public static final boolean ELITISM = true;

        /* population specific parameters */
    public static final int MAX_GENERATIONS = 50;
    public static final int MAX_INDIVIDUALS = 100;
    public static final int NUMBER_OF_GENES = 4;
    public static final int STARTING_MONEY = 100000;

        /* parameters for calculating the stock parameters */
    public static final int STOCK_INTERVAL_LENGTH = 50;

        /* for testing purposes */
    public static final int OVERFIT_VALUE = 0;
}
