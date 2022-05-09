package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.gameboard.MotherNature;
import it.polimi.ingsw.server.model.player.Assistant;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.Professor;
import it.polimi.ingsw.server.model.studentcontainers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelCache {

    private List<Player> playersList;

    private List<Island> islands = new ArrayList<>();
    private List<Cloud> clouds = new ArrayList<>();
    private MotherNature motherNature;
    private int deactivators = 0;
    private StudentContainer monk;
    private StudentContainer princess;
    private StudentContainer joker;
    private int coinReserve = 0;

    private Map<Name, Boolean> characters;
    private Name playedCharacter;

    public ModelCache(List<Player> playersList, List<Island> islands, List<Cloud> clouds,
                      MotherNature motherNature, int deactivators, StudentContainer monk,
                      StudentContainer princess, StudentContainer joker, int coinReserve,
                      Map<Name, Boolean> characters, Name playedCharacter) {
        this.playersList = playersList;
        this.islands = islands;
        this.clouds = clouds;
        this.motherNature = motherNature;
        this.deactivators = deactivators;
        this.monk = monk;
        this.princess = princess;
        this.joker = joker;
        this.coinReserve = coinReserve;
        this.characters = characters;
        this.playedCharacter = playedCharacter;
    }

    public void setPlayerEntrance(int indexOfPlayer, Entrance entrance) {
        playersList.get(indexOfPlayer).setEntrance(entrance);
    }

    public void setPlayerDiningRoom(int indexOfPlayer, DiningRoom diningRoom) {
        playersList.get(indexOfPlayer).setDiningRoom(diningRoom);
    }

    public void setPlayerLastPlayedCard(int indexOfPlayer, List<Assistant> assistants) {
        playersList.get(indexOfPlayer).setLastPlayedCards(assistants);
    }

    public void setPlayerAssistantDeck(int indexOfPlayer, List<Assistant> assistants) {
        playersList.get(indexOfPlayer).setLastPlayedCards(assistants);
    }

    public void setPlayerProfessors(int indexOfPlayer, List<Professor> professors) {
        playersList.get(indexOfPlayer).setProfessors(professors);
    }

    public void setPlayerCoins(int indexOfPlayer, int coins) {
        playersList.get(indexOfPlayer).setMyCoins(coins);
    }

    public void setPlayerTowers(int indexOfPlayer, int towers) {
        playersList.get(indexOfPlayer).setTowers(towers);
    }

    public void setIslandByIndex(int indexOfIsland, Island island) {
        islands.remove(indexOfIsland);
        islands.add(indexOfIsland, island);
    }

    public void setIslands(List<Island> islands) {
        this.islands = islands;
    }

    public void setCloudByIndex(int indexOfCloud, Cloud cloud) {
        clouds.remove(indexOfCloud);
        clouds.add(indexOfCloud, cloud);
    }

    public void setClouds(List<Cloud> clouds) {
        this.clouds = clouds;
    }

    public void setMotherNatureCurrentPosition(int position) {
        motherNature.setCurrentIsland(position);
    }

    public void setCharacterUpdatedCostByName(Name name) {
        characters.put(name, true);
    }

    public void setDeactivators(int deactivators) {
        this.deactivators = deactivators;
    }

    public void setMonk(StudentContainer monk) {
        this.monk = monk;
    }

    public void setPrincess(StudentContainer princess) {
        this.princess = princess;
    }

    public void setJoker(StudentContainer joker) {
        this.joker = joker;
    }

    public void setCoinReserve(int coinReserve) {
        this.coinReserve = coinReserve;
    }

    public void setPlayedCharacter(Name playedCharacter) {
        this.playedCharacter = playedCharacter;
    }


    public List<Player> getPlayersList() {
        return playersList;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public MotherNature getMotherNature() {
        return motherNature;
    }

    public int getDeactivators() {
        return deactivators;
    }

    public StudentContainer getMonk() {
        return monk;
    }

    public StudentContainer getPrincess() {
        return princess;
    }

    public StudentContainer getJoker() {
        return joker;
    }

    public int getCoinReserve() {
        return coinReserve;
    }

    public Map<Name, Boolean> getCharacters() {
        return characters;
    }

    public Name getPlayedCharacter() {
        return playedCharacter;
    }
}
