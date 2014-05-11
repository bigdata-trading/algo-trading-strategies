package ch.epfl.bigdata.ts.dataparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by alexis on 5/10/2014.
 */
public class Run3 {


    public static class descComparator implements Comparator<Order> {

        public descComparator() {

        }

        @Override
        public int compare(Order o1, Order o2) {
            if(o1.getPrice() < o2.getPrice())
                return 1;
            else if(o1.getPrice() == o2.getPrice()) {
                if(o1.getTimestamp() <= o2.getTimestamp())
                    return -1;
                else
                    return 1;
            }
            else
                return -1;
        }
    }

    public static class ascComparator implements Comparator<Order> {

        public ascComparator() {

        }

        @Override
        public int compare(Order o1, Order o2) {
            if(o1.getPrice() < o2.getPrice())
                return -1;
            else if(o1.getPrice() == o2.getPrice()) {
                if(o1.getTimestamp() <= o2.getTimestamp())
                    return -1;
                else
                    return 1;
            }
            else
                return 1;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        // full filename
        String filename = "C:\\Users\\alexis\\Dropbox\\EPFL_COURSES\\Big_Data\\20140130_MSFT.csv";

        File file = new File(filename);
        HashMap<Long, Order> buyQueue = new HashMap<Long, Order>();
        HashMap<Long, Order> sellQueue = new HashMap<Long, Order>();

        Scanner scan = new Scanner(file);

        System.out.println(scan.nextLine());

        /*int numberOfDeletesOrCancels = 0;
        int numberOfBuysOrSells = 0;
        int numberOfEorF = 0;
        int numberOfT = 0;
        int i2 = 1;

        while(scan.hasNext()) {
            //System.out.println(i2);
            String value = scan.nextLine();
            String[] cols = value.split(",");

            if(cols[3].charAt(0) == Order.TYPE_PART_EXECUTE || cols[3].charAt(0) == Order.TYPE_FULL_EXECUTE) {
                numberOfEorF++;
            }

            if(cols[3].charAt(0) == Order.TYPE_BUY || cols[3].charAt(0) == Order.TYPE_SELL) {
                numberOfBuysOrSells++;
            }

            if(cols[3].charAt(0) == Order.TYPE_PART_CANCEL || cols[3].charAt(0) == Order.TYPE_FULL_DELETE) {
                numberOfDeletesOrCancels++;
            }

            if(cols[3].charAt(0) == Order.TYPE_NON_ORDER_EXECUTE) {
                numberOfT++;
            }
            i2++;
        }

        System.out.println(numberOfDeletesOrCancels);
        System.out.println(numberOfEorF);
        System.out.println(numberOfBuysOrSells);
        System.out.println(numberOfT);*/

        while(scan.hasNext()) {

            String value = scan.nextLine();
            String[] cols = value.split(",");
            /*System.out.println(Long.parseLong(cols[2]));
            System.out.println(Long.parseLong(cols[0]));
            System.out.println(cols[3].charAt(0));
            System.out.println(Long.parseLong(cols[4]));
            System.out.println(Long.parseLong(cols[5]));*/
            Order order = new Order(Long.parseLong(cols[2]), Long.parseLong(cols[0]), cols[3].charAt(0), Long.parseLong(cols[4]), Long.parseLong(cols[5]), "0");

            if(order.getType() == Order.TYPE_BUY) {
                buyQueue.put(order.getOrderID(), order);
            }
            else if(order.getType() == Order.TYPE_SELL) {
                sellQueue.put(order.getOrderID(), order);
            }
            else if(order.getType() == Order.TYPE_PART_CANCEL) {
                if(buyQueue.containsKey(order.getOrderID())) {
                    long previousNumberOfShares = buyQueue.get(order.getOrderID()).getNumberShares();
                    // we don't want to rehash
                    buyQueue.get(order.getOrderID()).setNumberShares(previousNumberOfShares-order.getNumberShares());
                }
                else if(sellQueue.containsKey(order.getOrderID())) {
                    long previousNumberOfShares = sellQueue.get(order.getOrderID()).getNumberShares();
                    // we don't want to rehash
                    sellQueue.get(order.getOrderID()).setNumberShares(previousNumberOfShares-order.getNumberShares());
                }
            }
            else if(order.getType() == Order.TYPE_FULL_DELETE) {
                if(buyQueue.containsKey(order.getOrderID())) {
                    buyQueue.remove(order.getOrderID());
                }
                else if(sellQueue.containsKey(order.getOrderID())) {
                    sellQueue.remove(order.getOrderID());
                }
            }
            else if(order.getType() == Order.TYPE_PART_EXECUTE || order.getType() == Order.TYPE_FULL_EXECUTE) {
                // here are the sel queue and the buy queue before the transaction happens
                List<Order> buys = new ArrayList<Order>(buyQueue.values());
                List<Order> sells =  new ArrayList<Order>(sellQueue.values());

                //System.out.println(order.getTimestamp() + " " + order.getOrderID() + " Buy size " + buys.size());
                //System.out.println(order.getTimestamp() + " " + order.getOrderID() + " Sell size " + sells.size());


                Collections.sort(buys, new descComparator());
                Collections.sort(sells, new ascComparator());

                Order currentBuy = buys.get(0);
                Order currentSell = sells.get(0);
                //System.out.println("Buy price " + currentBuy.getPrice());
                //System.out.println("Sell price " + currentSell.getPrice());
                //System.out.println();

                if(currentBuy.getNumberShares() == currentSell.getNumberShares()) {
                    //remove both of them
                    buyQueue.remove(currentBuy.getOrderID());
                    sellQueue.remove(currentSell.getOrderID());
                }
                else if(currentBuy.getNumberShares() > currentSell.getNumberShares()) {
                    buyQueue.get(currentBuy.getOrderID()).setNumberShares(currentBuy.getNumberShares()- currentSell.getNumberShares());
                    sellQueue.remove(currentSell.getOrderID());
                }
                else if(currentSell.getNumberShares() > currentBuy.getNumberShares()) {
                    sellQueue.get(currentSell.getOrderID()).setNumberShares(currentSell.getNumberShares()- currentBuy.getNumberShares());
                    buyQueue.remove(currentBuy.getOrderID());
                }
            }
            else if(order.getType() == Order.TYPE_NON_ORDER_EXECUTE) {
                // here are the sel queue and the buy queue before the transaction happens
                List<Order> buys = new ArrayList<Order>(buyQueue.values());
                List<Order> sells =  new ArrayList<Order>(sellQueue.values());

                System.out.println(order.getTimestamp() + " " + order.getOrderID() + " Buy size " + buys.size());
                System.out.println(order.getTimestamp() + " " + order.getOrderID() + " Sell size " + sells.size());


                Collections.sort(buys, new descComparator());
                Collections.sort(sells, new ascComparator());

                Order currentBuy = buys.get(0);
                Order currentSell = sells.get(0);
                System.out.println("Buy price " + currentBuy.getPrice() + " " + currentBuy.getNumberShares());
                System.out.println("T price " + order.getPrice() + " " + order.getNumberShares());
                System.out.println("Sell price " + currentSell.getPrice() + " " + currentSell.getNumberShares());
                System.out.println();
                Thread.sleep(500);
            }
            else if(order.getType() == Order.TYPE_BULK) {
                // here are the sel queue and the buy queue before the transaction happens
                List<Order> buys = new ArrayList<Order>(buyQueue.values());
                List<Order> sells =  new ArrayList<Order>(sellQueue.values());

                System.out.println(order.getTimestamp() + " " + order.getOrderID() + " Buy size " + buys.size());
                System.out.println(order.getTimestamp() + " " + order.getOrderID() + " Sell size " + sells.size());


                Collections.sort(buys, new descComparator());
                Collections.sort(sells, new ascComparator());

                Order currentBuy = buys.get(0);
                Order currentSell = sells.get(0);
                System.out.println("Buy price " + currentBuy.getPrice());
                System.out.println("X price " + order.getPrice());
                System.out.println("Sell price " + currentSell.getPrice());
                System.out.println();
                Thread.sleep(500);
            }
        }
    }
}
