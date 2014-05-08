package ch.epfl.bigdata.ts.dataparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Filip, Milos, Alexios
 */
public class Utils {

    public static final String FILE_SUFFIX = "_MSFT_tick.csv";
    public static final String pathToInput = "C:\\Dopolnitelno\\EPFL\\M2\\Big Data\\Projects\\algo-trading-strategies\\data\\";//put the absolute path to the data folder

    public static final int STARTING_YEAR = 2014;
    public static final int STARTING_MONTH = 0;
    public static final int STARTING_DAY = 30;
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

    public static String[] dataFileNames = {"20140130_MSFT_tick.csv",
            "20140131_MSFT_tick.csv",
            "20140203_MSFT_tick.csv",
            "20140204_MSFT_tick.csv",
            "20140205_MSFT_tick.csv",
            "20140206_MSFT_tick.csv",
            "20140207_MSFT_tick.csv",
            "20140210_MSFT_tick.csv",
            "20140211_MSFT_tick.csv",
            "20140212_MSFT_tick.csv",
            "20140213_MSFT_tick.csv",
            "20140214_MSFT_tick.csv",
            "20140218_MSFT_tick.csv",
            "20140219_MSFT_tick.csv",
            "20140220_MSFT_tick.csv",
            "20140221_MSFT_tick.csv",
            "20140224_MSFT_tick.csv",
            "20140225_MSFT_tick.csv",
            "20140226_MSFT_tick.csv",
            "20140227_MSFT_tick.csv",
            "20140228_MSFT_tick.csv",
            "20140303_MSFT_tick.csv",
            "20140304_MSFT_tick.csv",
            "20140305_MSFT_tick.csv",
            "20140306_MSFT_tick.csv",
            "20140307_MSFT_tick.csv",
            "20140310_MSFT_tick.csv",
            "20140311_MSFT_tick.csv",
            "20140312_MSFT_tick.csv",
            "20140313_MSFT_tick.csv",
            "20140314_MSFT_tick.csv",
            "20140317_MSFT_tick.csv",
            "20140318_MSFT_tick.csv",
            "20140319_MSFT_tick.csv",
            "20140320_MSFT_tick.csv",
            "20140321_MSFT_tick.csv",
            "20140324_MSFT_tick.csv",
            "20140325_MSFT_tick.csv",
            "20140326_MSFT_tick.csv",
            "20140327_MSFT_tick.csv",
            "20140328_MSFT_tick.csv",
            "20140331_MSFT_tick.csv"};

    /**
     * @param filename - fileName in data folder
     * @return
     * @throws FileNotFoundException
     */
    public static List<Tick> readCSV(String filename) throws FileNotFoundException {

        List<Tick> result = new ArrayList<Tick>();
        File file = new File(pathToInput + filename);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String data[] = line.split(",");
            Long orderId = Long.parseLong(data[0]);
            Long timestamp = Long.parseLong(data[1]);
            Long numberShares = Long.parseLong(data[3]);
            Long price = Long.parseLong(data[4]);
            result.add(new Tick(orderId, timestamp, numberShares, price));
        }

        Collections.sort(result);

        return result;
    }
}
