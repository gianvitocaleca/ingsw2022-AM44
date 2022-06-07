package it.polimi.ingsw.server.model.enums;

public enum Name {
    HERALD(3, 1, false, true, false, false, false, false, false,"Characters/herald.jpg"),
    KNIGHT(2, 1, false, false, false, false, false, false, false,"Characters/knight.jpg"),
    CENTAUR(3, 1, false, false, false, false, false, false, false,"Characters/centaur.jpg"),
    FARMER(2, 1, false, false, false, false, false, false, false,"Characters/farmer.jpg"),
    FUNGARO(3, 1, true, false, false, false, false, false, false,"Characters/fungaro.jpg"),
    JOKER(1, 3, true, false, false, false, true, true, false,"Characters/joker.jpg"),
    THIEF(3, 1, true, false, false, false, false, false, true,"Characters/thief.jpg"),
    MINSTREL(1, 2, true, false, false, false, true, true, false,"Characters/minstrel.jpg"),
    MONK(1, 1, true, false, false, true, false, false, true,"Characters/monk.jpg"),
    HERBALIST(2, 1, false, true, false, false, false, false, false,"Characters/herbalist.jpg"),
    MAGICPOSTMAN(1, 1, false, false, true, false, false,false, false, "Characters/magicPostman.jpg"),
    PRINCESS(2, 1, true, false, false, false, false, false, true,"Characters/princess.jpg");

    private final int cost;
    private final int maxMoves;
    private final String image;
    private final boolean needsSourceCreature;
    private final boolean needsIslandIndex;
    private final boolean needsMnMovements;
    private final boolean needsDestination;
    private final boolean needsDestinationCreature;

    private final boolean swap;
    private final boolean move;

    Name(int cost, int maxMoves, boolean needsSourceCreature, boolean needsIslandIndex, boolean needsMnMovements,
         boolean needsDestination, boolean needsDestinationCreature, boolean swap, boolean move, String image) {
        this.cost = cost;
        this.maxMoves = maxMoves;
        this.needsSourceCreature = needsSourceCreature;
        this.needsIslandIndex = needsIslandIndex;
        this.needsMnMovements = needsMnMovements;
        this.needsDestination = needsDestination;
        this.needsDestinationCreature = needsDestinationCreature;
        this.swap = swap;
        this.move = move;
        this.image = image;
    }

    public boolean isSwap() {
        return swap;
    }

    public boolean isMove() {
        return move;
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

    public String getImage() {
        return image;
    }

    public boolean needsParameters() {
        return needsSourceCreature || needsIslandIndex || needsMnMovements || needsDestination || needsDestinationCreature;
    }
}
