package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.gameboard.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.studentcontainers.*;
import it.polimi.ingsw.model.students.*;

import java.util.*;

public class GameModel implements Playable {

    private List<Player> players;
    private int currPlayer;
    private Table table;
    private int numberOfPlayers;
    private List<Character> characters;
    private Character playedCharacter;

    public GameModel(boolean advancedRules, List<String> usernames, int numberOfPlayers, List<Color> colors, List<Wizard> wizards) {
        //aggiungere un giocatore alla volta per il problema del colore e del mago?
        players = createListOfPlayers(advancedRules, usernames, colors, wizards);
        this.numberOfPlayers = numberOfPlayers;
        //Il primo a giocare la carta assistente a inizio partita sarà il primo che ha fatto log in e di conseguenza il player in posizione zero
        currPlayer = 0;
        this.table = new Table(numberOfPlayers, advancedRules);
        //mancano i character
    }

    private List<Player> createListOfPlayers(boolean advancedRules, List<String> usernames, List<Color> colors, List<Wizard> wizards) {
        List<Player> newPlayers = new ArrayList<>();
        if (!advancedRules) {
            //istanzia il GameModel per le regole da principianti
            if (numberOfPlayers == 2) {
                for (int i = 0; i < usernames.size(); i++) {
                    Entrance entrance = createEntrance(numberOfPlayers);
                    newPlayers.add(createTwoPlayer(entrance, usernames.get(i), colors.get(i), wizards.get(i)));
                }
            } else {
                for (int i = 0; i < usernames.size(); i++) {
                    Entrance entrance = createEntrance(numberOfPlayers);
                    newPlayers.add(createThreePlayer(entrance, usernames.get(i), colors.get(i), wizards.get(i)));
                }
            }
        } else {
            //istanzio le regole per giocatori esperti
            if (numberOfPlayers == 2) {
                for (int i = 0; i < usernames.size(); i++) {
                    Entrance entrance = createEntrance(numberOfPlayers);
                    newPlayers.add(createTwoPlayerAdvanced(entrance, usernames.get(i), colors.get(i), wizards.get(i)));
                }
            } else {
                for (int i = 0; i < usernames.size(); i++) {
                    Entrance entrance = createEntrance(numberOfPlayers);
                    newPlayers.add(createThreePlayerAdvanced(entrance, usernames.get(i), colors.get(i), wizards.get(i)));
                }
            }
        }
        return newPlayers;
    }

    private Entrance createEntrance(int numberOfPlayers) {
        if (numberOfPlayers == 2) {
            return new Entrance(7);
        }
        //in case of 3 players
        return new Entrance(9);
    }

    private Player createTwoPlayer(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 0, myWizard, 8, myEntrance);
    }

    private Player createThreePlayer(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 0, myWizard, 6, myEntrance);
    }

    private Player createTwoPlayerAdvanced(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 1, myWizard, 8, myEntrance);
    }

    private Player createThreePlayerAdvanced(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 1, myWizard, 6, myEntrance);
    }

    public void fillClouds() {
        StudentBucket sb = StudentBucket.getInstance();
        List<Student> newStudentsOnCloud;
        for (Cloud c : table.getClouds()) {
            newStudentsOnCloud = new ArrayList<>();
            for (int i = 0; i < c.getCapacity(); i++) {
                try {
                    newStudentsOnCloud.add(sb.generateStudent());
                } catch (StudentsOutOfStockException ex) {
                    if (checkEndGame()) {
                        findWinner();
                    } else {
                        ex.printStackTrace();
                    }
                }
            }
            c.addStudents(newStudentsOnCloud);
        }
    }

    public void playAssistant(int indexOfAssistant) {
        players.get(currPlayer).setAssistantCard(players.get(currPlayer).getAssistantDeck().get(indexOfAssistant));
    }

    public Table getTable() {
        return table;
    }

    public void establishRoundOrder() {

    }

    public void moveStudents(StudentContainer source, StudentContainer destination, List<Creature> creatures) {
        List<Student> newStudents = new ArrayList<>();
        for(Creature c : creatures){
            newStudents.add(source.removeStudent(c));
        }
        destination.addStudents(newStudents);
    }

    public boolean moveMotherNature(int jumps) {
        return true;
    }

    public boolean checkEndGame() {
        return false;
    }

    /**
     * Winning conditions based on number of towers and of professors.
     *
     * @return is the player that has won the game.
     */

    public Player findWinner() {
        Player ans = players.get(0);
        for (Player p : players) {
            if (p.getTowers() < ans.getTowers()) {
                ans = p;
            } else if (p.getTowers() == ans.getTowers() &&
                    p.getProfessors().size() > ans.getProfessors().size()) {
                ans = p;
            }
        }
        return ans;
    }

    public void checkTower() {

    }

    public void checkNeighborIsland() {
        boolean left = false, right = false;
        Island currentIsland=table.getCurrentIsland();
        Island nextIsland=table.getNextIsland();
        Island prevIsland=table.getPrevIsland();

        if(prevIsland.getNumberOfTowers()>0 && prevIsland.getColorOfTowers().equals(currentIsland.getColorOfTowers())){
            left=true;
        }
        if(nextIsland.getNumberOfTowers()>0 && nextIsland.getColorOfTowers().equals(currentIsland.getColorOfTowers())){
            right=true;
        }

        if(right&&left){
            try{
                table.islandFusion("Both");
            }catch (GroupsOfIslandsException e){
                checkEndGame();
            }

        }else if(right){
            try{
                table.islandFusion("Right");
            }catch (GroupsOfIslandsException e){
                checkEndGame();
            }
        }else if(left){
            try{
                table.islandFusion("Left");
            }catch (GroupsOfIslandsException e){
                checkEndGame();
            }
        }
    }


    public void modifyCostOfCharacter(Character character) {

    }

    @Override
    public void addNoEntry(int indexOfIsland) {

    }

    @Override
    public void evaluateInfluence() {

    }

    @Override
    public void setPostmanMovements(int numberOfSteps) {

    }

    @Override
    public void thiefEffect(Creature creature) {

    }

    @Override
    public void moveStudents(StudentContainer source, StudentContainer destination, Creature creature) {

    }

    @Override
    public void setInfluenceCharacter(int typeOfInfluenceCharacter) {

    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrPlayer() {
        return currPlayer;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public Character getPlayedCharacter() {
        return playedCharacter;
    }
}
