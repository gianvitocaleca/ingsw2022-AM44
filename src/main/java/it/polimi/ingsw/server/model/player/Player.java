package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.exceptions.GameEndedException;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Assistants;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.server.model.studentcontainers.Entrance;

import java.util.*;

public class Player {

    private final int diningRoomCapacity = 9;
    private final String username;
    private Entrance entrance;
    private DiningRoom diningRoom;
    private final Color myColor;
    private final Wizard wizard;
    private List<Assistant> lastPlayedCard;
    private List<Assistant> assistantDeck;
    private List<Professor> professors;
    private int myCoins;
    private int towers;
    private Map<Creature, ArrayList<Boolean>> givenCoins = createMap();

    public Player(String username, Color myColor, int myCoins, Wizard wizard, int towers, Entrance entrance) {
        this.username = username;
        this.myColor = myColor;
        this.myCoins = myCoins;
        this.wizard = wizard;
        this.towers = towers;
        this.lastPlayedCard = new ArrayList<>();
        assistantDeck = new ArrayList<>();
        for (Assistants v : Assistants.values()) {
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
        Assistant temp = new Assistant(lastPlayedCard.get(lastPlayedCard.size() - 1).getName());
        return temp;
    }

    public List<Assistant> getLastPlayedCards() {

        List<Assistant> tempList = new ArrayList<>();

        for (Assistant a : lastPlayedCard) {
            tempList.add(new Assistant(a.getName()));
        }

        return tempList;
    }

    public List<Assistant> getAssistantDeck() {

        List<Assistant> tempList = new ArrayList<>();

        for (Assistant a : assistantDeck) {
            tempList.add(new Assistant(a.getName()));
        }

        return tempList;
    }

    public List<Professor> getProfessors() {
        List<Professor> tempList = new ArrayList<>();

        for (Professor p : professors) {
            tempList.add(new Professor(p.getCreature()));
        }

        return tempList;
    }

    public Wizard getWizard() {
        return wizard;
    }

    public int getTowers() {
        return towers;
    }

    public Entrance getEntrance() {

        Entrance temp = new Entrance(entrance.getCapacity());
        temp.addStudents(entrance.getStudents());

        return temp;
    }

    public DiningRoom getDiningRoom() {

        DiningRoom temp = new DiningRoom(diningRoom.getCapacity());
        temp.addStudents(diningRoom.getStudents());

        return temp;
    }

    public void addCoin() {
        this.myCoins++;
    }

    public void removeCoin(int character_cost) {
        myCoins -= character_cost;
    }

    public void setAssistantCard(int indexofassistant) {
        lastPlayedCard.add(assistantDeck.remove(indexofassistant));
    }

    public void addTowers(int num_of_towers) {
        towers += num_of_towers;
    }

    public void removeTowers(int placedTowers) throws GameEndedException {
        towers -= placedTowers;
        if (towers == 0) {
            throw new GameEndedException();
        }
    }

    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    public Professor removeProfessor(Creature creature) {
        Professor temp = professors.stream().filter(p -> p.getCreature().equals(creature)).findFirst().get();
        professors.remove(temp);
        return temp;
    }

    @Override
    public String toString() {
        return "value of last played card is " + getLastPlayedCard().getValue();
    }

    private Map<Creature, ArrayList<Boolean>> createMap() {
        Map<Creature, ArrayList<Boolean>> newMap = new HashMap<>();
        for (Creature c : Creature.values()) {
            newMap.put(c, new ArrayList<Boolean>());
        }
        return newMap;
    }

    public Map<Creature, ArrayList<Boolean>> getGivenCoins() {

        Map<Creature, ArrayList<Boolean>> temp = new HashMap<>();

        for (Creature c : Creature.values()) {
            temp.put(c, givenCoins.get(c));
        }

        return temp;
    }

    public boolean checkCoinGiver(Creature creature) {
        int response = diningRoom.getNumberOfStudentsByCreature(creature);
        if (response / 3 > givenCoins.get(creature).size()) {
            addCoin();
            givenCoins.get(creature).add(true);
            return true;
        }
        return false;
    }

    public void setMyCoins(int myCoins) {
        this.myCoins = myCoins;
    }

    public void setTowers(int towers) {
        this.towers = towers;
    }

    public void setGivenCoins(Map<Creature, ArrayList<Boolean>> givenCoins) {
        this.givenCoins = givenCoins;
    }

    public void setDiningRoom(DiningRoom diningRoom) {
        this.diningRoom = diningRoom;
    }

    public void setLastPlayedCards(List<Assistant> lastPlayedCard) {
        this.lastPlayedCard = lastPlayedCard;
    }

    public void setAssistantDeck(List<Assistant> assistantDeck) {
        this.assistantDeck = assistantDeck;
    }

    public void setProfessors(List<Professor> professors) {
        this.professors = professors;
    }

    public void setEntrance(Entrance entrance) {
        this.entrance = entrance;
    }

    public boolean hasProfessor(Creature c){
        List <Creature> professorsCreatures = professors.stream().map(Professor::getCreature).toList();
        return professorsCreatures.contains(c);
    }
}
