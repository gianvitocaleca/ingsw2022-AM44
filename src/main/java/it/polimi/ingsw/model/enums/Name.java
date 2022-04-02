package it.polimi.ingsw.model.enums;

public enum Name {
    HERALD(3,1,false,true,false,false),
    KNIGHT(2,1,false,false,false,false),
    CENTAUR(3,1,false,false,false,false),
    FARMER(2,1,false,false,false,false),
    FUNGARO(3,1,true,false,false,false),
    JOKER(1,3,true,false,false,true),
    THIEF(3,1,true,false,false,false),
    MINSTREL(1,2,true,false,false,false), //source: sempre sala, dest sempre entrance
    MONK(1,1,true,false,false,true), //dest= islandindex
    HERBALIST(2,1,false,true,false,false),
    MAGICPOSTMAN(1,1,false,false,true,false),
    PRINCESS(2,1,true,false,false,false);

    private final int cost;
    private final int maxMoves;

    private final boolean needsCreature;
    private final boolean needsIslandIndex;
    private final boolean needsMnMovements;
    private final boolean needsDestination;

    Name(int cost, int maxMoves, boolean needsCreature, boolean needsIslandIndex, boolean needsMnMovements, boolean needsDestination) {
        this.cost = cost;
        this.maxMoves = maxMoves;
        this.needsCreature = needsCreature;
        this.needsIslandIndex = needsIslandIndex;
        this.needsMnMovements = needsMnMovements;
        this.needsDestination = needsDestination;
    }


    public int getCost() {
        return cost;
    }

    public int getMaxMoves() {
        return maxMoves;
    }

    public boolean isNeedsCreature() {
        return needsCreature;
    }

    public boolean isNeedsIslandIndex() {
        return needsIslandIndex;
    }

    public boolean isNeedsMnMovements() {
        return needsMnMovements;
    }

    public boolean isNeedsDestination() {
        return needsDestination;
    }
}
