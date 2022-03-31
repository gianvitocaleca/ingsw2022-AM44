package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Value;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.model.studentcontainers.Entrance;

import java.util.*;

public class Player {

    private final String username;
    private final Entrance entrance;
    private final DiningRoom diningRoom;
    private final int diningRoomCapacity = 9;
    private final Color myColor;
    private int myCoins;
    private final Wizard wizard;
    private final List<Assistant> lastPlayedCard;
    private final List<Assistant> assistantDeck;
    private final List<Professor> professors;
    private int towers;

    public Player(String username, Color myColor, int myCoins, Wizard wizard,int towers, Entrance entrance) {
        this.username = username;
        this.myColor = myColor;
        this.myCoins = myCoins;
        this.wizard = wizard;
        this.towers = towers;
        this.lastPlayedCard= new ArrayList<>();
        assistantDeck = new ArrayList<>();
        for(Value v : Value.values()){
            assistantDeck.add(new Assistant(v));
        }
        professors = new ArrayList<>();
        this.entrance = entrance;
        this.diningRoom = new DiningRoom(diningRoomCapacity);
    }

    public String getUsername() {
        return username;
    }

    public Color getMyColor() {
        return myColor;
    }

    public int getMyCoins() {
        return myCoins;
    }

    public Assistant getLastPlayedCard() {

        return lastPlayedCard.get(lastPlayedCard.size()-1);
    }

    public List<Assistant> getLastPlayedCards() {
        List<Assistant> tempList = new ArrayList<>(lastPlayedCard);
        return tempList;
    }

    public List<Assistant> getAssistantDeck() {
        List<Assistant> tempList = new ArrayList<>(assistantDeck);
        return tempList;
    }

    public List<Professor> getProfessors() {
        List<Professor> tempProf = new ArrayList<>(professors);
        return tempProf;
    }

    public Wizard getWizard() {
        return wizard;
    }

    public int getTowers() {
        return towers;
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public DiningRoom getDiningRoom() {
        return diningRoom;
    }

    public void addCoin() {
        this.myCoins++;
    }

    public void removeCoin(int character_cost) {
        myCoins = myCoins - character_cost;
    }

    public void setAssistantCard(Assistant assistant) {
        assistantDeck.remove(assistant);
        lastPlayedCard.add(assistant);
    }

    public void returnTowers(int num_of_towers) {
        towers = towers + num_of_towers;
    }

    public void placeTowers(int placedTowers){
        towers -= placedTowers;
    }

    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    public Professor removeProfessor(Creature creature) {
        return new Professor(creature);
    }

}
