package it.polimi.ingsw;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.GameStatus;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.networkMessages.CharactersParametersPayload;
import it.polimi.ingsw.server.controller.events.*;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.characters.ConcreteCharacterCreator;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.studentcontainers.Cloud;
import it.polimi.ingsw.server.model.studentcontainers.Entrance;
import it.polimi.ingsw.server.model.students.Student;
import it.polimi.ingsw.server.viewProxy.MessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    GameModel gm;
    Controller controller;
    MessageHandler view;

    Boolean planned = false;


    /**
     * This create a new GameModel instance to use in every test
     */
    @BeforeEach
    public void createGame() {
        gm = new GameModel(true,
                new ArrayList<>(Arrays.asList("Paolo", "Gianvito", "Sabrina")),
                3,
                new ArrayList<>(Arrays.asList(Color.values())),
                new ArrayList<>(Arrays.asList(Wizard.GANDALF, Wizard.SABRINA, Wizard.BALJEET)));

        view = new MessageHandler();
        controller = new Controller(gm,view,new GameStatus(GamePhases.PLANNING,false));
    }

    /**
     * This test verifies that the controller can play an assistant card chosen by the current player
     * in the correct way. In particular, it verifies that the size of the assistant deck is decreased
     * and that the current player index is changed.
     */
    @Test
    public void playAssistantTest(){

        int pastPlayerIndex = gm.getCurrentPlayerIndex();

        PlanningEvent evt = new PlanningEvent(view,0);
        view.playAssistantReceiver(evt);

        int currPlayerIndex = gm.getCurrentPlayerIndex();

        assertTrue(pastPlayerIndex!=currPlayerIndex);

        assertEquals(9, gm.getPlayers().get(pastPlayerIndex).getAssistantDeck().size());
        assertEquals(currPlayerIndex, (pastPlayerIndex + 1) % (gm.getNumberOfPlayers()));


    }

    /**
     * This test verifies that the controller sends an error message to the client
     * because he chose an assistant card already played by another one.
     * It verifies also that the current player is the same as before.
     */
    @Test
    public void playAssistantAlreadyPlayedTest(){

        PlanningEvent evt = new PlanningEvent(view,0);
        view.playAssistantReceiver(evt);

        String currentPlayer = gm.getPlayers().get(gm.getCurrentPlayerIndex()).getUsername();
        view.playAssistantReceiver(evt);

        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getUsername(),currentPlayer);


    }

    /**
     * This test verifies that the controller sends an error message and doesn't change the current player
     * when he plays an assistant that doesn't exist.
     */
    @Test
    public void playWrongAssistantTest(){

        int pastPlayerIndex = gm.getCurrentPlayerIndex();

        PlanningEvent evt = new PlanningEvent(view,11);
        view.playAssistantReceiver(evt);

        int currPlayerIndex = gm.getCurrentPlayerIndex();

        assertTrue(pastPlayerIndex==currPlayerIndex);
        assertEquals(10, gm.getPlayers().get(pastPlayerIndex).getAssistantDeck().size());

    }

    /**
     * This test verifies the correct behavior of the controller when a complete planning phase happens.
     * In particular, he verifies that every player plays an assistant card, game's phase is changed to
     * the following one and that the current player is again the first one.
     */
    @Test
    public void completePlanningPhaseTest(){


        PlanningEvent evt = new PlanningEvent(view,0);
        view.playAssistantReceiver(evt);
        evt = new PlanningEvent(view,1);
        view.playAssistantReceiver(evt);
        evt = new PlanningEvent(view,2);
        view.playAssistantReceiver(evt);


        assertEquals(controller.getCurrentPhase(), GamePhases.ACTION_STUDENTSMOVEMENT);
        assertEquals(gm.getCurrentPlayerIndex(),0);


    }

    /**
     * This test verifies that the controller can play a character card in the correct way.
     */
    @Test
    public void playCharacterTest(){
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.MAGICPOSTMAN,gm));

        completePlanningPhaseTest();

        PlayCharacterEvent evt = new PlayCharacterEvent(view,0);
        view.playCharacterReceiver(evt);

        assertTrue(controller.isWaitingForParameters());

        CharactersParametersPayload parameters = new CharactersParametersPayload(new ArrayList<>(),0,2,null, new ArrayList<>());
        CharacterParametersEvent ev2 = new CharacterParametersEvent(view,parameters);
        view.characterParametersReceiver(ev2);

        assertEquals(gm.getPostmanMovements(),2);

    }

    /**
     * This test verifies that the controller sends an error message when a player wants
     * to play a wrong character card. In particular, waitingForParameters is still true.
     */
    @Test
    public void playWrongCharacterTest(){

        completePlanningPhaseTest();

        PlayCharacterEvent evt = new PlayCharacterEvent(view,3);
        view.playCharacterReceiver(evt);

        assertFalse(controller.isWaitingForParameters());

    }

    /**
     * This test verifies that the controller sends an error message when the player wants
     * to play a character card without enough money and that waitingForParameters is still true.
     */
    @Test
    public void playCharacterWithPoorPlayerTest(){
        ConcreteCharacterCreator ccc = new ConcreteCharacterCreator();
        gm.getCharacters().set(0, ccc.createCharacter(Name.MAGICPOSTMAN,gm));

        completePlanningPhaseTest();

        List<Player> temp = gm.getPlayers();
        temp.get(gm.getCurrentPlayerIndex()).removeCoin(1);
        gm.setPlayers(temp);

        PlayCharacterEvent evt = new PlayCharacterEvent(view,0);
        view.playCharacterReceiver(evt);

        assertFalse(controller.isWaitingForParameters());

    }

    /**
     * This test verifies that the move students action of a player works.
     * In particular, the students are put in the correct place and game's phase is changed.
     */
    @Test
    public void moveStudentsTest(){
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

        MoveStudentsEvent evt = new MoveStudentsEvent(view,gm.getPlayers().get(gm.getCurrentPlayerIndex()).getUsername(),
                false,12,providedCreature);

        view.moveStudentsReceiver(evt);
        if(gm.getCurrentPlayerIndex()==0){
            assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getProfessors().get(0).getCreature(), Creature.YELLOW_GNOMES);
            assertEquals(controller.getCurrentStatus().getNumberOfStudentsMoved(),1);
            assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getDiningRoom().getNumberOfStudentsByCreature(Creature.YELLOW_GNOMES),1);
        }


        int oldRed = gm.getTable().getIslands().get(0).getNumberOfStudentsByCreature(Creature.RED_DRAGONS);

        providedCreature.remove(0);
        providedCreature.add(Creature.RED_DRAGONS);
        evt = new MoveStudentsEvent(view,gm.getPlayers().get(gm.getCurrentPlayerIndex()).getUsername(),
                true,0,providedCreature);

        view.moveStudentsReceiver(evt);

        assertEquals(gm.getTable().getIslands().get(0).getNumberOfStudentsByCreature(Creature.RED_DRAGONS),oldRed+1);
        assertEquals(controller.getCurrentStatus().getNumberOfStudentsMoved(),2);

        providedCreature.remove(0);
        providedCreature.add(Creature.RED_DRAGONS);
        evt = new MoveStudentsEvent(view,gm.getPlayers().get(gm.getCurrentPlayerIndex()).getUsername(),
                true,0,providedCreature);

        view.moveStudentsReceiver(evt);

        providedCreature.remove(0);
        providedCreature.add(Creature.YELLOW_GNOMES);
        evt = new MoveStudentsEvent(view,gm.getPlayers().get(gm.getCurrentPlayerIndex()).getUsername(),
                true,0,providedCreature);

        view.moveStudentsReceiver(evt);

        assertEquals(controller.getCurrentStatus().getNumberOfStudentsMoved(),0);
        assertEquals(controller.getCurrentPhase(),GamePhases.ACTION_MOVEMOTHERNATURE);

    }

    /**
     * This test verifies that the controller sends an error message when a player wants to move
     * creatures that aren't in the entrance. The controller doesn't increase the number of students moved
     * and the number of students in the entrance is the same as before.
     */
    @Test
    public void moveWrongStudentsTest(){
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

        MoveStudentsEvent evt = new MoveStudentsEvent(view,gm.getPlayers().get(gm.getCurrentPlayerIndex()).getUsername(),
                false,12,providedCreature);

        view.moveStudentsReceiver(evt);

        assertEquals(controller.getCurrentStatus().getNumberOfStudentsMoved(),0);
        assertEquals(gm.getPlayers().get(gm.getCurrentPlayerIndex()).getEntrance().getStudents().size(),9);

    }

    private void planningIfNo(){
        if(!planned){
            completePlanningPhaseTest();
            planned = true;
        }
    }

    /**
     * This test verifies that mother nature is moved in the correct way using the number provided
     * by the user.
     */
    @Test
    public void moveMotherNatureTest(){
        planningIfNo();
        moveStudentsTest();
        int motherNaturePosition = gm.getTable().getMnPosition();
        IntegerEvent evt = new IntegerEvent(view,1);
        view.integerEventReceiver(evt);


        assertEquals(gm.getTable().getMnPosition(), (motherNaturePosition+1)%gm.getTable().getIslands().size());

    }

    /**
     * This test verifies that the controller doesn't move mother nature when the player chooses a  number
     * too high.
     */
    @Test
    public void moveWrongMotherNatureTest(){
        completePlanningPhaseTest();
        moveStudentsTest();
        int motherNaturePosition = gm.getTable().getMnPosition();
        IntegerEvent evt = new IntegerEvent(view,12);
        view.integerEventReceiver(evt);

        assertEquals(gm.getTable().getMnPosition(), motherNaturePosition);

    }

    /**
     * This test verifies that the controller doesn't move mother nature when the player chooses a negative number
     */
    @Test
    public void moveNegativeMotherNatureTest(){
        completePlanningPhaseTest();
        moveStudentsTest();
        int motherNaturePosition = gm.getTable().getMnPosition();
        IntegerEvent evt = new IntegerEvent(view,-1);
        view.integerEventReceiver(evt);

        assertEquals(gm.getTable().getMnPosition(), motherNaturePosition);

    }

    /**
     * This tests that the selectCloud works correctly moving the students from the selected cloud
     * to the currentPlayer's entrance
     */
    @Test
    public void selectCloudTest(){

        moveMotherNatureTest();
        int currPlayerIndex = gm.getCurrentPlayerIndex();
        int cloudSelected = 0;
        IntegerEvent evt = new IntegerEvent(view,cloudSelected);
        view.integerEventReceiver(evt);

        assertEquals(gm.getTable().getClouds().get(0).getStudents().size(),0);
        assertEquals(gm.getPlayers().get(currPlayerIndex).getEntrance().getStudents().size(),9);
        assertEquals(gm.getCurrentPlayerIndex(),currPlayerIndex+1);

    }

    /**
     * This tests that the selectCloud doesn't work when a non-existent cloud is selected by the user
     */
    @Test
    public void selectWrongCloudTest(){

        moveMotherNatureTest();
        int currPlayerIndex = gm.getCurrentPlayerIndex();
        int cloudSelected = 3;
        IntegerEvent evt = new IntegerEvent(view,cloudSelected);
        view.integerEventReceiver(evt);

        assertEquals(gm.getPlayers().get(currPlayerIndex).getEntrance().getStudents().size(),5);
        for(Cloud c : gm.getTable().getClouds() ){
            assertEquals(c.getStudents().size(),4);
        }

    }

    /**
     * This tests that the selectCloud doesn't work when an already selected cloud is selected by the user
     */
    @Test
    public void selectAlreadySelectedCloudTest(){

        selectCloudTest();
        moveMotherNatureTest();

        int currPlayerIndex = gm.getCurrentPlayerIndex();
        int cloudSelected = 0;
        IntegerEvent evt = new IntegerEvent(view,cloudSelected);
        view.integerEventReceiver(evt);

        assertEquals(gm.getPlayers().get(currPlayerIndex).getEntrance().getStudents().size(),5);
        assertEquals(gm.getTable().getClouds().get(0).getStudents().size(),0);

    }
    /**
     * This tests that when the last cloud is selected, after the moving of students,
     * the currentPhase is set to PLANNING
     */
    @Test
    public void selectLastCloudTest(){

        selectCloudTest();
        moveMotherNatureTest();

        int cloudSelected = 1;
        IntegerEvent evt = new IntegerEvent(view,cloudSelected);
        view.integerEventReceiver(evt);

        moveMotherNatureTest();
        cloudSelected = 2;
        evt = new IntegerEvent(view,cloudSelected);
        view.integerEventReceiver(evt);

        assertEquals(controller.getCurrentPhase(),GamePhases.PLANNING);
        assertEquals(gm.getCurrentPlayerIndex(),0);

    }
}
