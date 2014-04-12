package ch.epfl.bigdata.ts.dataparser;

/**
 * @author FIlip, Milos, Alexios
 */
public class Tick implements Comparable<Tick> {

    long orderID;
    long timestamp;
    char type;
    long numberShares;
    double price;

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

    public double getPrice() {
        return price;
    }

    public Tick(long orderID, long timestamp, char type, long numberShares, long price) {
        this.orderID = orderID;
        this.timestamp = timestamp;
        this.type = type;
        this.numberShares = numberShares;
        this.price = price / 10000;
    }

    public Tick(long orderID, long timestamp, long numberShares, long price) {
        this.orderID = orderID;
        this.timestamp = timestamp;
        this.numberShares = numberShares;
        this.price = price / 10000;
    }


    @Override
    public int compareTo(Tick o) {
        if (this.timestamp < o.timestamp)
            return -1;

        return 1;
    }

    @Override
    public String toString() {
        return timestamp +
                "," + type +
                "," + numberShares +
                "," + price;
    }
}
