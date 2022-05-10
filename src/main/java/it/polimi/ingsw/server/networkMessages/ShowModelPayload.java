package it.polimi.ingsw.server.networkMessages;

import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.gameboard.MotherNature;
import it.polimi.ingsw.server.model.gameboard.Table;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.studentcontainers.Cloud;
import it.polimi.ingsw.server.model.studentcontainers.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowModelPayload implements Payload{

    List<Player> playersList;

    private List<Island> islands;
    private List<Cloud> clouds;
    private int motherNature;
    private int deactivators;
    private int coinReserve;

    private List<Name> characters;
    private List<Creature> monkCreatures;
    private List<Creature> princessCreatures;
    private List<Creature> jokerCreatures;


    public ShowModelPayload(List<Player> playersList, Table table) {
        this.playersList = playersList;
        this.islands = table.getIslands();
        this.clouds = table.getClouds();
        this.motherNature = table.getMnPosition();
        this.deactivators = table.getDeactivators();
        this.coinReserve = table.getCoinReserve();
        this.monkCreatures = new ArrayList<>();
        this.jokerCreatures = new ArrayList<>();
        this.princessCreatures = new ArrayList<>();
    }

    public void setCharacters(List<Name> characters) {
        this.characters = characters;
    }

    public List<Player> getPlayersList() {
        return playersList;
    }

    public List<Name> getCharacters() {
        return characters;
    }

    public void setMonkCreatures(List<Creature> monkCreatures) {
        this.monkCreatures = monkCreatures;
    }

    public void setPrincessCreatures(List<Creature> princessCreatures) {
        this.princessCreatures = princessCreatures;
    }

    public void setJokerCreatures(List<Creature> jokerCreatures) {
        this.jokerCreatures = jokerCreatures;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public int getMotherNature() {
        return motherNature;
    }

    public int getDeactivators() {
        return deactivators;
    }

    public int getCoinReserve() {
        return coinReserve;
    }

    public List<Creature> getMonkCreatures() {
        return monkCreatures;
    }

    public List<Creature> getPrincessCreatures() {
        return princessCreatures;
    }

    public List<Creature> getJokerCreatures() {
        return jokerCreatures;
    }

    @Override
    public String toString() {
        String string = "Players:";
        for(Player p : playersList){
            string = string +" "+p.getUsername();
        }
        return string;
    }
}
