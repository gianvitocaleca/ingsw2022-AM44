package it.polimi.ingsw;

public class MotherNature {
    private int current_island;

    public MotherNature(int position) {
        this.current_island = position;
    }

    public int getCurrentIsland() {
        return current_island;
    }

    public void setCurrentIsland(int current_island) {
        this.current_island = current_island;
    }

}
