package it.polimi.ingsw.model;

import it.polimi.ingsw.messages.CharactersParameters;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.evaluators.InfluenceEvaluator;
import it.polimi.ingsw.model.evaluators.StandardEvaluator;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.gameboard.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.studentcontainers.*;
import it.polimi.ingsw.model.students.*;

import java.util.*;
import java.util.stream.Collectors;

public class GameModel implements Playable {

    public static final int NUMBER_OF_CHARACTERS = 3;
    public static final int THREE_PLAYERS_CAPACITY = 9;
    public static final int TWO_PLAYERS_CAPACITY = 7;
    public static final int TWO_PLAYERS_NUMBEROFTOWERS = 8;
    public static final int THREE_PLAYERS_NUMBEROFTOWERS = 6;
    public static final int ADVANCED_RULES_STARTINGCOINS = 1;
    private Table table;
    private final int numberOfPlayers;
    private List<Player> players;
    private int currentPlayerIndex;
    private List<Character> characters;
    private int playedCharacter;
    private InfluenceEvaluator evaluator;
    private int postmanMovements;
    private boolean isFarmer;
    private boolean advancedRules;
    private boolean lastRound = false;

    //Constructor
    public GameModel(boolean advancedRules, List<String> usernames, int numberOfPlayers, List<Color> colors, List<Wizard> wizards) {
        this.advancedRules = advancedRules;
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
        populateMoverCharacter();
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
            moveStudents(princess, currPlayerDiningRoom, sourceCreatures);

            StudentBucket bucket = table.getBucket();
            try{
                princess.addStudent(bucket.generateStudent());
            }catch (StudentsOutOfStockException e){
                lastRound=true;
            }
            table.setBucket(bucket);

            table.setPrincess(princess);
            players.get(currentPlayerIndex).setDiningRoom(currPlayerDiningRoom);
            return true;
        }
        return false;
    }

    @Override
    public boolean monkEffect(List<Creature> providedSourceCreatures, int islandIndex) {
        List<Creature> sourceCreatures = table.getMonk().getStudents().stream().map(s -> s.getCreature()).toList();

        if (sourceCreatures.containsAll(providedSourceCreatures)) {
            StudentContainer monk = table.getMonk();
            Island destination = table.getIslands().get(islandIndex);
            moveStudents(monk, destination, sourceCreatures);

            StudentBucket bucket = table.getBucket();
            try{
                monk.addStudent(bucket.generateStudent());
            }catch (StudentsOutOfStockException e){
                lastRound=true;
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

            for(Assistant a: playedAssistants){
                if (a.getName().equals(players.get(currentPlayerIndex).getAssistantDeck().get(indexOfAssistant).getName())){
                    throw new AssistantAlreadyPlayedException();
                }
            }
        }

        players.get(currentPlayerIndex).setAssistantCard(indexOfAssistant);
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
     * @param providedEntranceCreatures
     * @param providedDiningRoomCreatures
     * @return
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
     * @param source
     * @param destination
     * @param sourceCreature
     * @param destinationCreature
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
            StudentContainer prova = new Joker(6);
            for (int i = 0; i < prova.getCapacity(); i++) {
                try {
                    prova.addStudent(bucket.generateStudent());
                } catch (StudentsOutOfStockException e) {
                    e.printStackTrace();
                }
            }
            table.setJoker(prova);
        }
        table.setBucket(bucket);
    }

    /**
     * sums the selected postman movements to the number of jumps
     * mnFuturePos uses the module function to establish the correct future position of MotherNature
     *
     * @param jumps number of steps that MotherNature has to do
     */

    public void moveMotherNature(int jumps) {
        int j = jumps + postmanMovements;
        if (!(table.moveMotherNature(j))) {
            checkEndGame();
        }
    }


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

        //NOTIFY *********************

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

        Name charactersname = characters.get(indexOfCharacter).getName();

        List<Creature> moverContent;
        if (charactersname.equals(Name.MONK)) {
            moverContent = table.getMonk().getStudents().stream().map(s -> s.getCreature()).toList();
        } else if (charactersname.equals(Name.JOKER)) {
            moverContent = table.getJoker().getStudents().stream().map(s -> s.getCreature()).toList();
        } else if (charactersname.equals(Name.PRINCESS)) {
            moverContent = table.getPrincess().getStudents().stream().map(s -> s.getCreature()).toList();
        } else {
            moverContent = new ArrayList<>();
        }

        CharacterInformation info = new CharacterInformation(
                charactersname, characters.get(indexOfCharacter).hasCoin(), table.getDeactivators(), indexOfCharacter, moverContent);

        return info;
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

    /**
     * Creates three random characters for the game
     *
     * @return is the list of characters
     */
    private List<Character> createListOfCharacters() {
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        List<Character> chars = new ArrayList<Character>();
        List<Name> names = new ArrayList<Name>(Arrays.asList(Name.values()));
        for (int i = 0; i < NUMBER_OF_CHARACTERS; i++) {
            chars.add(ccc.createCharacter(names.remove(new Random().nextInt(names.size())), this));
        }
        return chars;
    }

    //region createListOfPlayers
    private Entrance createEntrance(int numberOfPlayers) {
        if (numberOfPlayers == 2) {
            return new Entrance(TWO_PLAYERS_CAPACITY);
        }
        //in case of 3 players
        return new Entrance(THREE_PLAYERS_CAPACITY);
    }

    private Player createTwoPlayer(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 0, myWizard, TWO_PLAYERS_NUMBEROFTOWERS, myEntrance);
    }

    private Player createThreePlayer(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, 0, myWizard, THREE_PLAYERS_NUMBEROFTOWERS, myEntrance);
    }

    private Player createTwoPlayerAdvanced(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, ADVANCED_RULES_STARTINGCOINS, myWizard, TWO_PLAYERS_NUMBEROFTOWERS, myEntrance);
    }

    private Player createThreePlayerAdvanced(Entrance myEntrance, String myUsername, Color myColor, Wizard myWizard) {
        return new Player(myUsername, myColor, ADVANCED_RULES_STARTINGCOINS, myWizard, THREE_PLAYERS_NUMBEROFTOWERS, myEntrance);
    }
    //endregion

    //endregion

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


    public boolean effect(CharactersParameters answer) {
        if (!(characters.get(playedCharacter).effect(answer))) {
            return false;
        }
        return true;
    }

}
