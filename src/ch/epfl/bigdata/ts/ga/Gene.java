package ch.epfl.bigdata.ts.ga;


public class Gene {
    private double value;
    String name = "";

    public Gene(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public Gene(double value) {
        this.value = value;
    }

    public Gene(Gene gene) {
        this(gene.name, gene.value);
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String toString() {
        return name + " " + value + " ";
    }
}
