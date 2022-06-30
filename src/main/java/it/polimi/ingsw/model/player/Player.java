package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.enums.Assistants;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.model.studentcontainers.Entrance;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Wizard;

import java.util.*;

public class Player {

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
    private boolean assistantPlayed;

    /**
     * Is the player's representation
     * @param username is the player's name
     * @param myColor is the player's color
     * @param myCoins is the player's number of coins
     * @param wizard is the player's wizard
     * @param towers is the number of towers on the player's game board
     * @param entrance is the player's entrance
     */
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
        this.diningRoom = new DiningRoom();
        this.assistantPlayed = false;
    }

    public boolean isAssistantPlayed() {
        return assistantPlayed;
    }

    public void setAssistantPlayed(boolean assistantPlayed) {
        this.assistantPlayed = assistantPlayed;
    }

    /**
     *
     * @return is the player's name
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return is the player's color
     */
    public Color getMyColor() {
        return myColor;
    }

    /**
     *
     * @return is the player's number of coins
     */
    public int getMyCoins() {
        return myCoins;
    }

    /**
     *
     * @return is the last played assistant
     */
    public Assistant getLastPlayedCard() {
        return new Assistant(lastPlayedCard.get(lastPlayedCard.size() - 1).getName());
    }

    /**
     *
     * @return is the list of all the played assistant by the player
     */
    public List<Assistant> getLastPlayedCards() {

        List<Assistant> tempList = new ArrayList<>();

        for (Assistant a : lastPlayedCard) {
            tempList.add(new Assistant(a.getName()));
        }

        return tempList;
    }

    /**
     *
     * @return is the list of all the remaining assistants of the player
     */
    public List<Assistant> getAssistantDeck() {

        List<Assistant> tempList = new ArrayList<>();

        for (Assistant a : assistantDeck) {
            tempList.add(new Assistant(a.getName()));
        }

        return tempList;
    }

    /**
     *
     * @return is the list of all the player's professors
     */
    public List<Professor> getProfessors() {
        List<Professor> tempList = new ArrayList<>();

        for (Professor p : professors) {
            tempList.add(new Professor(p.getCreature()));
        }

        return tempList;
    }

    /**
     *
     * @return is the player's wizard
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     *
     * @return is the number of towers the player has
     */
    public int getTowers() {
        return towers;
    }

    /**
     *
     * @return is the player's entrance
     */
    public Entrance getEntrance() {

        Entrance temp = new Entrance(entrance.getCapacity());
        temp.addStudents(entrance.getStudents());

        return temp;
    }

    /**
     *
     * @return is the player's dining room
     */
    public DiningRoom getDiningRoom() {

        DiningRoom temp = new DiningRoom();
        temp.addStudents(diningRoom.getStudents());

        return temp;
    }

    /**
     * Adds a coin to the player
     */
    public void addCoin() {
        this.myCoins++;
    }

    /**
     * Removes the coins from the player
     * @param characterCost is the number of coins to remove
     */
    public void removeCoin(int characterCost) {
        myCoins -= characterCost;
    }

    /**
     * Used to set the last played assistant
     * @param indexOfAssistant is the index of the assistant card
     */
    public void setAssistantCard(int indexOfAssistant) {
        lastPlayedCard.add(assistantDeck.remove(indexOfAssistant));
    }

    /**
     * Used to add a tower to the player.
     * @param numOfTowers is the number of tower to be added
     */
    public void addTowers(int numOfTowers) {
        towers += numOfTowers;
    }

    /**
     * Used to remove towers from the player
     * @param placedTowers is the number of towers to be removed
     * @throws GameEndedException thrown when the player has finished the towers
     */
    public void removeTowers(int placedTowers) throws GameEndedException {
        towers -= placedTowers;
        if (towers == 0) {
            throw new GameEndedException();
        }
    }

    /**
     * Used to add a professor to the player
     * @param professor is the provided professor
     */
    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    /**
     * Used to remove a professor
     * @param creature is the type of professor
     * @return is the removed professor
     */
    public Professor removeProfessor(Creature creature) {
        Professor temp = professors.stream().filter(p -> p.getCreature().equals(creature)).findFirst().get();
        professors.remove(temp);
        return temp;
    }

    /**
     * Used for test purposes.
     */
    @Override
    public String toString() {
        return "value of last played card is " + getLastPlayedCard().getValue();
    }

    /**
     * Used to keep track of the coins in the dining room.
     * @return is the new map
     */
    private Map<Creature, ArrayList<Boolean>> createMap() {
        Map<Creature, ArrayList<Boolean>> newMap = new HashMap<>();
        for (Creature c : Creature.values()) {
            newMap.put(c, new ArrayList<Boolean>());
        }
        return newMap;
    }

    /**
     *
     * @return is the dining room map of the player
     */
    public Map<Creature, ArrayList<Boolean>> getGivenCoins() {

        Map<Creature, ArrayList<Boolean>> temp = new HashMap<>();

        for (Creature c : Creature.values()) {
            temp.put(c, givenCoins.get(c));
        }

        return temp;
    }

    /**
     * Performs a check on the dining room map.
     * @param creature the type of student to check for
     * @return whether the player has earned a coin
     */
    public boolean checkCoinGiver(Creature creature) {
        int response = diningRoom.getNumberOfStudentsByCreature(creature);
        if (response / 3 > givenCoins.get(creature).size()) {
            addCoin();
            givenCoins.get(creature).add(true);
            return true;
        }
        return false;
    }

    /**
     *
     * @param myCoins is the number of coins to be set
     */
    public void setMyCoins(int myCoins) {
        this.myCoins = myCoins;
    }

    /**
     *
     * @param towers is the number of towers to be set
     */
    public void setTowers(int towers) {
        this.towers = towers;
    }

    /**
     *
     * @param givenCoins is the dining room map to be set
     */
    public void setGivenCoins(Map<Creature, ArrayList<Boolean>> givenCoins) {
        this.givenCoins = givenCoins;
    }

    /**
     *
     * @param diningRoom is the dining room to be set
     */
    public void setDiningRoom(DiningRoom diningRoom) {
        this.diningRoom = diningRoom;
    }

    /**
     *
     * @param lastPlayedCard is the list of played assistants to be set
     */
    public void setLastPlayedCards(List<Assistant> lastPlayedCard) {
        this.lastPlayedCard = lastPlayedCard;
    }

    /**
     *
     * @param assistantDeck is the list of remaining assistants to be set
     */
    public void setAssistantDeck(List<Assistant> assistantDeck) {
        this.assistantDeck = assistantDeck;
    }

    /**
     *
     * @param professors is the list of professors to be set
     */
    public void setProfessors(List<Professor> professors) {
        this.professors = professors;
    }

    /**
     *
     * @param entrance is the entrance to be set
     */
    public void setEntrance(Entrance entrance) {
        this.entrance = entrance;
    }

    /**
     *
     * @param c the type of professor
     * @return whether the player has the provided type of professor
     */
    public boolean hasProfessor(Creature c){
        List <Creature> professorsCreatures = professors.stream().map(Professor::getCreature).toList();
        return professorsCreatures.contains(c);
    }
}
