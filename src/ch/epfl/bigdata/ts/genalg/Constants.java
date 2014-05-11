package ch.epfl.bigdata.ts.genalg;

/**
 * Created by dorwi on 05.04.14.
 */
public class Constants {

         /* boundaries for parameters */
    public static final double BOT1_MIN = 0;
    public static final double BOT1_MAX = 1;
    public static final double TOP_MIN = 0;
    public static final double TOP_MAX = 1;
    public static final double GAIN_PERCENTAGE_MIN = 0.1;
    public static final double GAIN_PERCENTAGE_MAX = 0.5;
    public static final double LOSS_PERCENTAGE_MIN = 0.1;
    public static final double LOSS_PERCENTAGE_MAX = 0.3;

               /*GA parameters*/
    public static final double UNIFORM_RATE = 0.5;
    public static final double MUTATION_RATE = 0.015;
    public static final int TOURNAMENT_SIZE = 5;
    public static final boolean ELITISM = true;

        /* population specific parameters */
    public static final int MAX_GENERATIONS = 20;
    public static final int MAX_INDIVIDUALS = 200;
    public static final int NUMBER_OF_GENES = 4;
    public static final int STARTING_MONEY = 3000;

        /* parameters for calculating the stock parameters */
    public static final int STOCK_INTERVAL_LENGTH = 50;

        /* for testing purposes */
    public static final int OVERFIT_VALUE = 0;
}
