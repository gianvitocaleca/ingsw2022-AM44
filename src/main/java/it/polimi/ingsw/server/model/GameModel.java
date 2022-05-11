package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.events.ShowModelEvent;
import it.polimi.ingsw.server.networkMessages.CharactersParametersPayload;
import it.polimi.ingsw.server.networkMessages.ShowModelPayload;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.characters.CharacterInformation;
import it.polimi.ingsw.server.model.characters.ConcreteCharacterCreator;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.evaluators.InfluenceEvaluator;
import it.polimi.ingsw.server.model.evaluators.StandardEvaluator;
import it.polimi.ingsw.server.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.server.model.exceptions.CloudAlreadySelectedException;
import it.polimi.ingsw.server.model.exceptions.PlanningPhaseEndedException;
import it.polimi.ingsw.server.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.server.model.gameboard.MotherNature;
import it.polimi.ingsw.server.model.gameboard.Table;
import it.polimi.ingsw.server.model.player.Assistant;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.Professor;
import it.polimi.ingsw.server.model.studentcontainers.*;
import it.polimi.ingsw.server.model.students.Student;
import it.polimi.ingsw.server.model.students.StudentBucket;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import javax.swing.event.EventListenerList;
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
    private boolean advancedRules;
    private boolean lastRound = false;

    private EventListenerList listeners = new EventListenerList();

    public void addListener(MessageHandler listener) {
        listeners.add(MessageHandler.class, listener);
    }

    //Constructor
    public GameModel(boolean advancedRules, List<String> usernames, int numberOfPlayers, List<Color> colors, List<Wizard> wizards) {
        this.advancedRules = advancedRules;
        this.table = new Table(numberOfPlayers, advancedRules);
        players = createListOfPlayers(advancedRules, usernames, colors, wizards);
        this.numberOfPlayers = numberOfPlayers;
        //The first player to play assistant card at the beginning of the game will be the first to log in, which is in position 0
        currentPlayerIndex = 0;
        this.evaluator = new StandardEvaluator();
        characters = createListOfCharacters();
        postmanMovements = 0;
        playedCharacter = -1;
        populateMoverCharacter();
    }

    public boolean isAdvancedRules() {
        return advancedRules;
    }

    // region PLAYABLE OVERRIDE METHODS
    @Override
    public void addNoEntry(int indexOfIsland) {
        List<Island> islands = table.getIslands();
        islands.get(indexOfIsland).addNoEntry();
        table.setIslands(islands);
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
        StudentBucket sb = table.getBucket();
        int numberOfStudentsToRemove = 3;
        for (Player p : players) {
            //removes 3 or all the students of that creature
            int minNumberToRemove = Math.min(numberOfStudentsToRemove, p.getDiningRoom().getNumberOfStudentsByCreature(creature));
            for (int i = 0; i < minNumberToRemove; i++) {
                //removes the student from the dining room
                DiningRoom oldDiningRoom = p.getDiningRoom();
                Student removedStudent = oldDiningRoom.removeStudent(creature);
                p.setDiningRoom(oldDiningRoom);
                //gives the student back to the bucket
                sb.putBackCreature(removedStudent.getCreature());
            }
        }
        table.setBucket(sb);
        lastRound = false;
    }

    /**
     * Moves a student from the character to the dining room of current player
     *
     * @param providedSourceCreatures is the creature of the student
     * @return
     */
    @Override
    public boolean princessEffect(List<Creature> providedSourceCreatures) {
        List<Creature> sourceCreatures = table.getPrincess().getStudents().stream().map(s -> s.getCreature()).toList();

        if (sourceCreatures.containsAll(providedSourceCreatures)) {
            StudentContainer princess = table.getPrincess();
            DiningRoom currPlayerDiningRoom = players.get(currentPlayerIndex).getDiningRoom();
            moveStudents(princess, currPlayerDiningRoom, providedSourceCreatures);

            StudentBucket bucket = table.getBucket();
            try {
                princess.addStudent(bucket.generateStudent());
            } catch (StudentsOutOfStockException e) {
                lastRound = true;
            }
            table.setBucket(bucket);

            table.setPrincess(princess);
            players.get(currentPlayerIndex).setDiningRoom(currPlayerDiningRoom);
            return true;
        }
        return false;
    }

    /**
     * Moves a student from the character to an island of choice
     *
     * @param providedSourceCreatures is the creature of the student
     * @param islandIndex             is the chosen island
     * @return
     */
    @Override
    public boolean monkEffect(List<Creature> providedSourceCreatures, int islandIndex) {
        List<Creature> sourceCreatures = table.getMonk().getStudents().stream().map(s -> s.getCreature()).toList();

        if (sourceCreatures.containsAll(providedSourceCreatures)) {
            StudentContainer monk = table.getMonk();
            Island destination = table.getIslands().get(islandIndex);
            moveStudents(monk, destination, providedSourceCreatures);

            StudentBucket bucket = table.getBucket();
            try {
                monk.addStudent(bucket.generateStudent());
            } catch (StudentsOutOfStockException e) {
                lastRound = true;
            }
            table.setBucket(bucket);

            table.setMonk(monk);
            table.setIndexIsland(islandIndex, destination);
            return true;
        }
        return false;

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
            coinGiver(players.get(currentPlayerIndex));
            return true;
        }
        return false;
    }

    private void coinGiver(Player currPlayer) {
        for (Creature c : Creature.values()) {
            if (table.getCoinReserve() > 0) {
                if (currPlayer.checkCoinGiver(c)) {
                    table.removeCoin();
                }
            }
        }

    }

    @Override
    public void setInfluenceEvaluator(InfluenceEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public void setFarmer() {
        isFarmer = true;
    }

    @Override
    public boolean setHeraldIsland(int indexIsland) {
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

    //endregion

    //region PUBLIC METHODS

    //region PLANNING PHASE

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

        if (currentPlayerIndex < numberOfPlayers - 1) {
            currentPlayerIndex++;
        } else {
            currentPlayerIndex = 0;
        }
        return true;
    }

    public boolean playAssistant(int indexOfAssistant) throws AssistantAlreadyPlayedException, PlanningPhaseEndedException {
        if (indexOfAssistant < 0 || indexOfAssistant >= players.get(currentPlayerIndex).getAssistantDeck().size()) {
            return false;
        }
        List<Assistant> playedAssistants = new ArrayList<Assistant>();

        if (!(currentPlayerIndex == 0)) {
            for (int i = currentPlayerIndex - 1; i >= 0; i--) {
                playedAssistants.add(players.get(i).getLastPlayedCard());
            }

            for (Assistant a : playedAssistants) {
                if (a.getName().equals(players.get(currentPlayerIndex).getAssistantDeck().get(indexOfAssistant).getName())) {
                    throw new AssistantAlreadyPlayedException();
                }
            }
        }

        players.get(currentPlayerIndex).setAssistantCard(indexOfAssistant);
        ShowModelPayload modelUpdate = showModelPayloadCreator();
        modelUpdate.setUpdatePlayersAssistant();
        showModel(modelUpdate);
        if (currentPlayerIndex < numberOfPlayers - 1) {
            currentPlayerIndex++;
        } else {
            throw new PlanningPhaseEndedException();
        }
        return true;
    }

    //endregion

    //region ACTION PHASE

    /**
     * put in order players according to the assistant card played by each player.
     */
    public void establishRoundOrder() {
        Collections.sort(players, (p1, p2) -> {
            if (p1.getLastPlayedCard().getValue() < p2.getLastPlayedCard().getValue()) return -1;
            else if (p1.getLastPlayedCard().getValue() > p2.getLastPlayedCard().getValue()) return 1;
            else return 0;
        });
        currentPlayerIndex = 0;
    }

    //MOVE STUDENTS

    /**
     * Swaps the students between joker card and player entrance
     *
     * @param providedSourceCreatures      are the creatures that will be removed from the source and added to the destination
     * @param providedDestinationCreatures are the creatures that will be removed from the destination and added to the source
     * @return true if effect is correctly executed
     */
    @Override
    public boolean jokerEffect(List<Creature> providedSourceCreatures, List<Creature> providedDestinationCreatures) {

        List<Creature> sourceCreatures = table.getJoker().getStudents().stream().map(s -> s.getCreature()).collect(Collectors.toList());
        List<Creature> destCreatures = players.get(currentPlayerIndex).getEntrance().getStudents().stream().map(s -> s.getCreature()).collect(Collectors.toList());

        if (sourceCreatures.containsAll(providedSourceCreatures) && destCreatures.containsAll(providedDestinationCreatures)) {
            StudentContainer joker = table.getJoker();
            Entrance entrance = players.get(currentPlayerIndex).getEntrance();
            swapStudents(joker, entrance, providedSourceCreatures, providedDestinationCreatures);
            table.setJoker(joker);
            players.get(currentPlayerIndex).setEntrance(entrance);
            return true;
        }
        return false;
    }

    /**
     * Swaps the students from the entrance to the dining room of current player
     *
     * @param providedEntranceCreatures   is the list of creatures to move from the entrance
     * @param providedDiningRoomCreatures is the list of creatures to move from the dining room
     * @return true or false depending on the correct execution of method
     */
    @Override
    public boolean minstrelEffect(List<Creature> providedEntranceCreatures, List<Creature> providedDiningRoomCreatures) {

        List<Creature> entranceCreatures = players.get(currentPlayerIndex).getEntrance().getStudents().stream().map(s -> s.getCreature()).collect(Collectors.toList());
        List<Creature> destCreatures = players.get(currentPlayerIndex).getEntrance().getStudents().stream().map(s -> s.getCreature()).collect(Collectors.toList());

        if (entranceCreatures.containsAll(providedEntranceCreatures) && destCreatures.containsAll(destCreatures)) {
            Entrance currPlayerEntrance = players.get(currentPlayerIndex).getEntrance();
            DiningRoom currPlayerDiningRoom = players.get(currentPlayerIndex).getDiningRoom();
            swapStudents(currPlayerEntrance, currPlayerDiningRoom, providedEntranceCreatures, providedDiningRoomCreatures);
            players.get(currentPlayerIndex).setEntrance(currPlayerEntrance);
            players.get(currentPlayerIndex).setDiningRoom(currPlayerDiningRoom);
            return true;
        }
        return false;
    }

    /**
     * This method is used by Joker and Minstrel characters to swap the students
     *
     * @param source              is the first student container from which students have to be moved
     * @param destination         is the second student container from which students have to be moved
     * @param sourceCreature      is the list of creatures to move from the source container
     * @param destinationCreature is the list of creatures to move from the destination container
     */
    private void swapStudents(StudentContainer source, StudentContainer destination, List<Creature> sourceCreature, List<Creature> destinationCreature) {
        List<Student> studentsFromSource = new ArrayList<>();
        List<Student> studentsFromDestination = new ArrayList<>();
        //gets all the students from the source
        for (Creature c : sourceCreature) {
            studentsFromSource.add(source.removeStudent(c));
        }
        //gets all the students form the destination
        for (Creature c : destinationCreature) {
            studentsFromDestination.add(destination.removeStudent(c));
        }
        //swaps the students
        source.addStudents(studentsFromDestination);
        destination.addStudents(studentsFromSource);
    }

    /**
     * Populates the student containers for Monk and Princess
     */

    public void populateMoverCharacter() {
        List<Name> charNames = characters.stream().map(Character::getName).toList();
        StudentBucket bucket = table.getBucket();
        StudentContainer container;

        if (charNames.contains(Name.MONK)) {
            container = table.getMonk();
            for (int i = 0; i < container.getCapacity(); i++) {
                try {
                    container.addStudent(bucket.generateStudent());
                } catch (StudentsOutOfStockException e) {
                    e.printStackTrace();
                }
            }
            table.setMonk(container);
        }
        table.setBucket(bucket);

        if (charNames.contains(Name.PRINCESS)) {
            container = table.getPrincess();
            for (int i = 0; i < container.getCapacity(); i++) {
                try {
                    container.addStudent(bucket.generateStudent());
                } catch (StudentsOutOfStockException e) {
                    e.printStackTrace();
                }
            }
            table.setPrincess(container);
        }
        table.setBucket(bucket);

        if (charNames.contains(Name.JOKER)) {
            StudentContainer joker = new Joker(6);
            for (int i = 0; i < joker.getCapacity(); i++) {
                try {
                    joker.addStudent(bucket.generateStudent());
                } catch (StudentsOutOfStockException e) {
                    e.printStackTrace();
                }
            }
            table.setJoker(joker);
        }
        table.setBucket(bucket);
    }

    /**
     * sums the selected postman movements to the number of jumps
     * mnFuturePos uses the module function to establish the correct future position of MotherNature
     *
     * @param jumps number of steps that MotherNature has to do
     * @return true if mother nature changed her position
     */

    public boolean moveMotherNature(int jumps) {
        if (checkJumps(jumps)) {
            int j = jumps + postmanMovements;
            if (!(table.moveMotherNature(j))) {
                checkEndGame();
            }
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
        if (jumps < 0) {
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

    public int getPostmanMovements() {
        return postmanMovements;
    }

    public boolean checkIfLastRound() {
        return lastRound;
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
        return gameEnded;
    }

    @Override
    public int getDeactivators() {
        return table.getDeactivators();
    }

    @Override
    public boolean setDeactivators(int deactivators) {
        table.setDeactivators(deactivators);
        return true;
    }


    @Override
    public StudentBucket getBucket() {
        return table.getBucket();
    }

    @Override
    public void setBucket(StudentBucket bucket) {
        table.setBucket(bucket);
    }

    //region getters
    public Table getTable() {
        Table temp = new Table(numberOfPlayers, advancedRules);
        temp.setMotherNature(table.getMotherNature());
        temp.setClouds(table.getClouds());
        temp.setCoinReserve(table.getCoinReserve());
        temp.setIslands(table.getIslands());
        temp.setMonk(table.getMonk());
        temp.setJoker(table.getJoker());
        temp.setPrincess(table.getPrincess());
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
            if (p.getTowers() < ans.getTowers()) {
                ans = p;
            } else if (p.getTowers() == ans.getTowers() &&
                    p.getProfessors().size() > ans.getProfessors().size()) {
                ans = p;
            }
        }
        return ans;
    }

    //endregion

    public void setTable(Table table) {
        this.table = table;
    }

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

    //region setters
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

    //endregion

    public List<Character> getCharacters() {
        return characters;
    }

    public CharacterInformation getCharactersInformation(int indexOfCharacter) {

        Name charactersName = characters.get(indexOfCharacter).getName();

        List<Creature> moverContent;
        if (charactersName.equals(Name.MONK)) {
            moverContent = table.getMonk().getStudents().stream().map(s -> s.getCreature()).toList();
        } else if (charactersName.equals(Name.JOKER)) {
            moverContent = table.getJoker().getStudents().stream().map(s -> s.getCreature()).toList();
        } else if (charactersName.equals(Name.PRINCESS)) {
            moverContent = table.getPrincess().getStudents().stream().map(s -> s.getCreature()).toList();
        } else {
            moverContent = new ArrayList<>();
        }

        return new CharacterInformation(
                charactersName, characters.get(indexOfCharacter).hasCoin(), table.getDeactivators(), indexOfCharacter, moverContent);
    }


    public int getPlayedCharacter() {
        return playedCharacter;
    }

    //endregion

    //endregion

    //region PRIVATE METHODS
    //region Constructor
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
        List<Character> chars = new ArrayList<>();
        List<Name> names = new ArrayList<>(Arrays.asList(Name.values()));
        for (int i = 0; i < NUMBER_OF_CHARACTERS; i++) {
            chars.add(ccc.createCharacter(names.remove(new Random().nextInt(names.size())), this));
        }
        return chars;
    }

    //region createListOfPlayers
    private Entrance createEntrance(int numberOfPlayers) {
        List<Student> students = new ArrayList<>();
        StudentBucket bucket = table.getBucket();

        if (numberOfPlayers == 2) {
            return populateEntrance(students, bucket, TWO_PLAYERS_CAPACITY);
        }
        //in case of 3 players
        return populateEntrance(students, bucket, THREE_PLAYERS_CAPACITY);
    }

    private Entrance populateEntrance(List<Student> students, StudentBucket bucket, int playersCapacity) {
        Entrance entrance = new Entrance(playersCapacity);
        for (int i = 0; i < playersCapacity; i++) {
            try {
                students.add(bucket.generateStudent());
            } catch (StudentsOutOfStockException ignore) {
            }
        }
        entrance.addStudents(students);
        table.setBucket(bucket);
        return entrance;
    }


    private Player createTwoPlayer(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 0, myWizard, TWO_PLAYERS_NUMBER_OF_TOWERS, myEntrance);
    }

    private Player createThreePlayer(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 0, myWizard, THREE_PLAYERS_NUMBER_OF_TOWERS, myEntrance);
    }

    private Player createTwoPlayerAdvanced(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, ADVANCED_RULES_STARTING_COINS, myWizard, TWO_PLAYERS_NUMBER_OF_TOWERS, myEntrance);
    }

    private Player createThreePlayerAdvanced(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, ADVANCED_RULES_STARTING_COINS, myWizard, THREE_PLAYERS_NUMBER_OF_TOWERS, myEntrance);
    }
    //endregion

    //endregion

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

            //find who has more students of creature c
            Player hasMoreStudents = players.get(0);
            for (Player p : players) {

                if (isFarmer && players.get(currentPlayerIndex).getUsername().equals(p.getUsername())) {
                    if (p.getDiningRoom().getNumberOfStudentsByCreature(c) >=
                            hasMoreStudents.getDiningRoom().getNumberOfStudentsByCreature(c)) {
                        hasMoreStudents = p;
                    }
                } else if (p.getDiningRoom().getNumberOfStudentsByCreature(c) >
                        hasMoreStudents.getDiningRoom().getNumberOfStudentsByCreature(c)) {
                    hasMoreStudents = p;
                }
            }
            if (professorOwner.isPresent()) {
                if (!(professorOwner.get().getUsername().equals(hasMoreStudents.getUsername()))) {

                    hasMoreStudents.addProfessor(professorOwner.get().removeProfessor(c));
                }

            } else {
                if (hasMoreStudents.getDiningRoom().getNumberOfStudentsByCreature(c) > 0) {
                    hasMoreStudents.addProfessor(new Professor(c));
                }
            }
        }
    }

    private boolean hasProfessor(int indexOfPlayer, Creature c) {
        if (!players.get(indexOfPlayer).getProfessors().isEmpty()) {
            return players.get(indexOfPlayer).getProfessors().stream().filter(p -> p.getCreature().equals(c)).findFirst().isPresent();
        }
        return false;
    }

    //endregion


    //work in progress

    public void conquerIsland(Player hasMoreInfluence) {

        Island currentIsland = table.getCurrentIsland();

        if (!hasMoreInfluence.getMyColor().equals(currentIsland.getColorOfTowers())) {
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
                checkEndGame();
            }
        }
    }


    public boolean effect(CharactersParametersPayload answer) {
        boolean temp = characters.get(playedCharacter).effect(answer);
        return temp;
    }

    public void showModel(ShowModelPayload modelUpdate) {
        ShowModelEvent modelEvent = new ShowModelEvent(this, modelUpdate);
        for (MessageHandler listener : listeners.getListeners(MessageHandler.class)) {
            listener.eventPerformed(modelEvent);
        }
    }

    public ShowModelPayload showModelPayloadCreator() {
        ShowModelPayload showModelPayload = new ShowModelPayload(getPlayers(), getTable());

        if (isAdvancedRules()) {
            Map<Name, Integer> characters = new HashMap<>();
            for (Character c : getCharacters()) {
                characters.put(c.getName(), c.getCost());
            }
            showModelPayload.setCharacters(characters);
            List<Creature> creatureList;
            if (getTable().getJoker().getStudents().size() > 0) {
                creatureList = new ArrayList<>();
                for (Student s : getTable().getJoker().getStudents()) {
                    creatureList.add(s.getCreature());
                }
                showModelPayload.setJokerCreatures(creatureList);
            }
            if (getTable().getPrincess().getStudents().size() > 0) {
                creatureList = new ArrayList<>();
                for (Student s : getTable().getPrincess().getStudents()) {
                    creatureList.add(s.getCreature());
                }
                showModelPayload.setPrincessCreatures(creatureList);
            }
            if (getTable().getMonk().getStudents().size() > 0) {
                creatureList = new ArrayList<>();
                for (Student s : getTable().getMonk().getStudents()) {
                    creatureList.add(s.getCreature());
                }
                showModelPayload.setMonkCreatures(creatureList);
            }
        }

        return showModelPayload;
    }


}
