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
    private final Table table;
    private final int numberOfPlayers;
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

    /**
     * put in order players according to the assistant card played by each player.
     */
    public void establishRoundOrder(){
        Collections.sort(players, (p1,p2) -> {
            if(p1.getLastPlayedCard().getValue()<p2.getLastPlayedCard().getValue()) return -1;
            else if(p1.getLastPlayedCard().getValue()>p2.getLastPlayedCard().getValue()) return 1;
            else return 0;
        } );
    }

    public void moveStudents(StudentContainer source, StudentContainer destination, List<Creature> creatures) {
        List<Student> newStudents = new ArrayList<>();
        for (Creature c : creatures) {
            newStudents.add(source.removeStudent(c));
        }
        destination.addStudents(newStudents);
    }

    public boolean moveMotherNature(int jumps) {
        if(jumps<((table.getIslands().size()-1)-table.getMnPosition())){
            table.getMotherNature().setCurrentIsland(jumps+table.getMnPosition());
        }else{
            int mnFuturePos = jumps-(table.getIslands().size()-1- table.getMnPosition());
            table.getMotherNature().setCurrentIsland(mnFuturePos-1);
        }
        return true;
    }

    public boolean checkEndGame() {
        boolean gameEnded = false;
        for(Player p: players){
            if(p.getTowers()==0){
                gameEnded=true;
            }else if(p.getAssistantDeck().size()==0){
                gameEnded = true;
            }
        }
        if(table.getIslands().size()==3){
            gameEnded = true;
        }
        StudentBucket sb = StudentBucket.getInstance();
        try{
            Student s = sb.generateStudent();
        }catch(StudentsOutOfStockException ex){
            gameEnded = true;
        }


        return gameEnded;
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

    /**
     * Moves the professors to the correct players
     * Creates the professors if not present
     */

    private void checkProfessor() {
        for (Creature c : Creature.values()) {
            Optional<Player> hasprofessor = Optional.empty();
            Player hasmorestudents = players.get(0);
            for (Player p : players) {
                if (p.getProfessors().size() > 0) {
                    for (Professor prof : p.getProfessors()) {
                        if (prof.getCreature().equals(c)) {
                            hasprofessor = Optional.of(p);
                            break;
                        }
                    }
                }
                if (p.getDiningRoom().getNumberOfStudentsByCreature(c) >
                        hasmorestudents.getDiningRoom().getNumberOfStudentsByCreature(c)) {
                    hasmorestudents = p;
                }
            }
            if (hasprofessor.isPresent()) {
                if (!hasprofessor.get().equals(hasmorestudents)) {
                    if (hasprofessor.get().getDiningRoom().getNumberOfStudentsByCreature(c) <
                            hasmorestudents.getDiningRoom().getNumberOfStudentsByCreature(c)) {
                        hasmorestudents.addProfessor(hasprofessor.get().removeProfessor(c));
                    }
                }

            } else {
                hasmorestudents.addProfessor(new Professor(c));
            }
        }
    }

    public void checkTower() {

    }

    public void checkNeighborIsland() {
        boolean left = false, right = false;
        Island currentIsland = table.getCurrentIsland();
        Island nextIsland = table.getNextIsland();
        Island prevIsland = table.getPrevIsland();

        if (prevIsland.getNumberOfTowers() > 0 && prevIsland.getColorOfTowers().equals(currentIsland.getColorOfTowers())) {
            left = true;
        }
        if (nextIsland.getNumberOfTowers() > 0 && nextIsland.getColorOfTowers().equals(currentIsland.getColorOfTowers())) {
            right = true;
        }

        if (right && left) {
            try {
                table.islandFusion("Both");
            } catch (GroupsOfIslandsException e) {
                checkEndGame();
            }

        } else if (right) {
            try {
                table.islandFusion("Right");
            } catch (GroupsOfIslandsException e) {
                checkEndGame();
            }
        } else if (left) {
            try {
                table.islandFusion("Left");
            } catch (GroupsOfIslandsException e) {
                checkEndGame();
            }
        }
    }


    public void modifyCostOfCharacter(Character character) {

    }

    @Override
    public void addNoEntry(int indexOfIsland) {

    }

    /**
     * Evaluates the influence on the current island
     * If island has NoEntry does nothing and removes one NoEntry
     */

    @Override
    public void evaluateInfluence() {
        Island ci = table.getCurrentIsland();
        if (ci.getNumberOfNoEntries() == 0) {
            Optional<Player> hasmoreinfluece = Optional.empty();
            int influence = 0;
            for (Player p : players) {
                int sum = 0;
                //if player has professor add the relative influence
                if (p.getProfessors().size() > 0) {
                    for (Professor prof : p.getProfessors()) {
                        sum += ci.getNumberOfStudentsByCreature(prof.getCreature());
                    }
                }
                //if player has towers on the island add the relative influence
                if (ci.getNumberOfTowers() > 0 &&
                        p.getMyColor().equals(ci.getColorOfTowers())) {
                    sum += ci.getNumberOfTowers();
                }
                //if player has more influence update
                if (sum > influence) {
                    hasmoreinfluece = Optional.of(p);
                    influence = sum;
                }
            }
            //if the player who has more influence has changed
            if (!hasmoreinfluece.get().getMyColor().equals(ci.getColorOfTowers())) {
                //swap towers
                if (ci.getNumberOfTowers() > 0) {
                    for (Player p : players) {
                        //removes towers from the player who has influence
                        if (p.getMyColor().equals(hasmoreinfluece.get().getMyColor())) {
                            p.removeTowers(ci.getNumberOfTowers());
                        }
                        //adds towers to the player who had towers on the island
                        if (p.getMyColor().equals(ci.getColorOfTowers())) {
                            p.addTowers(ci.getNumberOfTowers());
                        }
                    }
                } else {
                    //removes one tower from the player that has conquered the island
                    for (Player p : players) {
                        if (p.getMyColor().equals(hasmoreinfluece.get().getMyColor())) {
                            p.removeTowers(1);
                        }
                    }
                }
                //change the color of the towers on the island
                ci.setColorOfTowers(hasmoreinfluece.get().getMyColor());
                //check the neighbor islands
                checkNeighborIsland();
            }
        } else {
            ci.removeNoEntry();
        }

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

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setCurrPlayer(int currPlayer) {
        this.currPlayer = currPlayer;
    }
}
