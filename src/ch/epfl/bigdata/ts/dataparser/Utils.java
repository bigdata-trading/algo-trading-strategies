package ch.epfl.bigdata.ts.dataparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author Filip, Milos, Alexios
 */
public class Utils {

    public static final String FILE_SUFFIX = "_MSFT_tick.csv";
    public static final String pathToInput = "";//put the absolute path to the data folder

    /**
     * @param date - format YYYYMMDD
     * @return
     * @throws FileNotFoundException
     */
    public static List<Tick> readCSV(String date) throws FileNotFoundException {

        List<Tick> result = new ArrayList<Tick>();
        File file = new File(pathToInput + date + FILE_SUFFIX);
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
