package it.polimi.ingsw.network.server.networkMessages.payloads;

import it.polimi.ingsw.network.server.CharacterInformation;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.Island;

import java.util.*;

public class ShowModelPayload implements Payload {

    private String currentPlayerUsername;
    private List<Player> playersList;
    private List<Island> islands;
    private List<Cloud> clouds;
    private int motherNature;
    private int deactivators;
    private int coinReserve;


    private boolean advancedRules = false;

    private List<CharacterInformation> characters;
    private List<Creature> monkCreatures;
    private List<Creature> princessCreatures;
    private List<Creature> jokerCreatures;

    private boolean updatePlayersAssistant = false; //assistantDeck, lastPlayedCard
    private boolean updatePlayersEntrance = false; //entrance
    private boolean updatePlayersDiningRoom = false; //dining, professors, myCoins, coinReserve
    private boolean updateIslands = false; //player's towers, islands, deactivators
    private boolean updateAll = false;
    private boolean updateClouds = false;
    private boolean updateMotherNature = false;
    private boolean updateCoinReserve = false; //myCoins, coinReserve
    private boolean updatePlayedCharacter = false;
    private boolean reconnection = false;

    /**
     * Used to deliver all the game information.
     * Is used incrementally to optimize network messages.
     * @param playersList is the list of players
     * @param table is the game object
     */
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
        this.characters = new ArrayList<>();
    }

    /**
     * Used to set the reconnection status
     */
    public void setReconnection() {
        reconnection = true;
    }

    /**
     *
     * @return whether the payload is for reconnection purposes
     */
    public boolean getReconnection() {
        return reconnection;
    }

    /**
     *
     * @return is the current player username
     */
    public String getCurrentPlayerUsername() {
        return currentPlayerUsername;
    }

    /**
     *
     * @param username is the current player username
     */
    public void setCurrentPlayerUsername(String username) {
        this.currentPlayerUsername = username;
    }

    /**
     *
     * @return is the list of characters in the game
     */
    public List<CharacterInformation> getCharacters() {
        return characters;
    }

    /**
     *
     * @return is the list of players in the game
     */
    public List<Player> getPlayersList() {
        return playersList;
    }

    /**
     *
     * @param advancedRules is the type of rules for the game
     */
    public void setAdvancedRules(boolean advancedRules) {
        this.advancedRules = advancedRules;
    }

    /**
     *
     * @return whether the game uses advanced rules
     */
    public boolean isAdvancedRules() {
        return advancedRules;
    }

    /**
     *
     * @param characters is the list of characters for the game
     */
    public void setCharacters(List<CharacterInformation> characters) {
        this.characters = characters;
    }

    /**
     *
     * @param monkCreatures is the list of creatures for the monk character
     */
    public void setMonkCreatures(List<Creature> monkCreatures) {
        this.monkCreatures = monkCreatures;
    }

    /**
     *
     * @param princessCreatures is the list of creatures for the princess character
     */
    public void setPrincessCreatures(List<Creature> princessCreatures) {
        this.princessCreatures = princessCreatures;
    }

    /**
     *
     * @param jokerCreatures is the list of creatures for the joker character
     */
    public void setJokerCreatures(List<Creature> jokerCreatures) {
        this.jokerCreatures = jokerCreatures;
    }

    /**
     *
     * @return is the list of islands
     */
    public List<Island> getIslands() {
        return islands;
    }

    /**
     *
     * @return is the list of clouds
     */
    public List<Cloud> getClouds() {
        return clouds;
    }

    /**
     *
     * @param playersList is the list of players in the game
     */
    public void setPlayersList(List<Player> playersList) {
        this.playersList = playersList;
    }

    /**
     *
     * @param islands is the list of islands in the game
     */
    public void setIslands(List<Island> islands) {
        this.islands = islands;
    }

    /**
     *
     * @param clouds is the list of clouds in the game
     */
    public void setClouds(List<Cloud> clouds) {
        this.clouds = clouds;
    }

    /**
     *
     * @param motherNature is mother nature position in the game
     */
    public void setMotherNature(int motherNature) {
        this.motherNature = motherNature;
    }

    /**
     *
     * @param deactivators is the number of no entries for the herbalist character
     */
    public void setDeactivators(int deactivators) {
        this.deactivators = deactivators;
    }

    /**
     *
     * @param coinReserve is the number of coins left on the table
     */
    public void setCoinReserve(int coinReserve) {
        this.coinReserve = coinReserve;
    }

    /**
     *
     * @return is the position of mother nature
     */
    public int getMotherNature() {
        return motherNature;
    }

    /**
     *
     * @return is the number of no entries on the herbalist
     */
    public int getDeactivators() {
        return deactivators;
    }

    /**
     *
     * @return is the number of coins left on the table
     */
    public int getCoinReserve() {
        return coinReserve;
    }

    /**
     *
     * @return is the list of creatures on the monk character
     */
    public List<Creature> getMonkCreatures() {
        return monkCreatures;
    }

    /**
     *
     * @return is the list of creatures on the princess character
     */
    public List<Creature> getPrincessCreatures() {
        return princessCreatures;
    }

    /**
     *
     * @return is the list of creatures on the joker character
     */
    public List<Creature> getJokerCreatures() {
        return jokerCreatures;
    }

    /**
     *
     * @return whether the assistants need to be updated
     */
    public boolean isUpdatePlayersAssistant() {
        return updatePlayersAssistant;
    }

    /**
     * Used to set whether the assistants need to be updated
     */
    public void setUpdatePlayersAssistant() {
        this.updatePlayersAssistant = true;
    }

    /**
     *
     * @return whether the entrances need to be updated
     */
    public boolean isUpdatePlayersEntrance() {
        return updatePlayersEntrance;
    }

    /**
     * Used to set whether the entrances need to be updated
     */
    public void setUpdatePlayersEntrance() {
        this.updatePlayersEntrance = true;
    }

    /**
     *
     * @return whether the dining rooms need to be updated
     */
    public boolean isUpdatePlayersDiningRoom() {
        return updatePlayersDiningRoom;
    }

    /**
     * Used to set whether the dining rooms need to be updated
     */
    public void setUpdatePlayersDiningRoom() {
        this.updatePlayersDiningRoom = true;
    }

    /**
     *
     * @return whether the islands need to be updated
     */
    public boolean isUpdateIslands() {
        return updateIslands;
    }

    /**
     * Used to set whether the islands need to be updated
     */
    public void setUpdateIslands() {
        this.updateIslands = true;
    }

    /**
     *
     * @return whether the hole game needs to be updated
     */
    public boolean isUpdateAll() {
        return updateAll;
    }

    /**
     * Used to set whether the hole game needs to be updated
     */
    public void setUpdateAll() {
        this.updateAll = true;
    }

    /**
     *
     * @return whether the clouds need to be updated
     */
    public boolean isUpdateClouds() {
        return updateClouds;
    }

    /**
     * Used to set whether the clouds need to be updated
     */
    public void setUpdateClouds() {
        this.updateClouds = true;
    }

    /**
     *
     * @return whether mother nature needs to be updated
     */
    public boolean isUpdateMotherNature() {
        return updateMotherNature;
    }

    /**
     * Used to set whether mother nature needs to be updated
     */
    public void setUpdateMotherNature() {
        this.updateMotherNature = true;
    }

    /**
     *
     * @return whether the coin reserve needs to be updated
     */
    public boolean isUpdateCoinReserve() {
        return updateCoinReserve;
    }

    /**
     * Used to set whether the coin reserve needs to be updated
     */
    public void setUpdateCoinReserve() {
        this.updateCoinReserve = true;
    }

    /**
     * Used to set whether the played character needs to be updated
     */
    public void setUpdatePlayedCharacter() {
        this.updatePlayedCharacter = true;
    }

    /**
     *
     * @return whether the played character needs to be updated
     */
    public boolean isUpdatePlayedCharacter() {
        return updatePlayedCharacter;
    }

    /**
     *
     * @return is the number of coins the current player has
     */
    public int getCurrentPlayerCoins() {
        int i = 0;
        for (Player p : playersList) {
            if (p.getUsername().equals(currentPlayerUsername)) {
                i = p.getMyCoins();
            }
        }
        return i;
    }

    /**
     *
     * @param username is the given username
     * @return is the number of towers the player has
     */
    public int getPlayerTowers(String username) {
        int i = 0;
        for (Player p : playersList) {
            if (p.getUsername().equals(username)) {
                i = p.getTowers();
            }
        }
        return i;
    }
}
