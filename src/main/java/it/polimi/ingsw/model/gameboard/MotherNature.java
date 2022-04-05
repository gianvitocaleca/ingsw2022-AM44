package it.polimi.ingsw.model.gameboard;

import java.util.Random;

public class MotherNature {
    public static final int MAX_NUMBER_OF_ISLANDS = 12;
    private int currentIsland;

    public MotherNature() {
        this.currentIsland = new Random().nextInt(MAX_NUMBER_OF_ISLANDS-1);
    }

    public int getCurrentIsland() {
        return currentIsland;
    }

    public void setCurrentIsland(int currentIsland) {
        this.currentIsland = currentIsland;
    }

}
