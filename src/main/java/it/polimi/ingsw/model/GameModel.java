package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.Entrance;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.network.server.CharacterInformation;
import it.polimi.ingsw.controller.events.ShowModelEvent;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.network.server.networkMessages.payloads.ShowModelPayload;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.ConcreteCharacterCreator;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.evaluators.InfluenceEvaluator;
import it.polimi.ingsw.model.evaluators.StandardEvaluator;
import it.polimi.ingsw.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.model.exceptions.CloudAlreadySelectedException;
import it.polimi.ingsw.model.exceptions.PlanningPhaseEndedException;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.gameboard.MotherNature;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Assistant;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Professor;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import it.polimi.ingsw.network.server.handlers.MessageHandler;

import javax.swing.event.EventListenerList;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class GameModel implements Playable {
    public static final int NUMBER_OF_CHARACTERS = 3;
    public static final int THREE_PLAYERS_CAPACITY = 9;
    public static final int TWO_PLAYERS_CAPACITY = 7;
    public static final int TWO_PLAYERS_NUMBER_OF_TOWERS = 8;
    public static final int THREE_PLAYERS_NUMBER_OF_TOWERS = 6;
    public static final int ADVANCED_RULES_STARTING_COINS = 1;
    private Table table;
    private final int numberOfPlayers;
    private List<Player> players;
    private int currentPlayerIndex;
    private final List<Character> characters;
    private int playedCharacter;
    private InfluenceEvaluator evaluator;
    private int postmanMovements;
    private boolean isFarmer;
    private final boolean advancedRules;
    private boolean lastRound = false;
    private final EventListenerList listeners;
    private SecureRandom rand = new SecureRandom();

    /**
     * It is the model of the game
     * @param advancedRules whether the game has advanced rules
     * @param usernames the list of the players
     * @param numberOfPlayers the number of players
     * @param colors the list of colors
     * @param wizards the list of wizards
     */
    public GameModel(boolean advancedRules, List<String> usernames, int numberOfPlayers, List<Color> colors, List<Wizard> wizards) {
        this.listeners = new EventListenerList();
        this.advancedRules = advancedRules;
        this.table = new Table(numberOfPlayers, advancedRules);
        players = createListOfPlayers(advancedRules, usernames, colors, wizards);
        this.numberOfPlayers = numberOfPlayers;
        currentPlayerIndex = 0;
        this.evaluator = new StandardEvaluator();
        characters = createListOfCharacters();
        postmanMovements = 0;
        playedCharacter = -1;
    }

    /**
     * Used to add a no entry signal on an island
     * @param indexOfIsland is the given island
     */
    @Override
    public void addNoEntry(int indexOfIsland) {
        List<Island> islands = table.getIslands();
        islands.get(indexOfIsland).addNoEntry();
        table.setIslands(islands);
        ShowModelPayload payload = showModelPayloadCreator();
        payload.setUpdateIslands();
        showModel(payload);
    }

    /**
     * Evaluates the influence on the current island
     * If island has NoEntry does nothing and removes one NoEntry
     */
    @Override
    public void evaluateInfluence() throws GameEndedException {
        evaluator.evaluateInfluence(this);
        //if GroupsOfIslandsException, checkNeighborIsland returns false
        if (!table.checkNeighborIsland()) {
            throw new GameEndedException();
        }
        ShowModelPayload payload = showModelPayloadCreator();
        payload.setUpdateIslands();
        payload.setUpdateMotherNature();
        showModel(payload);
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
     *
     * @param lastRound whether the current round will be the last
     */
    @Override
    public void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    /**
     * Moves the students from the source to the destination
     *
     * @param source      is the source of the moving
     * @param destination is the destination of the moving
     * @param creatures   are the creatures of the students
     * @return true if moveStudents was executed correctly
     */
    @Override
    public boolean moveStudents(StudentContainer source, StudentContainer destination, List<Creature> creatures) {
        List<Student> newStudents = new ArrayList<>();
        List<Creature> sourceCreatures = source.getStudents().stream().map(s -> s.getCreature()).collect(Collectors.toList());
        if (sourceCreatures.containsAll(creatures)) {
            for (Creature c : creatures) {
                newStudents.add(source.removeStudent(c));
            }
            destination.addStudents(newStudents);
            coinGiver();
            checkProfessor();
            return true;
        }
        return false;
    }

    /**
     * Used to give the players the coins
     */
    public void coinGiver() {
        boolean update = false;
        for (Creature c : Creature.values()) {
            if (advancedRules) {
                if (players.get(currentPlayerIndex).checkCoinGiver(c)) {
                    table.removeCoin();
                    update = true;
                }
            }
        }
        if (update) {
            ShowModelPayload payload = showModelPayloadCreator();
            payload.setUpdateCoinReserve();
            showModel(payload);
        }

    }

    /**
     * Used to set the influence evaluator.
     * Used with advanced rules.
     * @param evaluator the type of evaluator to use.
     */
    @Override
    public void setInfluenceEvaluator(InfluenceEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * Used by the farmer character
     */
    @Override
    public void setFarmer() {
        isFarmer = true;
    }

    /**
     * Used to remove the farmer effect
     */
    public void resetFarmer() {
        isFarmer = false;
    }

    /**
     * Used by the herald effect
     * @param indexIsland is the selected island
     * @return whether the effect was successful
     * @throws GameEndedException is thrown if an end game condition is met
     */
    @Override
    public boolean setHeraldIsland(int indexIsland) throws GameEndedException {
        if (indexIsland < table.getIslands().size()) {
            int originalMnPosition = table.getMnPosition();
            MotherNature mn = table.getMotherNature();
            mn.setCurrentIsland(indexIsland);
            table.setMotherNature(mn);
            evaluateInfluence();
            mn.setCurrentIsland(originalMnPosition);
            table.setMotherNature(mn);
            return true;
        }
        return false;
    }

    /**
     * This method sets lastRound=true when table.fillClouds returns false due to StudentsOutOfStockException
     */
    public void fillClouds() {
        if (!(table.fillClouds())) {
            lastRound = true;
        }
        ShowModelPayload modelUpdate = showModelPayloadCreator();
        modelUpdate.setUpdateClouds();
        showModel(modelUpdate);
    }

    public void resetAssistants(){
        for(Player p: players){
            p.setAssistantPlayed(false);
        }
    }

    /**
     * Used for the cloud selection command
     * @param indexOfCloud is the selected cloud
     * @return whether the operation was successful
     * @throws CloudAlreadySelectedException is thrown if another player has already selected the provided cloud
     */
    public boolean moveFromSelectedCloud(int indexOfCloud) throws CloudAlreadySelectedException {
        if (indexOfCloud >= table.getClouds().size() || indexOfCloud < 0) {
            return false;
        } else {
            if (table.getClouds().get(indexOfCloud).getStudents().size() == 0) {
                throw new CloudAlreadySelectedException();
            }
        }
        List<Creature> creatures = table.getClouds().get(indexOfCloud).getStudents().stream().map(s -> s.getCreature()).toList();
        List<Cloud> clouds = table.getClouds();
        Entrance currentPlayerEntrance = players.get(currentPlayerIndex).getEntrance();
        moveStudents(clouds.get(indexOfCloud), currentPlayerEntrance, creatures);
        table.setClouds(clouds);
        players.get(currentPlayerIndex).setEntrance(currentPlayerEntrance);
        resetFarmer();
        findNextPlayer();
        postmanMovements = 0;
        ShowModelPayload payload = showModelPayloadCreator();
        payload.setUpdateClouds();
        payload.setUpdatePlayersEntrance();
        showModel(payload);
        return true;
    }

    /**
     * Used to change the current player
     */
    public void findNextPlayer() {
        if (currentPlayerIndex < numberOfPlayers - 1) {
            currentPlayerIndex++;
        } else {
            currentPlayerIndex = 0;
        }
        setInfluenceEvaluator(new StandardEvaluator());
    }

    /**
     * Used during the planning phase of the turn
     * @param indexOfAssistant is the selected assistant
     * @return whether the operation was successful
     * @throws AssistantAlreadyPlayedException is thrown if another player already played the given assistant
     * @throws PlanningPhaseEndedException is thrown if the planning phase has ended
     */
    public boolean playAssistant(int indexOfAssistant) throws AssistantAlreadyPlayedException, PlanningPhaseEndedException{
        if (indexOfAssistant < 0 || indexOfAssistant >= players.get(currentPlayerIndex).getAssistantDeck().size()) {
            return false;
        }
        List<Assistant> playedAssistants = new ArrayList<>();

        if (currentPlayerIndex != 0) {
            for (int i = currentPlayerIndex - 1; i >= 0; i--) {
                if(players.get(i).isAssistantPlayed()){
                    playedAssistants.add(players.get(i).getLastPlayedCard());
                }
            }

            for (Assistant a : playedAssistants) {
                if (a.getName().equals(players.get(currentPlayerIndex).getAssistantDeck().get(indexOfAssistant).getName())) {
                    throw new AssistantAlreadyPlayedException();
                }
            }
        }

        players.get(currentPlayerIndex).setAssistantCard(indexOfAssistant);
        players.get(currentPlayerIndex).setAssistantPlayed(true);
        ShowModelPayload modelUpdate = showModelPayloadCreator();
        modelUpdate.setUpdatePlayersAssistant();
        showModel(modelUpdate);

        if(players.get(currentPlayerIndex).getAssistantDeck().size() == 0) {
                setLastRound(true);
        }

        findNextPlayer();

        if (currentPlayerIndex == 0) {
            throw new PlanningPhaseEndedException();
        }

        return true;
    }


    private void establisher(List<Player> goodPlayers) {
        Collections.sort(goodPlayers, (p1, p2) -> {
                if (p1.getLastPlayedCard().getValue() < p2.getLastPlayedCard().getValue()) return -1;
                else if (p1.getLastPlayedCard().getValue() > p2.getLastPlayedCard().getValue()) return 1;
                else return 0;
        });
        currentPlayerIndex = 0;
    }

    /**
     * put in order players according to the assistant card played by each player.
     */
    public void establishRoundOrder(){
        List<Player> badPlayers = new ArrayList<>();
        List<Player> goodPlayers = new ArrayList<>();
        for(int i = 0; i< players.size(); i++){
            if(players.get(i).isAssistantPlayed()){
                goodPlayers.add(players.get(i));
            }else{
                badPlayers.add(players.get(i));
            }
        }
        establisher(goodPlayers);
        goodPlayers.addAll(badPlayers);
        players = goodPlayers;
    }

    /**
     * sums the selected postman movements to the number of jumps
     * mnFuturePos uses the module function to establish the correct future position of MotherNature
     *
     * @param jumps number of steps that MotherNature has to do
     * @return true if mother nature changed her position
     */

    public boolean moveMotherNature(int jumps) throws GameEndedException {
        if (checkJumps(jumps)) {
            int j = jumps + postmanMovements;
            table.moveMotherNature(j);
            evaluateInfluence();
            return true;
        }
        return false;
    }

    /**
     * verifies if the number of steps is valid
     *
     * @param jumps is the number of steps the player want mother nature to do
     * @return true if the assistant card played by the client allows mother nature's movement
     */
    private boolean checkJumps(int jumps) {
        if (jumps <= 0) {
            return false;
        }
        return players.get(currentPlayerIndex).getLastPlayedCard().getMovements() >= jumps;
    }

    /**
     * verifies if the character can be played and if it can be played this method removes coin
     * from the player and gives them to coinReserve.
     *
     * @param indexOfCharacter is the position of the character played
     * @return true if the character can be played and paid.
     */
    public boolean playCharacter(int indexOfCharacter) {

        if (indexOfCharacter < 0 || indexOfCharacter > 2) {
            throw new IndexOutOfBoundsException();
        }


        if (characters.get(indexOfCharacter).canBePlayed(players.get(currentPlayerIndex).getMyCoins())) {
            //get character cost (it already handles the updated cost)
            int removedCoins = characters.get(indexOfCharacter).getCost();
            //player pays for the character
            players.get(currentPlayerIndex).removeCoin(removedCoins);
            characters.get(indexOfCharacter).setUpdatedCost();

            //table gets the coins from the player
            table.addCoins(removedCoins);
            //play character
            playedCharacter = indexOfCharacter;
            return true;
        }

        return false;
    }

    /**
     *
     * @return is the number of selected jumps for the postman character
     */
    public int getPostmanMovements() {
        return postmanMovements;
    }

    /**
     *
     * @return whether the current turn is the last
     */
    public boolean checkIfLastRound() {
        return lastRound;
    }

    /**
     * Used to check the end game conditions
     * @return whether the game has ended
     */
    public boolean checkEndGame() {
        boolean gameEnded = false;
        for (Player p : players) {
            if (p.getTowers() == 0 || p.getAssistantDeck().isEmpty()) {
                gameEnded = true;
            }
        }
        if (table.getIslands().size() == 3) {
            gameEnded = true;
        }
        return gameEnded;
    }

    /**
     * Used by the herbalist effect.
     * @return is the number of no entries on the character card
     */
    @Override
    public int getDeactivators() {
        return table.getDeactivators();
    }

    /**
     * Used by the herbalist effect.
     * @param deactivators is the number of no entries to be set
     * @return whether the operation was successful
     */
    @Override
    public boolean setDeactivators(int deactivators) {
        table.setDeactivators(deactivators);
        return true;
    }

    /**
     *
     * @return is the student bucket
     */
    @Override
    public StudentBucket getBucket() {
        return table.getBucket();
    }

    /**
     *
     * @param bucket is the student bucket to be set
     */
    @Override
    public void setBucket(StudentBucket bucket) {
        table.setBucket(bucket);
    }

    /**
     *
     * @return is a copy of the game table
     */
    public Table getTable() {
        Table temp = new Table(numberOfPlayers, advancedRules);
        temp.setMotherNature(table.getMotherNature());
        temp.setClouds(table.getClouds());
        temp.setCoinReserve(table.getCoinReserve());
        temp.setIslands(table.getIslands());
        temp.setDeactivators(table.getDeactivators());
        return temp;
    }

    /**
     * Winning conditions based on number of towers and of professors.
     *
     * @return is the player that has won the game.
     */
    public Player findWinner() {
        Player ans = players.get(0);
        for (Player p : players) {
            if (p.getTowers() < ans.getTowers() || (p.getTowers() == ans.getTowers() &&
                    p.getProfessors().size() > ans.getProfessors().size())) {
                ans = p;
            }
        }
        return ans;
    }

    /**
     *
     * @param table is the game table to be set
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     *
     * @return is a copy list of the players
     */
    public List<Player> getPlayers() {
        List<Player> temp = new ArrayList<>();
        for (Player p : players) {
            Player temPlayer = new Player(p.getUsername(), p.getMyColor(), p.getMyCoins(), p.getWizard(), p.getTowers(), p.getEntrance());
            temPlayer.setAssistantDeck(p.getAssistantDeck());
            temPlayer.setDiningRoom(p.getDiningRoom());
            temPlayer.setLastPlayedCards(p.getLastPlayedCards());
            temPlayer.setGivenCoins(p.getGivenCoins());
            temPlayer.setProfessors(p.getProfessors());
            temp.add(temPlayer);
        }
        return temp;
    }

    /**
     *
     * @param players is the list of players to be set
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     *
     * @return is the index of the current player
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     *
     * @param currentPlayerIndex is the index of the current player to be set
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    /**
     *
     * @return is the number of players in the game
     */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Used with advanced rules.
     * @return is the list of characters in the game
     */
    public List<Character> getCharacters() {
        return characters;
    }

    /**
     * Used with advanced rules.
     * @return is the character played in this turn
     */
    public int getPlayedCharacter() {
        return playedCharacter;
    }

    /**
     * Used to create the players at the beginning of the game
     * @param advancedRules whether the game has advanced rules
     * @param usernames the list of player's names
     * @param colors the list of player's colors
     * @param wizards the list of player's wizards
     * @return the list of created players
     */
    private List<Player> createListOfPlayers(boolean advancedRules, List<String> usernames, List<Color> colors, List<Wizard> wizards) {
        List<Player> newPlayers = new ArrayList<>();
        if (!advancedRules) {
            //beginner rules
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
            //expert game rules
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

    /**
     * Creates three random characters for the game
     *
     * @return is the list of characters
     */
    private List<Character> createListOfCharacters() {
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        List<Character> characterList = new ArrayList<>();
        List<Name> names = new ArrayList<>(Arrays.asList(Name.values()));
        //dopo toglilo!!
        characterList.add(ccc.createCharacter(Name.KNIGHT,this));
        for (int i = 1; i < NUMBER_OF_CHARACTERS; i++) {
            characterList.add(ccc.createCharacter(names.remove(rand.nextInt(names.size())), this));
        }
        return characterList;
    }

    /**
     * Used to create the player's entrance
     * @param numberOfPlayers is the number of players in the game
     * @return is the created entrance
     */
    private Entrance createEntrance(int numberOfPlayers) {
        List<Student> students = new ArrayList<>();
        StudentBucket bucket = table.getBucket();

        if (numberOfPlayers == 2) {
            return populateEntrance(students, bucket, TWO_PLAYERS_CAPACITY);
        }
        //in case of 3 players
        return populateEntrance(students, bucket, THREE_PLAYERS_CAPACITY);
    }

    /**
     * Used to populate an empty entrance with students
     * @param students is the list of students to put in the entrance
     * @param bucket is the student bucket
     * @param playersCapacity is the capacity of the student container
     * @return is the populated entrance
     */
    private Entrance populateEntrance(List<Student> students, StudentBucket bucket, int playersCapacity) {
        Entrance entrance = new Entrance(playersCapacity);
        for (int i = 0; i < playersCapacity; i++) {
            try {
                students.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException e) {
                setLastRound(true);
            }
        }
        entrance.addStudents(students);
        table.setBucket(bucket);
        return entrance;
    }

    /**
     *
     * @return is the created player for a two player normal game
     */
    private Player createTwoPlayer(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 0, myWizard, TWO_PLAYERS_NUMBER_OF_TOWERS, myEntrance);
    }

    /**
     *
     * @return is the created player for a three player normal game
     */
    private Player createThreePlayer(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 0, myWizard, THREE_PLAYERS_NUMBER_OF_TOWERS, myEntrance);
    }

    /**
     *
     * @return is the created player for a two player advanced game
     */
    private Player createTwoPlayerAdvanced(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, ADVANCED_RULES_STARTING_COINS, myWizard, TWO_PLAYERS_NUMBER_OF_TOWERS, myEntrance);
    }

    /**
     *
     * @return is the created player for a three player advanced game
     */
    private Player createThreePlayerAdvanced(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, ADVANCED_RULES_STARTING_COINS, myWizard, THREE_PLAYERS_NUMBER_OF_TOWERS, myEntrance);
    }

    /**
     * Moves the professors to the correct players
     * Creates the professors if not present
     */
    public void checkProfessor() {
        for (Creature c : Creature.values()) {

            Optional<Player> professorOwner = Optional.empty();


            for (int i = 0; i < players.size(); i++) {
                if (hasProfessor(i, c)) {
                    professorOwner = Optional.of(players.get(i));
                    break;
                }

            }
            if (professorOwner.isPresent()) {
                Player profOwner = professorOwner.get();
                for (Player p : players) {
                    if (isFarmer && players.get(currentPlayerIndex).getUsername().equals(p.getUsername())) {
                        if (p.getDiningRoom().getNumberOfStudentsByCreature(c) >=
                                profOwner.getDiningRoom().getNumberOfStudentsByCreature(c)) {
                            profOwner = p;
                        }
                    } else if (p.getDiningRoom().getNumberOfStudentsByCreature(c) >
                            profOwner.getDiningRoom().getNumberOfStudentsByCreature(c)) {
                        profOwner = p;
                    }
                }
                profOwner.addProfessor(professorOwner.get().removeProfessor(c));
            } else {
                String playerToGiveProfessorUsername = findWhoHasMoreStudents(c);
                for (Player p : players) {
                    if (p.getUsername().equals(playerToGiveProfessorUsername) && p.getDiningRoom().getNumberOfStudentsByCreature(c) > 0) {
                        p.addProfessor(new Professor(c));
                        break;
                    }
                }

            }
        }
        ShowModelPayload payload = showModelPayloadCreator();
        payload.setUpdatePlayersDiningRoom();
        payload.setUpdatePlayersEntrance();
        payload.setUpdateIslands();
        showModel(payload);
    }

    /**
     * Used to evaluate which player has the most students in the dining room.
     * @param c the type of student
     * @return is the player's name
     */
    private String findWhoHasMoreStudents(Creature c) {
        List<Player> playerList = getPlayers();
        Collections.sort(playerList, (p1, p2) -> {
            if (p1.getDiningRoom().getNumberOfStudentsByCreature(c) < p2.getDiningRoom().getNumberOfStudentsByCreature(c))
                return -1;
            else if (p1.getDiningRoom().getNumberOfStudentsByCreature(c) > p2.getDiningRoom().getNumberOfStudentsByCreature(c))
                return 1;
            else return 0;
        });
        return playerList.get(numberOfPlayers - 1).getUsername();
    }

    /**
     *
     * @param indexOfPlayer the index of the player
     * @param c the type of professor
     * @return whether the given player has the provided type of professor
     */
    private boolean hasProfessor(int indexOfPlayer, Creature c) {
        if (!players.get(indexOfPlayer).getProfessors().isEmpty()) {
            return players.get(indexOfPlayer).getProfessors().stream().filter(p -> p.getCreature().equals(c)).findFirst().isPresent();
        }
        return false;
    }

    /**
     * Used to conquer the current island
     * @param hasMoreInfluence the player with the most influence
     * @throws GameEndedException is thrown if an end game condition is met
     */
    public void conquerIsland(Player hasMoreInfluence) throws GameEndedException {

        Island currentIsland = table.getCurrentIsland();

        if (!hasMoreInfluence.getMyColor().equals(currentIsland.getColorOfTowers()) || currentIsland.getNumberOfTowers() == 0) {
            //swap towers
            if (currentIsland.getNumberOfTowers() > 0) {
                for (Player p : players) {
                    //Remove towers from the player who has influence
                    if (p.getMyColor().equals(hasMoreInfluence.getMyColor())) {
                        p.removeTowers(currentIsland.getNumberOfTowers());
                    }
                    //Add towers to the player who had towers on the island
                    if (p.getMyColor().equals(currentIsland.getColorOfTowers())) {
                        p.addTowers(currentIsland.getNumberOfTowers());
                    }
                }
            } else {
                //removes one tower from the player that has conquered the island
                for (Player p : players) {
                    if (p.getMyColor().equals(hasMoreInfluence.getMyColor())) {
                        p.removeTowers(1);
                    }
                }
            }
            //change the color of the towers on the island
            currentIsland.setColorOfTowers(hasMoreInfluence.getMyColor());
            table.setCurrentIsland(currentIsland);
            //check the neighbor islands
            if (!table.checkNeighborIsland()) {
                throw new GameEndedException();
            }
        }
    }

    /**
     * Used to play the characters effects
     * @param answer is the payload with the selected parameters
     * @return whether the effect was successful
     * @throws GameEndedException is thrown if an end game condition is met
     * @throws UnplayableEffectException is thrown if the provided parameters are illegal
     */
    public boolean effect(CharactersParametersPayload answer) throws GameEndedException, UnplayableEffectException {
        boolean temp = false;
        try {
            temp = characters.get(playedCharacter).effect(answer);
        } catch (UnplayableEffectException e) {
            //get character cost (it already handles the updated cost)
            characters.get(playedCharacter).unsetUpdatedCost();
            int toAddCoins = characters.get(playedCharacter).getCost();
            //player pays for the character
            for (int i = 0; i < toAddCoins; i++) {
                players.get(currentPlayerIndex).addCoin();
                table.removeCoin();
            }
            throw new UnplayableEffectException();
        }
        if (temp) {
            ShowModelPayload payload = showModelPayloadCreator();
            payload.setUpdateAll();
            showModel(payload);
        }
        return temp;
    }

    /**
     * Used to send a message to the players
     * @param modelUpdate is the show model payload
     */
    public void showModel(ShowModelPayload modelUpdate) {
        ShowModelEvent modelEvent = new ShowModelEvent(this, modelUpdate);
        for (MessageHandler listener : listeners.getListeners(MessageHandler.class)) {
            listener.eventPerformed(modelEvent);
        }
    }

    /**
     * Used to create the appropriate show model payload.
     * Used for incremental updates.
     * @return is the created payload
     */
    public ShowModelPayload showModelPayloadCreator() {
        ShowModelPayload showModelPayload = new ShowModelPayload(getPlayers(), getTable());
        showModelPayload.setCurrentPlayerUsername(players.get(getCurrentPlayerIndex()).getUsername());
        if (isAdvancedRules()) {
            showModelPayload.setAdvancedRules(isAdvancedRules());
            List<CharacterInformation> characterInfos = new ArrayList<>();
            List<Creature> creatureList;
            for (int i = 0; i < characters.size(); i++) {
                Character c = characters.get(i);
                characterInfos.add(new CharacterInformation(c.getName(), c.getCost(), i));
                if (c.getName().equals(Name.JOKER)) {
                    creatureList = new ArrayList<>();
                    for (Student s : c.getStudents()) {
                        creatureList.add(s.getCreature());
                    }
                    showModelPayload.setJokerCreatures(creatureList);
                } else if (c.getName().equals(Name.PRINCESS)) {
                    creatureList = new ArrayList<>();
                    for (Student s : c.getStudents()) {
                        creatureList.add(s.getCreature());
                    }
                    showModelPayload.setPrincessCreatures(creatureList);
                } else if (c.getName().equals(Name.MONK)) {
                    creatureList = new ArrayList<>();
                    for (Student s : c.getStudents()) {
                        creatureList.add(s.getCreature());
                    }
                    showModelPayload.setMonkCreatures(creatureList);
                } else if (c.getName().equals(Name.HERBALIST)) {
                    showModelPayload.setDeactivators(getDeactivators());
                }
            }
            showModelPayload.setCharacters(characterInfos);
        }

        return showModelPayload;
    }

    /**
     *
     * @param listener is the message handler listener
     */
    public void addListener(MessageHandler listener) {
        listeners.add(MessageHandler.class, listener);
    }

    /**
     *
     * @return whether the game has advanced rules
     */
    public boolean isAdvancedRules() {
        return advancedRules;
    }


}
