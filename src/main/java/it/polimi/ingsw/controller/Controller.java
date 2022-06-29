package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.Listeners.ActionPhaseListener;
import it.polimi.ingsw.controller.Listeners.PlanningPhaseListener;
import it.polimi.ingsw.controller.Listeners.ReconnectionListener;

import static it.polimi.ingsw.controller.enums.GamePhases.*;
import static it.polimi.ingsw.utils.TextAssets.gamePausedText;

import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.controller.events.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.network.server.states.NetworkState;
import it.polimi.ingsw.network.server.SocketID;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.model.studentcontainers.Entrance;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.network.server.networkMessages.payloads.ActionPayload;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.network.server.networkMessages.payloads.ShowModelPayload;
import it.polimi.ingsw.network.server.networkMessages.payloads.StringPayload;
import it.polimi.ingsw.network.server.handlers.MessageHandler;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final int NUMBER_OF_STUDENTS_TO_MOVE;
    private final int MIN_NUMBER_OF_PLAYERS = 1;
    private final GameModel model;
    private final MessageHandler messageHandler;
    private GameStatus currentGameStatus;
    private boolean currentPlayerPlayedCharacter = false;

    private final NetworkState networkState;


    public Controller(GameModel model, MessageHandler messageHandler, GameStatus gameStatus, NetworkState networkState) {

        this.model = model;
        this.messageHandler = messageHandler;


        this.messageHandler.addListener(new ActionPhaseListener(this));
        this.messageHandler.addListener(new PlanningPhaseListener(this));
        this.messageHandler.addListener(new ReconnectionListener(this));
        this.model.addListener(messageHandler);

        currentGameStatus = gameStatus;
        currentGameStatus.setAdvancedRules(model.isAdvancedRules());
        currentGameStatus.setCurrentPlayerUsername(model.getPlayers().get(model.getCurrentPlayerIndex()).getUsername());
        this.networkState = networkState;
        this.NUMBER_OF_STUDENTS_TO_MOVE = model.getNumberOfPlayers() + 1;

    }

    /**
     * This method is called by the game handler when the game has to start;
     * @throws PausedException if the game can't start because of number of players.
     */
    public void startController() throws PausedException {
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
     * This method is used to send a message to the new player connected in order to inform him
     * about his username, the one of the disconnected player;
     * @param socketID contains the socket of the new player to send the message.
     */
    public void reconnection(SocketID socketID) {
        ShowModelPayload showModelPayload = model.showModelPayloadCreator();
        showModelPayload.setUpdateAll();
        showModelPayload.setReconnection();
        messageHandler.eventPerformed(new ShowModelEvent(this, showModelPayload), socketID);
    }

    public void resumeGame() {
        if (messageHandler.resumeGame()) {
            try {
                updateCurrentPlayer();
                sendPhaseMessage(currentGameStatus.getPhase().getHeader());
            } catch (PausedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method is called ad the end of the action phase to check if the game has ended in case
     * of StudentOutOfStockException and to notify the clients about the winner of the game
     */
    public boolean checkIfLastRound() {
        if (model.checkIfLastRound()) {
            Player winner = model.findWinner();
            sendWinnerPlayerMessage(winner);
            return true;
        }
        return false;
    }

    /**
     * This method updates the current player in case of disconnection, turn's end and round's end.
     * @throws PausedException
     */
    public void updateCurrentPlayer() throws PausedException {
        if (networkState.getNumberOfConnectedSocket() > MIN_NUMBER_OF_PLAYERS) {
            Player curr = model.getPlayers().get(model.getCurrentPlayerIndex());
            while (!networkState.isPlayerConnected(curr.getUsername())) {
                boolean isLast = model.getCurrentPlayerIndex() == model.getNumberOfPlayers() - 1;
                model.findNextPlayer();
                if (isLast && currentGameStatus.getPhase().equals(PLANNING)) {
                    currentGameStatus.setPhase(ACTION_STUDENTSMOVEMENT);
                } else if (isLast) {
                    currentGameStatus.setPhase(PLANNING);
                }
            }
            currentGameStatus.setCurrentPlayerUsername(curr.getUsername());
            currentPlayerPlayedCharacter = false;
        } else {
            pauseGame();
        }
    }

    /**
     * This method is used to pause game
     * @throws PausedException when the game has to be paused because there aren't enough players.
     */
    public void pauseGame() throws PausedException {
        messageHandler.pauseGame();
        throw new PausedException();
    }

    /**
     * Used to check the number of players.
     * @return true if the number of players is at least 3.
     */
    public boolean isMoreThanTwoPlayers() {
        return model.getNumberOfPlayers() > 2;
    }

    /**
     * Used to inform players about the winner player of the game.
     * @param winner
     */
    private void sendWinnerPlayerMessage(Player winner) {
        messageHandler.eventPerformed(new BroadcastEvent(this, winner.getUsername(), Headers.winnerPlayer));
    }

    /**
     * Used to create an event containing information about the action allowed to the player.
     * @param phase is used to create the event that contains information about the actions allowed.
     */
    public void sendPhaseMessage(Headers phase) {
        if (phase.equals(Headers.action)) {
            if (currentGameStatus.getPhase().equals(ACTION_STUDENTSMOVEMENT)) {
                messageHandler.eventPerformed(new StatusEvent(this, phase), new ActionPayload(true, false, false, currentGameStatus.isAdvancedRules(), currentGameStatus.getCurrentPlayerUsername()));
            } else if (currentGameStatus.getPhase().equals(ACTION_MOVEMOTHERNATURE)) {
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

    /**
     * Used to send an error message to the current player.
     * @param string is the content of the message.
     */
    private void sendErrorMessage(String string) {
        int id = 0;
        for (SocketID socketID : networkState.getSocketIDList()) {
            if (socketID.isConnected()) {
                if (socketID.getPlayerInfo().getUsername().equals(
                        model.getPlayers().get(model.getCurrentPlayerIndex()).getUsername())) {
                    id = socketID.getId();
                }
            }

        }
        messageHandler.eventPerformed(new StringEvent(this, string, Headers.errorMessage,
                networkState.getSocketByID(id)));
    }

    /**
     * Used to send a message informing the current player that he has played a character.
     * @param name
     */
    private void sendCharacterPlayedMessage(Name name) {
        int id = 0;
        for (SocketID socketID : networkState.getSocketIDList()) {
            if (socketID.getPlayerInfo().getUsername().equals(
                    model.getPlayers().get(model.getCurrentPlayerIndex()).getUsername())) {
                id = socketID.getId();
            }
        }
        messageHandler.eventPerformed(new CharacterPlayedEvent(this, name, networkState.getSocketByID(id)));
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
                try {
                    updateCurrentPlayer();
                    sendPhaseMessage(currentGameStatus.getPhase().getHeader());
                } catch (PausedException e) {
                    System.out.println(gamePausedText);
                }
            }
        } catch (AssistantAlreadyPlayedException a) {
            sendErrorMessage("Already played assistant, play another one");
        } catch (PlanningPhaseEndedException p) {
            model.establishRoundOrder();
            currentGameStatus.setPhase(ACTION_STUDENTSMOVEMENT);
            try {
                updateCurrentPlayer();
                sendPhaseMessage(currentGameStatus.getPhase().getHeader());
            } catch (PausedException e) {
                System.out.println(gamePausedText);
            }
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
        boolean isOkEntrance = false;

        if (evt.isDestinationIsland()) {

            Table table = model.getTable();
            Island destinationIsland = table.getIslands().get(evt.getIndexOfIsland());

            isOkEntrance = setDestination(playerEntrance, destinationIsland, evt.getCreatureList());

            table.setIndexIsland(evt.getIndexOfIsland(), destinationIsland);
            model.setTable(table);

        } else {
            DiningRoom playerDiningRoom = players.get(model.getCurrentPlayerIndex()).getDiningRoom();

            isOkEntrance = setDestination(playerEntrance, playerDiningRoom, evt.getCreatureList());

            players.get(model.getCurrentPlayerIndex()).setDiningRoom(playerDiningRoom);
        }
        if (isOkEntrance) {
            players.get(model.getCurrentPlayerIndex()).setEntrance(playerEntrance);
            model.setPlayers(players);
            model.checkProfessor();
            model.coinGiver();
            sendPhaseMessage(Headers.action);
        }
    }

    private boolean setDestination(StudentContainer source, StudentContainer destination, List<Creature> creatures) {
        if (!(model.moveStudents(source, destination, creatures))) {
            sendErrorMessage("Wrong creatures in entrance, try again");
            return false;
        } else {
            currentGameStatus.setNumberOfStudentsMoved(currentGameStatus.getNumberOfStudentsMoved() + 1);
            if (currentGameStatus.getNumberOfStudentsMoved() == NUMBER_OF_STUDENTS_TO_MOVE) {
                currentGameStatus.setPhase(ACTION_MOVEMOTHERNATURE);
                currentGameStatus.setNumberOfStudentsMoved(0);
            }
        }
        return true;
    }

    /**
     * This method moves mother nature of the number of jumps provided by the player
     *
     * @param jumps is the number of jumps
     */
    public void moveMotherNature(int jumps) {
        try {
            if (!(model.moveMotherNature(jumps))) {
                sendErrorMessage("Incorrect number of jumps provided");
            } else {
                currentGameStatus.setPhase(ACTION_CLOUDCHOICE);
                sendPhaseMessage(Headers.action);
            }
        } catch (GameEndedException e) {
            if (model.checkEndGame()) {
                sendWinnerPlayerMessage(model.findWinner());
            }
        }

    }

    /**
     * Used to move students from the selected cloud to the player's entrance.
     * @param indexOfCloud is the cloud selected by the current player
     */
    public void selectCloud(int indexOfCloud) {
        int currentPlayerIndex = model.getCurrentPlayerIndex();
        try {
            if (!(model.moveFromSelectedCloud(indexOfCloud))) {
                sendErrorMessage("Incorrect index of cloud provided");
            } else {
                if (currentPlayerIndex == model.getNumberOfPlayers() - 1) {
                    if (!checkIfLastRound()) {
                        currentGameStatus.setPhase(PLANNING);
                        try {
                            updateCurrentPlayer();
                            sendPhaseMessage(currentGameStatus.getPhase().getHeader());
                        } catch (PausedException e) {
                            System.out.println(gamePausedText);
                        }

                    }
                } else {
                    currentGameStatus.setPhase(ACTION_STUDENTSMOVEMENT);
                    try {
                        updateCurrentPlayer();
                        sendPhaseMessage(currentGameStatus.getPhase().getHeader());
                    } catch (PausedException e) {
                        System.out.println(gamePausedText);
                    }

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
                Name playedCharacterName = model.getCharacters().get(model.getPlayedCharacter()).getName();
                if (playedCharacterName.needsParameters()) {
                    currentGameStatus.toggleWaitingForParameters();
                    sendCharacterPlayedMessage(playedCharacterName);
                } else {
                    sendCharacterPlayedMessage(playedCharacterName);
                    effect();
                }
                currentPlayerPlayedCharacter = true;
            }

        } catch (IndexOutOfBoundsException e) {
            sendErrorMessage("Non existent character, try again");
        }

    }

    public void effect(CharactersParametersPayload parameters) {
        if (isWaitingForParameters()) {
            try {
                if (model.effect(parameters)) {
                    currentGameStatus.toggleWaitingForParameters();
                    sendPhaseMessage(Headers.action);
                } else {
                    sendErrorMessage("Wrong Parameters");
                }
            } catch (GameEndedException e) {
                if (model.checkEndGame()) {
                    sendWinnerPlayerMessage(model.findWinner());
                }
            } catch (UnplayableEffectException e) {
                sendErrorMessage("You can't play that character");
                sendPhaseMessage(Headers.action);
            }

        }
    }

    public void effect() {
        try {
            model.effect(new CharactersParametersPayload(new ArrayList<>(), 0, 0, new ArrayList<>()));
            sendPhaseMessage(Headers.action);
        } catch (GameEndedException | UnplayableEffectException ignore) {
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

    public void setCurrentStatus(GameStatus providedGS) {
        this.currentGameStatus = providedGS;
    }

    public boolean isWaitingForParameters() {
        return currentGameStatus.isWaitingForParameters();
    }

    /**
     * This method is used to check if a player has already played a character in his turn.
     * @return true if the player has already played a character.
     */
    public boolean getCurrentPlayerPlayedCharacter() {
        return currentPlayerPlayedCharacter;
    }

    public NetworkState getNetworkState(){ return networkState; }
}