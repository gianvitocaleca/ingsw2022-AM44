package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.NetworkState;
import it.polimi.ingsw.server.SocketID;
import it.polimi.ingsw.server.controller.Listeners.ActionPhaseListener;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.controller.Listeners.PlanningPhaseListener;
import it.polimi.ingsw.server.controller.events.*;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.characters.Character;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.server.model.exceptions.CloudAlreadySelectedException;
import it.polimi.ingsw.server.model.exceptions.PlanningPhaseEndedException;
import it.polimi.ingsw.server.model.gameboard.Table;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.server.model.studentcontainers.Entrance;
import it.polimi.ingsw.server.model.studentcontainers.Island;
import it.polimi.ingsw.server.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.server.model.students.Student;
import it.polimi.ingsw.server.networkMessages.*;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
    /*
    Controller must:
    -reset the standard evaluator at the end of every ActionPhase
    -reset postmanMovements at the end of the ActionPhase
    -do all resets with a proper reset method in GameModel
    -check if a player needs to earn a coin and give it to him, if possible, and proceeds to remove it from the coin reserve
     */
    private final int NUMBER_OF_STUDENTS_TO_MOVE_BASIC = 3;
    private final int NUMBER_OF_STUDENTS_TO_MOVE_ADVANCED = 4;
    private GameModel model;
    private MessageHandler messageHandler;
    private GameStatus currentGameStatus;
    private boolean currentPlayerPlayedCharacter = false;

    private NetworkState networkState;


    public Controller(GameModel model, MessageHandler messageHandler, GameStatus gameStatus, NetworkState networkState) {

        this.model = model;
        this.messageHandler = messageHandler;


        this.messageHandler.addListener(new ActionPhaseListener(this));
        this.messageHandler.addListener(new PlanningPhaseListener(this));
        this.model.addListener(messageHandler);

        currentGameStatus = gameStatus;
        currentGameStatus.setAdvancedRules(model.isAdvancedRules());
        currentGameStatus.setCurrentPlayerUsername(model.getPlayers().get(model.getCurrentPlayerIndex()).getUsername());
        this.networkState = networkState;

    }

    public void start() {
        updateCurrentPlayer();

        ShowModelPayload modelUpdate = model.showModelPayloadCreator();
        modelUpdate.setUpdateAll();
        model.showModel(modelUpdate);

        switch (currentGameStatus.getPhase()) {
            case PLANNING:
                sendPhaseMessage(Headers.planning);
                break;
            case ACTION_MOVEMOTHERNATURE:
            case ACTION_STUDENTSMOVEMENT:
            case ACTION_CLOUDCHOICE:
            case ACTION_PLAYED_CHARACTER:
                sendPhaseMessage(Headers.action);
                break;
        }
    }

    /**
     * This method is called ad the end of the action phase to check if the game has ended in case of StudentOutOfStockException and to notify the clients about
     * the winner of the game
     */
    private boolean checkIfLastRound() {
        if (model.checkIfLastRound()) {
            Player winner = model.findWinner();
            sendWinnerPlayerMessage(winner);
            return true;
        }
        return false;
    }

    private void updateCurrentPlayer() {
        Player curr = model.getPlayers().get(model.getCurrentPlayerIndex());
        currentGameStatus.setCurrentPlayerUsername(curr.getUsername());
        //messageHandler.eventPerformed(new BroadcastEvent(this, curr.getUsername(), Headers.currentPlayer));
    }

    private void sendWinnerPlayerMessage(Player winner) {
        messageHandler.eventPerformed(new StringEvent(this, winner.getUsername(), Headers.winnerPlayer, new Socket()));
    }

    private void sendPhaseMessage(Headers phase) {
        if (phase.equals(Headers.action)) {
            if (currentGameStatus.equals(GamePhases.ACTION_STUDENTSMOVEMENT)) {
                messageHandler.eventPerformed(new StatusEvent(this, phase), new ActionPayload(true, false, false, currentGameStatus.isAdvancedRules(), currentGameStatus.getCurrentPlayerUsername()));
            } else if (currentGameStatus.equals(GamePhases.ACTION_MOVEMOTHERNATURE)) {
                if (currentPlayerPlayedCharacter) {
                    messageHandler.eventPerformed(new StatusEvent(this, phase), new ActionPayload(false, true, false, false, currentGameStatus.getCurrentPlayerUsername()));
                } else {
                    messageHandler.eventPerformed(new StatusEvent(this, phase), new ActionPayload(false, true, false, currentGameStatus.isAdvancedRules(), currentGameStatus.getCurrentPlayerUsername()));
                }
            } else {
                if (currentPlayerPlayedCharacter) {
                    messageHandler.eventPerformed(new StatusEvent(this, phase), new ActionPayload(false, false, true, false, currentGameStatus.getCurrentPlayerUsername()));
                } else {
                    messageHandler.eventPerformed(new StatusEvent(this, phase), new ActionPayload(false, false, true, currentGameStatus.isAdvancedRules(), currentGameStatus.getCurrentPlayerUsername()));
                }
            }
        } else {
            if (model.getCurrentPlayerIndex() == 0) {
                model.fillClouds();
            }
            messageHandler.eventPerformed(new StatusEvent(this, phase), new StringPayload(currentGameStatus.getCurrentPlayerUsername()));
        }

    }

    private void sendErrorMessage(String string) {
        int id = 0;
        for (SocketID socketID : networkState.getSocketIDList()) {
            if (socketID.getPlayerInfo().getUsername().equals(
                    model.getPlayers().get(model.getCurrentPlayerIndex()).getUsername())) {
                id = socketID.getId();
            }
        }
        System.out.println(string);
        messageHandler.eventPerformed(new StringEvent(this, string, Headers.errorMessage,
                networkState.getSocketByID(id)));
    }

    private void sendCharacterPlayedMessage(Name name) {
        messageHandler.eventPerformed(new CharacterPlayedEvent(this, name));
    }

    /**
     * this method plays the assistant card and informs the client if an error occurs or it ends well.
     *
     * @param indexOfAssistant is the assistant card the player wants to play
     */
    public void playAssistant(int indexOfAssistant) {

        try {
            if (!(model.playAssistant(indexOfAssistant))) {
                sendErrorMessage("Non existent assistant, play another one");
            } else {
                updateCurrentPlayer();
                sendPhaseMessage(Headers.planning);
            }
        } catch (AssistantAlreadyPlayedException a) {
            sendErrorMessage("Already played assistant, play another one");
        } catch (PlanningPhaseEndedException p) {
            model.establishRoundOrder();
            currentGameStatus.setPhase(GamePhases.ACTION_STUDENTSMOVEMENT);
            updateCurrentPlayer();
            sendPhaseMessage(Headers.action);
        }
    }

    /**
     * This method let currentPlayer move 3 students from entrance to diningRoom or Island
     *
     * @param evt contains information about source creatures and destination
     */
    public void moveStudents(MoveStudentsEvent evt) {
        List<Player> players = model.getPlayers();
        Entrance playerEntrance = players.get(model.getCurrentPlayerIndex()).getEntrance();

        if (evt.isDestinationIsland()) {

            Table table = model.getTable();
            Island destinationIsland = table.getIslands().get(evt.getIndexOfIsland());

            setDestination(playerEntrance, destinationIsland, evt.getCreatureList());

            table.setIndexIsland(evt.getIndexOfIsland(), destinationIsland);
            model.setTable(table);

        } else {
            DiningRoom playerDiningRoom = players.get(model.getCurrentPlayerIndex()).getDiningRoom();

            setDestination(playerEntrance, playerDiningRoom, evt.getCreatureList());

            players.get(model.getCurrentPlayerIndex()).setDiningRoom(playerDiningRoom);
        }

        players.get(model.getCurrentPlayerIndex()).setEntrance(playerEntrance);
        model.setPlayers(players);
        model.checkProfessor();
    }

    private void setDestination(StudentContainer source, StudentContainer destination, List<Creature> creatures) {
        if (!(model.moveStudents(source, destination, creatures))) {
            sendErrorMessage("Wrong creatures in entrance, try again");
        } else {
            currentGameStatus.setNumberOfStudentsMoved(currentGameStatus.getNumberOfStudentsMoved() + 1);
            if (currentGameStatus.isAdvancedRules()) {
                if (currentGameStatus.getNumberOfStudentsMoved() == NUMBER_OF_STUDENTS_TO_MOVE_ADVANCED) {
                    currentGameStatus.setPhase(GamePhases.ACTION_MOVEMOTHERNATURE);
                    currentGameStatus.setNumberOfStudentsMoved(0);
                    sendPhaseMessage(Headers.action);
                }
            } else {
                if (currentGameStatus.getNumberOfStudentsMoved() == NUMBER_OF_STUDENTS_TO_MOVE_BASIC) {
                    currentGameStatus.setPhase(GamePhases.ACTION_MOVEMOTHERNATURE);
                    currentGameStatus.setNumberOfStudentsMoved(0);
                    sendPhaseMessage(Headers.action);
                }
            }

        }
    }

    /**
     * This method moves mother nature of the number of jumps provided by the player
     *
     * @param jumps is the number of jumps
     */
    public void moveMotherNature(int jumps) {
        if (!(model.moveMotherNature(jumps))) {
            sendErrorMessage("Incorrect number of jumps provided");
        } else {
            currentGameStatus.setPhase(GamePhases.ACTION_CLOUDCHOICE);
            sendPhaseMessage(Headers.action);
        }
    }

    public void selectCloud(int indexOfCloud) {
        int currentPlayerIndex = model.getCurrentPlayerIndex();
        try {
            if (!(model.moveFromSelectedCloud(indexOfCloud))) {
                sendErrorMessage("Incorrect index of cloud provided");
            } else {
                if (currentPlayerIndex == model.getNumberOfPlayers() - 1) {
                    if (!checkIfLastRound()) {
                        currentGameStatus.setPhase(GamePhases.PLANNING);
                        sendPhaseMessage(Headers.planning);
                        updateCurrentPlayer();
                    }
                } else {
                    currentGameStatus.setPhase(GamePhases.ACTION_STUDENTSMOVEMENT);
                    sendPhaseMessage(Headers.action);
                    updateCurrentPlayer();
                }
            }
        } catch (CloudAlreadySelectedException e) {
            sendErrorMessage("Cloud already selected, try again");
        }
    }

    /**
     * this method play a character card and it toggles waitingForParameters.
     * waitingForParameters is true when the client has to specify what he wants to do with the character card he played.
     *
     * @param indexOfCharacter is the character the player wants to play.
     */
    public void playCharacter(int indexOfCharacter) {
        try {

            if (!(model.playCharacter(indexOfCharacter))) {
                sendErrorMessage("You don't have enough coins");
            } else {
                currentGameStatus.toggleWaitingForParameters();
                sendCharacterPlayedMessage(model.getCharacters().get(model.getPlayedCharacter()).getName());
            }

        } catch (IndexOutOfBoundsException e) {
            sendErrorMessage("Non existent character, try again");
        }

    }

    public void effect(CharactersParametersPayload parameters) {
        if (isWaitingForParameters()) {
            model.effect(parameters);
        } else {
            sendErrorMessage("You cannot play the character right now!");
        }

    }

    public GamePhases getCurrentPhase() {
        return currentGameStatus.getPhase();
    }

    public GameStatus getCurrentStatus() {
        GameStatus temp = new GameStatus(currentGameStatus.getPhase(), currentGameStatus.isAdvancedRules());
        temp.setNumberOfStudentsMoved(currentGameStatus.getNumberOfStudentsMoved());
        temp.setCurrentPlayerUsername(currentGameStatus.getCurrentPlayerUsername());
        if (currentGameStatus.isWaitingForParameters()) {
            temp.toggleWaitingForParameters();
        }
        return temp;
    }

    public boolean isWaitingForParameters() {
        return currentGameStatus.isWaitingForParameters();
    }

}
