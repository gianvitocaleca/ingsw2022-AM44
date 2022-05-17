package it.polimi.ingsw.server.networkMessages;

import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.gameboard.Table;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.studentcontainers.Cloud;
import it.polimi.ingsw.server.model.studentcontainers.Island;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowModelPayload implements Payload {

    private String currentPlayerUsername;
    private List<Player> playersList;
    private List<Island> islands;
    private List<Cloud> clouds;
    private int motherNature;
    private int deactivators;
    private int coinReserve;

    private Map<Name, Integer> characters;
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
        this.characters = new HashMap<>();
    }

    public String getCurrentPlayerUsername() {
        return currentPlayerUsername;
    }

    public void setCurrentPlayerUsername(String username) {
        this.currentPlayerUsername = username;
    }

    public Map<Name, Integer> getCharacters() {
        return characters;
    }

    public List<Player> getPlayersList() {
        return playersList;
    }

    public void setCharacters(Map<Name, Integer> characters) {
        this.characters = characters;
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

    public void setPlayersList(List<Player> playersList) {
        this.playersList = playersList;
    }

    public void setIslands(List<Island> islands) {
        this.islands = islands;
    }

    public void setClouds(List<Cloud> clouds) {
        this.clouds = clouds;
    }

    public void setMotherNature(int motherNature) {
        this.motherNature = motherNature;
    }

    public void setDeactivators(int deactivators) {
        this.deactivators = deactivators;
    }

    public void setCoinReserve(int coinReserve) {
        this.coinReserve = coinReserve;
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

    public boolean isUpdatePlayersAssistant() {
        return updatePlayersAssistant;
    }

    public void setUpdatePlayersAssistant() {
        this.updatePlayersAssistant = true;
    }

    public boolean isUpdatePlayersEntrance() {
        return updatePlayersEntrance;
    }

    public void setUpdatePlayersEntrance() {
        this.updatePlayersEntrance = true;
    }

    public boolean isUpdatePlayersDiningRoom() {
        return updatePlayersDiningRoom;
    }

    public void setUpdatePlayersDiningRoom() {
        this.updatePlayersDiningRoom = true;
    }

    public boolean isUpdateIslands() {
        return updateIslands;
    }

    public void setUpdateIslands() {
        this.updateIslands = true;
    }

    public boolean isUpdateAll() {
        return updateAll;
    }

    public void setUpdateAll() {
        this.updateAll = true;
    }

    public boolean isUpdateClouds() {
        return updateClouds;
    }

    public void setUpdateClouds() {
        this.updateClouds = true;
    }

    public boolean isUpdateMotherNature() {
        return updateMotherNature;
    }

    public void setUpdateMotherNature() {
        this.updateMotherNature = true;
    }

    public boolean isUpdateCoinReserve() {
        return updateCoinReserve;
    }

    public void setUpdateCoinReserve() {
        this.updateCoinReserve = true;
    }

    public void setUpdatePlayedCharacter() {
        this.updatePlayedCharacter = true;
    }

    public boolean isUpdatePlayedCharacter() {
        return updatePlayedCharacter;
    }

    public int getCurrentPlayerCoins() {
        int i = 0;
        for (Player p : playersList) {
            if (p.getUsername().equals(currentPlayerUsername)) {
                i = p.getMyCoins();
            }
        }
        return i;
    }

    public int getPlayerTowers(String username) {
        int i = 0;
        for (Player p : playersList) {
            if (p.getUsername().equals(username)) {
                i = p.getTowers();
            }
        }
        return i;
    }


    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("\r Players:");
        for (Player p : playersList) {
            string.append(printPlayer(p));
        }
        string.append("\n");
        string.append("Clouds:=: ");
        int i = 0;
        for (Cloud c : clouds) {
            string.append("\n" + i).append(": ").append(c.getStudents());
            i++;
        }
        string.append("\n");
        string.append("Islands:=: ");
        i = 1;
        for (Island e : islands) {
            string.append("\n" + i).append(": ").append(printIsland(e));
            if (i == motherNature) {
                string.append("\nMother nature is here!!!");
            }
            i++;
        }
        string.append("\n");
        if (characters.size() > 0) {
            string.append(" Table:=: ");
            string.append("\nCoin Reserve: ").append(coinReserve).append("\n");
            string.append("Characters:\n" + printCharacters());
        }
        string.append("\n");
        return string.toString();
    }

    private String printPlayer(Player p) {
        String string;
        string = "\nUsername: " + p.getUsername() + " Professors: " + p.getProfessors() + "\n" +
                "Wizard: " + p.getWizard() + " Color: " + p.getMyColor() + "\n"
                + "Entrance: " + p.getEntrance().getStudents() + "\n"
                + "Dining Room: " + p.getDiningRoom().getStudents() + "\n";
        if (characters.size() > 0) {
            string += "Coins: " + p.getMyCoins();
        }
        string += " Towers: " + p.getTowers() + "\n"
                + "Last Played Assistant: ";
        if (!(p.getAssistantDeck().size() == 10)) {
            string += p.getLastPlayedCard();
        } else {
            string += "none";
        }

        return string;
    }

    private String printIsland(Island i) {
        String string = "Students: " + i.getStudents() + "\n"
                + "Towers: " + i.getNumberOfTowers();
        if (i.getNumberOfTowers() > 0) {
            string += ", Of Color" + i.getColorOfTowers();
        }
        if (characters.size() > 0) {
            string += "\nDeactivators:" + i.getNumberOfNoEntries();
        }
        return string;
    }

    private String printCharacters() {
        String string = "";
        int i = 2;
        for (Name n : characters.keySet()) {
            string += i + ":" + n + " Cost:" + characters.get(n);
            if (n.equals(Name.MONK)) {
                string += " Creatures: " + monkCreatures;
            } else if (n.equals(Name.PRINCESS)) {
                string += " Creatures: " + princessCreatures;
            } else if (n.equals(Name.JOKER)) {
                string += " Creatures: " + jokerCreatures;
            }
            string += "\n";
            i--;
        }
        return string;
    }
}
