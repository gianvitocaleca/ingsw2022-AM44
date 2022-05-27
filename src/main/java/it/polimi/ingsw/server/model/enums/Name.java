package it.polimi.ingsw.server.model.enums;

public enum Name {
    HERALD(3, 1, false, true, false, false, false, "Characters/herald.jpg"),
    KNIGHT(2, 1, false, false, false, false, false,"Characters/knight"),
    CENTAUR(3, 1, false, false, false, false, false, "Characters/centaur.jpg"),
    FARMER(2, 1, false, false, false, false, false, "Characters/farmer.jpg"),
    FUNGARO(3, 1, true, false, false, false, false, "Characters/fungaro.jpg"),
    JOKER(1, 3, true, false, false, false, true, "Characters/joker.jpg"),
    THIEF(3, 1, true, false, false, false, false, "Characters/thief.jpg"),
    MINSTREL(1, 2, true, false, false, false, true, "Characters/minstrel.jpg"),
    MONK(1, 1, true, false, false, true, false, "Characters/monk.jpg"),
    HERBALIST(2, 1, false, true, false, false, false, "Characters/herbalist.jpg"),
    MAGICPOSTMAN(1, 1, false, false, true, false, false, "Characters/magicPostman.jpg"),
    PRINCESS(2, 1, true, false, false, false, false, "Characters/princess.jpg");

    private final int cost;
    private final int maxMoves;
    private final String image;
    private final boolean needsSourceCreature;
    private final boolean needsIslandIndex;
    private final boolean needsMnMovements;
    private final boolean needsDestination;
    private final boolean needsDestinationCreature;

    Name(int cost, int maxMoves, boolean needsSourceCreature, boolean needsIslandIndex, boolean needsMnMovements, boolean needsDestination, boolean needsDestinationCreature, String image) {
        this.cost = cost;
        this.maxMoves = maxMoves;
        this.needsSourceCreature = needsSourceCreature;
        this.needsIslandIndex = needsIslandIndex;
        this.needsMnMovements = needsMnMovements;
        this.needsDestination = needsDestination;
        this.needsDestinationCreature = needsDestinationCreature;
        this.image = image;
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

    public boolean needsParameters(){
        return needsSourceCreature || needsIslandIndex || needsMnMovements || needsDestination || needsDestinationCreature;
    }
}
