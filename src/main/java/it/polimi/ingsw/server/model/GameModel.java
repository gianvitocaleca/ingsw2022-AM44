package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.server.model.exceptions.GameEndedException;
import it.polimi.ingsw.server.CharacterInformation;
import it.polimi.ingsw.server.controller.events.ShowModelEvent;
import it.polimi.ingsw.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.server.networkMessages.payloads.ShowModelPayload;
import it.polimi.ingsw.server.model.characters.Character;
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
import it.polimi.ingsw.server.handlers.MessageHandler;

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
    private final boolean advancedRules;
    private boolean lastRound = false;
    private final EventListenerList listeners;

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

    @Override
    public void setInfluenceEvaluator(InfluenceEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public void setFarmer() {
        isFarmer = true;
    }

    public void resetFarmer() {
        isFarmer = false;
    }

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
        isFarmer = false;
        findNextPlayer();
        postmanMovements = 0;
        ShowModelPayload payload = showModelPayloadCreator();
        payload.setUpdateClouds();
        payload.setUpdatePlayersEntrance();
        showModel(payload);
        return true;
    }

    public void findNextPlayer() {
        if (currentPlayerIndex < numberOfPlayers - 1) {
            currentPlayerIndex++;
        } else {
            currentPlayerIndex = 0;
        }
    }

    public boolean playAssistant(int indexOfAssistant) throws AssistantAlreadyPlayedException, PlanningPhaseEndedException, GameEndedException {
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

        findNextPlayer();
        if (currentPlayerIndex == 0) {
            for (Player p : players) {
                if (p.getAssistantDeck().size() == 0) {
                    throw new GameEndedException();
                }
            }
            throw new PlanningPhaseEndedException();
        }
        return true;
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
        currentPlayerIndex = 0;
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
            if (p.getTowers() < ans.getTowers()) {
                ans = p;
            } else if (p.getTowers() == ans.getTowers() &&
                    p.getProfessors().size() > ans.getProfessors().size()) {
                ans = p;
            }
        }
        return ans;
    }


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
        for (int i = 0; i < NUMBER_OF_CHARACTERS; i++) {
            characterList.add(ccc.createCharacter(names.remove(new Random().nextInt(names.size())), this));
        }
        return characterList;
    }

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

    private boolean hasProfessor(int indexOfPlayer, Creature c) {
        if (!players.get(indexOfPlayer).getProfessors().isEmpty()) {
            return players.get(indexOfPlayer).getProfessors().stream().filter(p -> p.getCreature().equals(c)).findFirst().isPresent();
        }
        return false;
    }

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

    public void showModel(ShowModelPayload modelUpdate) {
        ShowModelEvent modelEvent = new ShowModelEvent(this, modelUpdate);
        for (MessageHandler listener : listeners.getListeners(MessageHandler.class)) {
            listener.eventPerformed(modelEvent);
        }
    }

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

    public void addListener(MessageHandler listener) {
        listeners.add(MessageHandler.class, listener);
    }

    public boolean isAdvancedRules() {
        return advancedRules;
    }


}
