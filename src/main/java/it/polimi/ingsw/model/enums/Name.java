package it.polimi.ingsw.model.enums;

public enum Name {
    HERALD(3, 1, false, true, false, false, false),
    KNIGHT(2, 1, false, false, false, false, false),
    CENTAUR(3, 1, false, false, false, false, false),
    FARMER(2, 1, false, false, false, false, false),
    FUNGARO(3, 1, true, false, false, false, false),
    JOKER(1, 3, true, false, false, false, true),
    THIEF(3, 1, true, false, false, false, false),
    MINSTREL(1, 2, true, false, false, false, true),
    MONK(1, 1, true, false, false, true, false),
    HERBALIST(2, 1, false, true, false, false, false),
    MAGICPOSTMAN(1, 1, false, false, true, false, false),
    PRINCESS(2, 1, true, false, false, false, false);

    private final int cost;
    private final int maxMoves;

    private final boolean needsSourceCreature;
    private final boolean needsIslandIndex;
    private final boolean needsMnMovements;
    private final boolean needsDestination;
    private final boolean needsDestinationCreature;

    Name(int cost, int maxMoves, boolean needsSourceCreature, boolean needsIslandIndex, boolean needsMnMovements, boolean needsDestination, boolean needsDestinationCreature) {
        this.cost = cost;
        this.maxMoves = maxMoves;
        this.needsSourceCreature = needsSourceCreature;
        this.needsIslandIndex = needsIslandIndex;
        this.needsMnMovements = needsMnMovements;
        this.needsDestination = needsDestination;
        this.needsDestinationCreature = needsDestinationCreature;
    }


    public int getCost() {
        return cost;
    }

    public int getMaxMoves() {
        return maxMoves;
    }

    public boolean isNeedsSourceCreature() {
        return needsSourceCreature;
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

    public boolean isNeedsDestinationCreature() {
        return needsDestinationCreature;
    }
}
