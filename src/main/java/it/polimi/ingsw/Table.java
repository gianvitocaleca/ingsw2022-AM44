package it.polimi.ingsw;

import java.util.*;

public class Table {
    private List<Island> islands;
    private List<Cloud> clouds;
    private MotherNature mother_nature;
    private int coin_reserve;

    public Table(List<Island> islands, MotherNature mother_nature, List<Cloud> clouds, int coin_reserve) {
        this.islands = islands;
        this.mother_nature = mother_nature;
        this.clouds = clouds;
        this.coin_reserve = coin_reserve;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public MotherNature getMother_nature() {
        return mother_nature;
    }

    public int getCoin_reserve() {
        return coin_reserve;
    }

    public Island islandFusion(List<Island> islands) {
        return new Island(new ArrayList<Student>(), 0, Color.BLACK);
    }
}
