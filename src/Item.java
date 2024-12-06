
public class Item {
    private int id;
    private double weight;
    private double value;

    public Item(int id, double weight, double value) {
        this.id = id;
        this.weight = weight;
        this.value = value;
    }

    public double getWeight() {
        return weight;
    }

    public double getValue() {
        return value;
    }
}
