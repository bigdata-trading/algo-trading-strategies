package ch.epfl.bigdata.ts.dataparser;

/**
 * @author
 */
public class Order implements Comparable<Order> {

    public final static char TYPE_BUY = 'B';
    public final static char TYPE_SELL = 'S';
    public final static char TYPE_PART_EXECUTE = 'E';
    public final static char TYPE_PART_CANCEL = 'C';
    public final static char TYPE_FULL_EXECUTE = 'F';
    public final static char TYPE_FULL_DELETE = 'D';
    public final static char TYPE_BULK = 'X';
    public final static char TYPE_NON_ORDER_EXECUTE = 'T';


    long orderID;
    long timestamp;
    char type;
    long numberShares;
    long price;

    public long getOrderID() {
        return orderID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public char getType() {
        return type;
    }

    public long getNumberShares() {
        return numberShares;
    }

    public long getPrice() {
        return price;
    }

    public Order(long orderID, long timestamp, char type, long numberShares, long price) {
        this.orderID = orderID;
        this.timestamp = timestamp;
        this.type = type;
        this.numberShares = numberShares;
        this.price = price;
    }


    @Override
    public int compareTo(Order o) {
        if (this.timestamp < o.timestamp)
            return -1;
        return 1;
    }

    @Override
    public String toString() {
        return orderID +
                "," + timestamp +
                "," + type +
                "," + numberShares +
                "," + price;
    }
}
