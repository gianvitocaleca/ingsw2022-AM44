package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.characters.ConcreteCharacterCreator;
import it.polimi.ingsw.model.characters.Postman;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.gameboard.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.studentcontainers.*;
import it.polimi.ingsw.model.students.*;

import java.util.*;

public class GameModel extends Observable implements Playable, Observer {

    private final Table table;
    private final int numberOfPlayers;
    private List<Player> players;
    private int currentPlayerIndex;
    private List<Character> characters;
    private int playedCharacter;
    private InfluenceEvaluator evaluator;
    private int postmanMovements;
    private boolean isFarmer;

    public GameModel(boolean advancedRules, List<String> usernames, int numberOfPlayers, List<Color> colors, List<Wizard> wizards) {
        //aggiungere un giocatore alla volta per il problema del colore e del mago?
        players = createListOfPlayers(advancedRules, usernames, colors, wizards);
        this.numberOfPlayers = numberOfPlayers;
        //Il primo a giocare la carta assistente a inizio partita sarà il primo che ha fatto log in e di conseguenza il player in posizione zero
        currentPlayerIndex = 0;
        this.table = new Table(numberOfPlayers, advancedRules);
        this.evaluator = new StandardEvaluator();
        characters = createListOfCharacters();
        postmanMovements = 0;
        playedCharacter = -1;
    }

    public void setCharacterTestForMVC() {
        characters.remove(0);
        characters.set(0, new Postman(Name.MAGICPOSTMAN, this));
    }

    private void askForRequest() {
        setChanged();
        notifyObservers(characters.get(playedCharacter).getName());
    }

    @Override
    public void setFarmer() {
        isFarmer = true;
    }

    public void effect(CharactersParameters answer) {
        characters.get(playedCharacter).effect(answer);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    /**
     * Creates three random characters for the game
     *
     * @return is the list of characters
     */

    private List<Character> createListOfCharacters() {
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        List<Character> chars = new ArrayList<Character>();
        List<Name> names = new ArrayList<Name>(Arrays.asList(Name.values()));
        for (int i = 0; i < 3; i++) {
            chars.add(ccc.createCharacter(names.remove(new Random().nextInt(names.size())), this));
        }
        return chars;
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
        players.get(currentPlayerIndex).setAssistantCard(players.get(currentPlayerIndex).getAssistantDeck().get(indexOfAssistant));
    }

    public void playCharacter(int indexOfCharacter) {

        Player currentPlayer = players.get(currentPlayerIndex);
        Character currentCharacter = characters.get(indexOfCharacter);
        //get character cost (it already handles the updated cost)
        int removedCoins = currentCharacter.getCost();
        //player pays for the character
        currentPlayer.removeCoin(removedCoins);
        currentCharacter.setUpdatedCost();
        //table gets the coins from the player
        table.addCoins(removedCoins);
        //play character
        playedCharacter = indexOfCharacter;
        askForRequest();
    }

    public Table getTable() {
        return table;
    }

    /**
     * put in order players according to the assistant card played by each player.
     */
    public void establishRoundOrder() {
        Collections.sort(players, (p1, p2) -> {
            if (p1.getLastPlayedCard().getValue() < p2.getLastPlayedCard().getValue()) return -1;
            else if (p1.getLastPlayedCard().getValue() > p2.getLastPlayedCard().getValue()) return 1;
            else return 0;
        });
    }

    public void moveStudents(StudentContainer source, StudentContainer destination, List<Creature> creatures) {
        List<Student> newStudents = new ArrayList<>();
        for (Creature c : creatures) {
            newStudents.add(source.removeStudent(c));
        }
        destination.addStudents(newStudents);
    }

    public boolean moveMotherNature(int jumps) {
        jumps += postmanMovements;
        if (jumps < ((table.getIslands().size() - 1) - table.getMnPosition())) {
            table.getMotherNature().setCurrentIsland(jumps + table.getMnPosition());
        } else {
            int mnFuturePos = jumps - (table.getIslands().size() - 1 - table.getMnPosition());
            table.getMotherNature().setCurrentIsland(mnFuturePos - 1);
        }
        return true;
    }

    public boolean checkEndGame() {
        boolean gameEnded = false;
        for (Player p : players) {
            if (p.getTowers() == 0) {
                gameEnded = true;
            } else if (p.getAssistantDeck().size() == 0) {
                gameEnded = true;
            }
        }
        if (table.getIslands().size() == 3) {
            gameEnded = true;
        }
        StudentBucket sb = StudentBucket.getInstance();
        try {
            Student s = sb.generateStudent();
        } catch (StudentsOutOfStockException ex) {
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


    @Override
    public void addNoEntry(int indexOfIsland) {
        table.getIslands().get(indexOfIsland).addNoEntry();
    }

    /**
     * Evaluates the influence on the current island
     * If island has NoEntry does nothing and removes one NoEntry
     */

    @Override
    public void evaluateInfluence() {
        evaluator.evaluateInfluence(this);
    }

    /**
     * Sets the number of steps for the postman character
     *
     * @param numberOfSteps is chosen by the player
     */
    @Override
    public void setPostmanMovements(int numberOfSteps) {
        postmanMovements = numberOfSteps;
    }

    /**
     * Removes 3 students from all the player's dining room
     * If less than 3 students are present, remove all
     *
     * @param creature is the type of student to be removed
     */
    @Override
    public void thiefEffect(Creature creature) {
        StudentBucket sb = StudentBucket.getInstance();
        for (Player p : players) {
            for (int i = 0; i < 3 && p.getDiningRoom().getNumberOfStudentsByCreature(creature) > 0; i++) {
                //removes the student from the dining room
                Student removedStudent = p.getDiningRoom().removeStudent(creature);
                //gives the student back to the bucket
                sb.putBackCreature(removedStudent.getCreature());
            }
        }
    }

    @Override
    public void moveStudents(StudentContainer source, StudentContainer destination, Creature creature) {

    }

    @Override
    public void setInfluenceEvaluator(InfluenceEvaluator evaluator) {
        this.evaluator = evaluator;
    }


    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public int getPlayedCharacter() {
        return playedCharacter;
    }


}
