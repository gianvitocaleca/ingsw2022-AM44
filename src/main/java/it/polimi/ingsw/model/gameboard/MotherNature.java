package it.polimi.ingsw.model.gameboard;

import java.util.Random;

public class MotherNature {
    private int currentIsland;

    public MotherNature() {
        this.currentIsland = new Random().nextInt(11);
    }

    public int getCurrentIsland() {
        return currentIsland;
    }

    public void setCurrentIsland(int currentIsland) {
        this.currentIsland = currentIsland;
    }

}
