package it.polimi.ingsw;

public enum Name {
    HERALD(3),
    KNIGHT(2),
    CENTAUR(3),
    FARMER(2),
    FUNGARO(3),
    JOKER(1),
    THIEF(3),
    MINSTREL(1),
    MONK(1),
    HERBALIST(2),
    MAGICPOSTMAN(1),
    PRINCESS(2);

    public final int cost;

    private Name(int cost) {
        this.cost = cost;
    }

    public int getCost(){ return cost; }
    public int getCostPlusOne(){ return cost+10;}
}
