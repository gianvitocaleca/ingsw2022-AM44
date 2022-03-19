package it.polimi.ingsw;

public class Entrance extends StudentContainer {
    private int capacity;

    public Entrance(int capacity) {
        super();
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
