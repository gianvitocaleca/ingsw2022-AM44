package it.polimi.ingsw;

import it.polimi.ingsw.controller.events.*;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Assistant;
import it.polimi.ingsw.model.player.Professor;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.network.server.enums.ServerPhases;
import it.polimi.ingsw.model.exceptions.PausedException;
import it.polimi.ingsw.network.server.states.NetworkState;
import it.polimi.ingsw.network.server.SocketID;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.GameStatus;
import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.ConcreteCharacterCreator;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.Entrance;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.network.server.handlers.MessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    GameModel gm;
    Controller controller;
    MessageHandler messageHandler;

    Boolean planned = false;


    /**
     * This create a new GameModel instance to use in every test
     */
    @BeforeEach
    public void createGame() throws PausedException {
        String player1 = "Paolo";
        SocketID socketID1 = new SocketID(1, new Socket());
        String player2 = "Gianvito";
        SocketID socketID2 = new SocketID(2, new Socket());
        String player3 = "Sabrina";
        SocketID socketID3 = new SocketID(3, new Socket());
        NetworkState state = new NetworkState(ServerPhases.READY);
        state.addSocket(socketID1);
        state.addSocket(socketID2);
        state.addSocket(socketID3);
        state.setUsername(1, player1);
        state.setUsername(2, player2);
        state.setUsername(3, player3);
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList(player1, player2, player3)),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.GANDALF, Wizard.SABRINA, Wizard.BALJEET)));

        messageHandler = new MessageHandler(state);
        controller = new Controller(gm, messageHandler, new GameStatus(GamePhases.PLANNING, false), state);
        controller.startController();
    }

    /**
     * This test verifies that the controller can play an assistant card chosen by the current player
     * in the correct way. In particular, it verifies that the size of the assistant deck is decreased
     * and that the current player index is changed.
     */
    @Test
    public void playAssistantTest() {

        int pastPlayerIndex = gm.getCurrentPlayerIndex();

        PlanningEvent evt = new PlanningEvent(messageHandler, 0);
        messageHandler.playAssistantReceiver(evt);

        int currPlayerIndex = gm.getCurrentPlayerIndex();

        assertTrue(pastPlayerIndex != currPlayerIndex);

        assertEquals(9, gm.getPlayers().get(pastPlayerIndex).getAssistantDeck().size());
        assertEquals(currPlayerIndex, (pastPlayerIndex + 1) % (gm.getNumberOfPlayers()));


    }

    /**
     * This test verifies that the controller sends an error message to the client
     * because he chose an assistant card already played by another one.
     * It verifies also that the current player is the same as before.
     */
    @Test
    public void playAssistantAlreadyPlayedTest() {

        PlanningEvent evt = new PlanningEvent(messageHandler, 0);
        messageHandler.playAssistantReceiver(evt);

        String currentPlayer = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getUsername();
        messageHandler.playAssistantReceiver(evt);

        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getUsername(), currentPlayer);


    }

    /**
     * This test verifies that the controller sends an error message and doesn't change the current player
     * when he plays an assistant that doesn't exist.
     */
    @Test
    public void playWrongAssistantTest() {

        int pastPlayerIndex = gm.getCurrentPlayerIndex();

        PlanningEvent evt = new PlanningEvent(messageHandler, 11);
        messageHandler.playAssistantReceiver(evt);

        int currPlayerIndex = gm.getCurrentPlayerIndex();

        assertTrue(pastPlayerIndex == currPlayerIndex);
        assertEquals(10, gm.getPlayers().get(pastPlayerIndex).getAssistantDeck().size());

    }

    /**
     * This tests that when the last assistant for a player is played, the current round
     * is set as the last round of the game
     */
    @Test
    public void playLastAssistantTest(){
        List<Player> players = gm.getPlayers();
        Player curr = gm.getPlayers().get(gm.getCurrentPlayerIndex());
        List<Assistant> assistants = curr.getAssistantDeck();
        for(int i = 0; i<curr.getAssistantDeck().size()-1;i++){
            assistants.remove(0);
        }
        curr.setAssistantDeck(assistants);
        players.set(gm.getCurrentPlayerIndex(),curr);
        gm.setPlayers(players);

        PlanningEvent evt = new PlanningEvent(messageHandler, 0);
        messageHandler.playAssistantReceiver(evt);

        assertTrue(controller.checkIfLastRound());

        assertEquals(gm.findWinner().getUsername(),curr.getUsername());

    }

    /**
     * This test verifies the correct behavior of the controller when a complete planning phase happens.
     * In particular, he verifies that every player plays an assistant card, game's phase is changed to
     * the following one and that the current player is again the first one.
     */
    @Test
    public void completePlanningPhaseTest() {


        PlanningEvent evt = new PlanningEvent(messageHandler, 0);
        messageHandler.playAssistantReceiver(evt);
        evt = new PlanningEvent(messageHandler, 1);
        messageHandler.playAssistantReceiver(evt);
        evt = new PlanningEvent(messageHandler, 2);
        messageHandler.playAssistantReceiver(evt);


        assertEquals(controller.getCurrentPhase(), GamePhases.ACTION_STUDENTSMOVEMENT);
        assertEquals(gm.getCurrentPlayerIndex(), 0);


    }

    /**
     * This test verifies that the controller can play a character card in the correct way.
     */
    @Test
    public void playCharacterTest() {
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.MAGICPOSTMAN, gm));

        completePlanningPhaseTest();

        PlayCharacterEvent evt = new PlayCharacterEvent(messageHandler, 0);
        messageHandler.playCharacterReceiver(evt);

        assertTrue(controller.isWaitingForParameters());

        CharactersParametersPayload parameters = new CharactersParametersPayload(new ArrayList<>(), 0, 2, new ArrayList<>());
        CharacterParametersEvent ev2 = new CharacterParametersEvent(messageHandler, parameters);
        messageHandler.characterParametersReceiver(ev2);

        assertEquals(gm.getPostmanMovements(), 2);

    }

    /**
     * This tests that when a wrong parameter is provided, the action will not be done
     */
    @Test
    public void playCharacterWithWrongParametersTest(){
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.MAGICPOSTMAN, gm));

        completePlanningPhaseTest();

        PlayCharacterEvent evt = new PlayCharacterEvent(messageHandler, 0);
        messageHandler.playCharacterReceiver(evt);

        assertTrue(controller.isWaitingForParameters());

        CharactersParametersPayload parameters = new CharactersParametersPayload(new ArrayList<>(), 0, 3, new ArrayList<>());
        CharacterParametersEvent ev2 = new CharacterParametersEvent(messageHandler, parameters);
        messageHandler.characterParametersReceiver(ev2);

        assertTrue(controller.isWaitingForParameters());
    }

    @Test
    public void PlayCharacterAndWinTheGame() throws GameEndedException {
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.HERALD, gm));

        completePlanningPhaseTest();

        List<Player> players = gm.getPlayers();
        Player curr = players.get(gm.getCurrentPlayerIndex());
        while(curr.getMyCoins()<Name.HERALD.getCost()+1){
            curr.addCoin();
        }
        curr.removeTowers(curr.getTowers()-1);
        List<Professor> prof = curr.getProfessors();
        prof.add(new Professor(Creature.RED_DRAGONS));
        curr.setProfessors(prof);
        players.set(gm.getCurrentPlayerIndex(),curr);
        gm.setPlayers(players);

        Table table = gm.getTable();
        List<Island> islands = gm.getTable().getIslands();
        List<Student> newStudents = new ArrayList<>();
        newStudents.add(new Student(Creature.RED_DRAGONS));
        newStudents.add(new Student(Creature.RED_DRAGONS));
        islands.get(0).addStudents(newStudents);
        table.setIslands(islands);
        gm.setTable(table);

        PlayCharacterEvent evt = new PlayCharacterEvent(messageHandler, 0);
        messageHandler.playCharacterReceiver(evt);

        CharactersParametersPayload parameters = new CharactersParametersPayload(new ArrayList<>(), 0, 0, new ArrayList<>());
        CharacterParametersEvent ev2 = new CharacterParametersEvent(messageHandler, parameters);
        messageHandler.characterParametersReceiver(ev2);

    }

    /**
     *  This tests the correct behavior of the effect method when a character that doesn't need
     *  parameters is played
     */
    @Test
    public void playCharacterThatDoesntNeedParametersTest(){
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.FARMER, gm));

        completePlanningPhaseTest();

        List<Player> players = gm.getPlayers();
        Player curr = players.get(gm.getCurrentPlayerIndex());
        while(curr.getMyCoins()<Name.FARMER.getCost()+1){
            curr.addCoin();
        }
        players.set(gm.getCurrentPlayerIndex(),curr);
        gm.setPlayers(players);
        PlayCharacterEvent evt = new PlayCharacterEvent(messageHandler, 0);
        messageHandler.playCharacterReceiver(evt);

        assertFalse(controller.isWaitingForParameters());

    }

    /**
     * This test verifies that the controller doesn't allow to play more than one character per turn
     */
    @Test
    public void playMoreThanOneCharacterTest(){
        playCharacterTest();

        PlayCharacterEvent evt = new PlayCharacterEvent(messageHandler, 0);
        messageHandler.playCharacterReceiver(evt);
        assertFalse(controller.isWaitingForParameters());
    }

    /**
     * This test verifies that if a player tries to send a characterParametersPayload
     * without having played the character, the controller will not allow the action
     */
    @Test
    public void sendCharacterParametersWithoutPlayingCharacter(){
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.MAGICPOSTMAN, gm));

        completePlanningPhaseTest();

        CharactersParametersPayload parameters = new CharactersParametersPayload(new ArrayList<>(), 0, 2, new ArrayList<>());
        CharacterParametersEvent ev2 = new CharacterParametersEvent(messageHandler, parameters);
        messageHandler.characterParametersReceiver(ev2);

        assertEquals(gm.getPostmanMovements(), 0);
    }

    /**
     * This test verifies that the controller sends an error message when a player wants
     * to play a wrong character card. In particular, waitingForParameters is still true.
     */
    @Test
    public void playWrongCharacterTest() {

        completePlanningPhaseTest();

        PlayCharacterEvent evt = new PlayCharacterEvent(messageHandler, 3);
        messageHandler.playCharacterReceiver(evt);

        assertFalse(controller.isWaitingForParameters());

    }

    /**
     * This test verifies that the controller sends an error message when the player wants
     * to play a character card without enough money and that waitingForParameters is still true.
     */
    @Test
    public void playCharacterWithPoorPlayerTest() {
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.MAGICPOSTMAN, gm));

        completePlanningPhaseTest();

        List<Player> temp = gm.getPlayers();
        temp.get(gm.getCurrentPlayerIndex()).removeCoin(1);
        gm.setPlayers(temp);

        PlayCharacterEvent evt = new PlayCharacterEvent(messageHandler, 0);
        messageHandler.playCharacterReceiver(evt);

        assertFalse(controller.isWaitingForParameters());

    }

    /**
     * This test verifies that the move students action of a player works.
     * In particular, the students are put in the correct place and game's phase is changed.
     */
    @Test
    public void moveStudentsTest() {
        planningIfNo();
        Entrance entrance = new Entrance(9);
        List<Student> studentList = new ArrayList<>();
        studentList.add(new Student(Creature.RED_DRAGONS));
        studentList.add(new Student(Creature.RED_DRAGONS));
        studentList.add(new Student(Creature.YELLOW_GNOMES));
        studentList.add(new Student(Creature.YELLOW_GNOMES));
        studentList.add(new Student(Creature.BLUE_UNICORNS));
        studentList.add(new Student(Creature.BLUE_UNICORNS));
        studentList.add(new Student(Creature.GREEN_FROGS));
        studentList.add(new Student(Creature.GREEN_FROGS));
        studentList.add(new Student(Creature.PINK_FAIRIES));
        entrance.addStudents(studentList);

        List<Player> players = gm.getPlayers();
        players.get(gm.getCurrentPlayerIndex()).setEntrance(entrance);

        gm.setPlayers(players);

        List<Creature> providedCreature = new ArrayList<>();
        providedCreature.add(Creature.YELLOW_GNOMES);

        MoveStudentsEvent evt = new MoveStudentsEvent(messageHandler,
                false, 12, providedCreature);

        messageHandler.moveStudentsReceiver(evt);
        if (gm.getCurrentPlayerIndex() == 0) {
            assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getProfessors().get(0).getCreature(), Creature.YELLOW_GNOMES);
            assertEquals(controller.getCurrentStatus().getNumberOfStudentsMoved(), 1);
            assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getNumberOfStudentsByCreature(Creature.YELLOW_GNOMES), 1);
        }


        int oldRed = gm.getTable().getIslands().get(0).getNumberOfStudentsByCreature(Creature.RED_DRAGONS);

        providedCreature.remove(0);
        providedCreature.add(Creature.RED_DRAGONS);
        evt = new MoveStudentsEvent(messageHandler,
                true, 0, providedCreature);

        messageHandler.moveStudentsReceiver(evt);

        assertEquals(gm.getTable().getIslands().get(0).getNumberOfStudentsByCreature(Creature.RED_DRAGONS), oldRed + 1);
        assertEquals(controller.getCurrentStatus().getNumberOfStudentsMoved(), 2);

        providedCreature.remove(0);
        providedCreature.add(Creature.RED_DRAGONS);
        evt = new MoveStudentsEvent(messageHandler,
                true, 0, providedCreature);

        messageHandler.moveStudentsReceiver(evt);

        providedCreature.remove(0);
        providedCreature.add(Creature.YELLOW_GNOMES);
        evt = new MoveStudentsEvent(messageHandler,
                true, 0, providedCreature);

        messageHandler.moveStudentsReceiver(evt);

        assertEquals(controller.getCurrentStatus().getNumberOfStudentsMoved(), 0);
        assertEquals(controller.getCurrentPhase(), GamePhases.ACTION_MOVEMOTHERNATURE);

    }

    /**
     * This test verifies that the controller sends an error message when a player wants to move
     * creatures that aren't in the entrance. The controller doesn't increase the number of students moved
     * and the number of students in the entrance is the same as before.
     */
    @Test
    public void moveWrongStudentsTest() {
        Entrance entrance = new Entrance(9);
        List<Student> studentList = new ArrayList<>();
        studentList.add(new Student(Creature.RED_DRAGONS));
        studentList.add(new Student(Creature.RED_DRAGONS));
        studentList.add(new Student(Creature.YELLOW_GNOMES));
        studentList.add(new Student(Creature.YELLOW_GNOMES));
        studentList.add(new Student(Creature.BLUE_UNICORNS));
        studentList.add(new Student(Creature.BLUE_UNICORNS));
        studentList.add(new Student(Creature.GREEN_FROGS));
        studentList.add(new Student(Creature.GREEN_FROGS));
        studentList.add(new Student(Creature.GREEN_FROGS));
        entrance.addStudents(studentList);

        List<Player> players = gm.getPlayers();
        players.get(gm.getCurrentPlayerIndex()).setEntrance(entrance);

        gm.setPlayers(players);

        List<Creature> providedCreature = new ArrayList<>();
        providedCreature.add(Creature.PINK_FAIRIES);

        MoveStudentsEvent evt = new MoveStudentsEvent(messageHandler,
                false, 12, providedCreature);

        messageHandler.moveStudentsReceiver(evt);

        assertEquals(controller.getCurrentStatus().getNumberOfStudentsMoved(), 0);
        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().size(), 9);

    }

    private void planningIfNo() {
        if (controller.getCurrentPhase().equals(GamePhases.PLANNING)) {
            completePlanningPhaseTest();
        }
    }

    /**
     * This test verifies that mother nature is moved in the correct way using the number provided
     * by the user.
     */
    @Test
    public void moveMotherNatureTest() {
        planningIfNo();
        moveStudentsTest();
        int motherNaturePosition = gm.getTable().getMnPosition();
        IntegerEvent evt = new IntegerEvent(messageHandler, 1);
        messageHandler.integerEventReceiver(evt);


        assertEquals(gm.getTable().getMnPosition(), (motherNaturePosition + 1) % gm.getTable().getIslands().size());

    }

    /**
     * This test verifies that the controller doesn't move mother nature when the player chooses a  number
     * too high.
     */
    @Test
    public void moveWrongMotherNatureTest() {
        completePlanningPhaseTest();
        moveStudentsTest();
        int motherNaturePosition = gm.getTable().getMnPosition();
        IntegerEvent evt = new IntegerEvent(messageHandler, 12);
        messageHandler.integerEventReceiver(evt);

        assertEquals(gm.getTable().getMnPosition(), motherNaturePosition);

    }

    /**
     * This test verifies that the controller doesn't move mother nature when the player chooses a negative number
     */
    @Test
    public void moveNegativeMotherNatureTest() {
        completePlanningPhaseTest();
        moveStudentsTest();
        int motherNaturePosition = gm.getTable().getMnPosition();
        IntegerEvent evt = new IntegerEvent(messageHandler, -1);
        messageHandler.integerEventReceiver(evt);

        assertEquals(gm.getTable().getMnPosition(), motherNaturePosition);

    }


    /**
     * This tests that the selectCloud works correctly moving the students from the selected cloud
     * to the currentPlayer's entrance
     */

    @Test
    public void selectCloudTest() {

        moveMotherNatureTest();
        int currPlayerIndex = gm.getCurrentPlayerIndex();
        int cloudSelected = 0;
        IntegerEvent evt = new IntegerEvent(messageHandler, cloudSelected);
        messageHandler.integerEventReceiver(evt);

        assertEquals(gm.getTable().getClouds().get(0).getStudents().size(), 0);
        assertEquals(gm.getPlayers().get(currPlayerIndex).getEntrance().getStudents().size(), 9);
        assertEquals(gm.getCurrentPlayerIndex(), currPlayerIndex + 1);

    }

    /**
     * This tests that the selectCloud doesn't work when a non-existent cloud is selected by the user
     */

    @Test
    public void selectWrongCloudTest() {

        moveMotherNatureTest();
        int currPlayerIndex = gm.getCurrentPlayerIndex();
        int cloudSelected = 3;
        IntegerEvent evt = new IntegerEvent(messageHandler, cloudSelected);
        messageHandler.integerEventReceiver(evt);

        assertEquals(gm.getPlayers().get(currPlayerIndex).getEntrance().getStudents().size(), 5);
        for (Cloud c : gm.getTable().getClouds()) {
            assertEquals(c.getStudents().size(), 4);
        }

    }


    /**
     * This tests that the selectCloud doesn't work when an already selected cloud is selected by the user
     */

    @Test
    public void selectAlreadySelectedCloudTest() {

        selectCloudTest();
        GameStatus gameStatus = controller.getCurrentStatus();
        gameStatus.setPhase(GamePhases.ACTION_CLOUDCHOICE);
        controller.setCurrentStatus(gameStatus);

        int currPlayerIndex = gm.getCurrentPlayerIndex();
        int cloudSelected = 0;
        IntegerEvent evt = new IntegerEvent(messageHandler, cloudSelected);
        messageHandler.integerEventReceiver(evt);

        assertEquals(gm.getPlayers().get(currPlayerIndex).getEntrance().getStudents().size(), 9);
        assertEquals(gm.getTable().getClouds().get(0).getStudents().size(), 0);

    }


    /**
     * This tests that when the last cloud is selected, after the moving of students,
     * the currentPhase is set to PLANNING
     */

    @Test
    public void selectLastCloudTest() {

        //selects the cloud for the first player
        selectCloudTest();
        //moves the students and MotherNature for the second player
        GameStatus gameStatus = controller.getCurrentStatus();
        gameStatus.setPhase(GamePhases.ACTION_CLOUDCHOICE);
        controller.setCurrentStatus(gameStatus);


        int cloudSelected = 1;
        IntegerEvent evt = new IntegerEvent(messageHandler, cloudSelected);
        messageHandler.integerEventReceiver(evt);

        //moves the students and MotheNature for the third player
        gameStatus = controller.getCurrentStatus();
        gameStatus.setPhase(GamePhases.ACTION_CLOUDCHOICE);
        controller.setCurrentStatus(gameStatus);

        cloudSelected = 2;
        evt = new IntegerEvent(messageHandler, cloudSelected);
        messageHandler.integerEventReceiver(evt);

        assertEquals(controller.getCurrentPhase(), GamePhases.PLANNING);
        assertEquals(gm.getCurrentPlayerIndex(), 0);

    }


}
