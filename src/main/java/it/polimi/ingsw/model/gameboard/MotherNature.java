package it.polimi.ingsw.model.gameboard;

import java.util.Random;

public class MotherNature {
    public static final int MAX_NUMBER_OF_ISLANDS = 12;
    private int currentIsland;

    /**
     * This class represents the mather nature pawn
     */
    public MotherNature() {
        this.currentIsland = new Random().nextInt(MAX_NUMBER_OF_ISLANDS - 1);
    }

    /**
     * @return is the current island
     */
    public int getCurrentIsland() {
        return currentIsland;
    }

    /**
     * @param currentIsland is the current island to be set
     */
    public void setCurrentIsland(int currentIsland) {
        this.currentIsland = currentIsland;
    }

}
