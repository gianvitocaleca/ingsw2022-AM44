package it.polimi.ingsw.network.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.controller.events.*;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.network.server.networkMessages.Message;
import it.polimi.ingsw.network.server.networkMessages.payloads.*;
import it.polimi.ingsw.network.server.SocketID;
import it.polimi.ingsw.controller.Listeners.ActionPhaseListener;
import it.polimi.ingsw.controller.Listeners.PlanningPhaseListener;
import it.polimi.ingsw.controller.Listeners.ReconnectionListener;
import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Wizard;
import it.polimi.ingsw.network.server.sender.MessageSenderServer;
import it.polimi.ingsw.network.server.states.CreationState;
import it.polimi.ingsw.network.server.states.LoginState;
import it.polimi.ingsw.network.server.states.NetworkState;

import javax.swing.event.EventListenerList;
import java.net.Socket;
import java.util.*;

public class MessageHandler implements EventListener {
    private Gson gson;
    private EventListenerList listeners = new EventListenerList();
    private String message;

    private MessageSenderServer mss;

    private final int ERROR = 18;

    private LoginState loginState;
    private CreationState creationState;
    private boolean gamePaused = false;

    /**
     * Used to translate and encode the messages.
     * Creates the correct event and uses the listeners to handle it.
     * @param state is the network state, used to send the messages
     */
    public MessageHandler(NetworkState state) {
        gson = new Gson();
        setNetworkState(state);
    }

    /**
     *
     * @param loginState is the current login state
     */
    public void setLoginState(LoginState loginState) {
        this.loginState = loginState;
    }

    /**
     *
     * @param networkState is the current network state
     */
    public void setNetworkState(NetworkState networkState) {
        mss = new MessageSenderServer(networkState);
    }

    /**
     *
     * @param creationState is the current creation state
     */
    public void setCreationState(CreationState creationState) {
        this.creationState = creationState;
    }

    /**
     *
     * @param listener is the planning phase listener
     */
    public void addListener(PlanningPhaseListener listener) {
        listeners.add(PlanningPhaseListener.class, listener);
    }

    /**
     *
     * @param listener is the reconnection listener
     */
    public void addListener(ReconnectionListener listener) {
        listeners.add(ReconnectionListener.class, listener);
    }

    /**
     *
     * @param listener is the action phase listener
     */
    public void addListener(ActionPhaseListener listener) {
        listeners.add(ActionPhaseListener.class, listener);
    }

    /**
     * Used to send a message on the network
     * @param evt is the status event
     * @param payload is the provided payload
     */
    public void eventPerformed(StatusEvent evt, Payload payload) {
        message = gson.toJson(new Message(evt.getHeader(), payload));
        mss.sendBroadcastMessage(message);
    }

    /**
     * Used to send a message on the network
     * @param evt is the played character event
     */
    public void eventPerformed(CharacterPlayedEvent evt) {
        message = gson.toJson(new Message(Headers.characterPlayed, new CharacterPlayedPayload(evt.getCharactersName())));
        mss.sendBroadcastMessage(message);
    }

    /**
     * Used to send a message on the network
     * @param evt is the show model event
     */
    public void eventPerformed(ShowModelEvent evt) {
        message = gson.toJson(new Message(Headers.showModelMessage, evt.getPayload()));
        mss.sendBroadcastMessage(message);
    }

    /**
     * Used to send a message on the network
     * @param evt is the string event
     */
    public void eventPerformed(StringEvent evt) {
        message = gson.toJson(new Message(evt.getHeader(), new StringPayload(evt.getMessage())));
        mss.sendMessage(message, evt.getSocket());
    }

    /**
     * Used to send a message on the network
     * @param evt is the close connection event
     */
    public void eventPerformed(CloseConnectionEvent evt) {
        message = gson.toJson(new Message(Headers.closeConnection, new StringPayload("")));
        mss.sendMessage(message, evt.getSocket());
    }

    /**
     * Used to send a message on the network
     * @param evt is the broadcast event
     */
    public void eventPerformed(BroadcastEvent evt) {
        message = gson.toJson(new Message(evt.getHeader(), new StringPayload(evt.getMessage())));
        mss.sendBroadcastMessage(message);
    }

    /**
     * Used to send a message on the network
     * @param evt is the show model event
     * @param socketID is the socket to send the message to
     */
    public void eventPerformed(ShowModelEvent evt, SocketID socketID) {
        message = gson.toJson(new Message(Headers.showModelMessage, evt.getPayload()));
        mss.sendMessage(message, socketID.getSocket());
    }

    /**
     * Used to handle a received message
     * @param evt is the received message event
     * @param sourceSocket is the socket which sent the message
     */
    public synchronized void eventPerformed(MessageReceivedEvent evt, Socket sourceSocket) {
        JsonObject jsonTree = JsonParser.parseString(evt.getMessage()).getAsJsonObject();
        JsonElement jsonHeader = jsonTree.get("header");
        Headers header = gson.fromJson(jsonHeader, Headers.class);

        JsonElement jsonPayload = jsonTree.get("payload");


        switch (header) {

            case creationRequirementMessage_NumberOfPlayers:
            case creationRequirementMessage_TypeOfRules:
                StringPayload creationPayload = gson.fromJson(jsonPayload, StringPayload.class);
                int ans;
                try {
                    ans = Integer.parseInt(creationPayload.getString());
                } catch (NumberFormatException ignore) {
                    ans = ERROR;
                }
                creationMessageReceiver(ans);
                break;
            case loginMessage_Username:
                LoginPayload loginPayload = gson.fromJson(jsonPayload, LoginPayload.class);
                loginMessageReceiver(sourceSocket, loginPayload, GamePhases.LOGIN_USERNAME);
                break;
            case loginMessage_Color:
                LoginPayload loginPayload1 = gson.fromJson(jsonPayload, LoginPayload.class);
                loginMessageReceiver(sourceSocket, loginPayload1, GamePhases.LOGIN_COLOR);
                break;
            case loginMessage_Wizard:
                LoginPayload loginPayload2 = gson.fromJson(jsonPayload, LoginPayload.class);
                loginMessageReceiver(sourceSocket, loginPayload2, GamePhases.LOGIN_WIZARD);
                break;
            case planning:
                PlanningAnswerPayload planningAnswerPayload = gson.fromJson(jsonPayload, PlanningAnswerPayload.class);
                playAssistantReceiver(new PlanningEvent(this, planningAnswerPayload.getIndexOfAssistant()));
                break;
            case action:
                ActionAnswerPayload actionAnswerPayload = gson.fromJson(jsonPayload, ActionAnswerPayload.class);
                createActionEvent(actionAnswerPayload);
                break;
            case characterPlayed:
                CharactersParametersPayload charactersParametersPayload = gson.fromJson(jsonPayload, CharactersParametersPayload.class);
                characterParametersReceiver(new CharacterParametersEvent(this, charactersParametersPayload));
            case ping:
                break;
            default:
                System.out.println("Wrong header provided! How did this happen?");
                break;
        }


    }

    /**
     * Used to create the action event
     * @param aap is the received payload
     */
    private void createActionEvent(ActionAnswerPayload aap) {
        if (aap.isMoveStudents()) {
            List<Creature> studentCreature = new ArrayList<>();
            studentCreature.add(aap.getStudentCreatureToMove());
            MoveStudentsEvent event = new MoveStudentsEvent(this, !aap.isDestinationDiningRoom(), aap.getClientInt(), studentCreature);
            moveStudentsReceiver(event);
        }
        if (aap.isMoveMotherNature() || aap.isSelectCloud()) {
            IntegerEvent event = new IntegerEvent(this, aap.getClientInt());
            integerEventReceiver(event);
        }
        if (aap.isPlayCharacter()) {
            PlayCharacterEvent event = new PlayCharacterEvent(this, aap.getClientInt());
            playCharacterReceiver(event);
        }
    }

    /**
     * Used to set the number of players and type of rules for the game
     * @param num is the number of players for the game
     */
    public void creationMessageReceiver(int num) {
        if (gamePaused) return;
        if (creationState.getPhase().equals(GamePhases.CREATION_NUMBER_OF_PLAYERS)) {
            creationState.setNumberOfPlayers(num);
        } else if (creationState.getPhase().equals(GamePhases.CREATION_RULES)) {
            creationState.setAdvancedRules(num);
        }
    }

    /**
     * Used to set the username, color and wizard of the player
     * @param socket is the source socket
     * @param loginPayload is the payload containing the info
     * @param gamePhases is the current phase of the login
     */
    public void loginMessageReceiver(Socket socket, LoginPayload loginPayload, GamePhases gamePhases) {
        if (gamePaused) return;
        switch (gamePhases) {
            case LOGIN_USERNAME:
                loginState.setUsername(socket, loginPayload.getString());
                break;
            case LOGIN_COLOR:
                int color;
                try {
                    color = Integer.parseInt(loginPayload.getString());
                } catch (NumberFormatException e) {
                    color = 5;
                }
                switch (color) {
                    case 1:
                        loginState.setColor(socket, Color.WHITE);
                        break;
                    case 2:
                        loginState.setColor(socket, Color.BLACK);
                        break;
                    case 3:
                        loginState.setColor(socket, Color.GREY);
                        break;
                    default:
                        loginState.setColor(socket, Color.WRONG);
                        break;
                }

                break;
            case LOGIN_WIZARD:
                int wizard;
                try {
                    wizard = Integer.parseInt(loginPayload.getString());
                } catch (NumberFormatException e) {
                    wizard = 5;
                }
                switch (wizard) {
                    case 1:
                        loginState.setWizard(socket, Wizard.GANDALF);
                        break;
                    case 2:
                        loginState.setWizard(socket, Wizard.BALJEET);
                        break;
                    case 3:
                        loginState.setWizard(socket, Wizard.SABRINA);
                        break;
                    case 4:
                        loginState.setWizard(socket, Wizard.KENJI);
                        break;
                    default:
                        loginState.setWizard(socket, Wizard.WRONG);
                        break;
                }

                break;
            default:
                break;
        }
    }

    /**
     * The listener performs the provided event
     * @param evt is the planning event
     */
    public void playAssistantReceiver(PlanningEvent evt) {
        if (gamePaused) return;
        for (PlanningPhaseListener event : listeners.getListeners(PlanningPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

    /**
     * The listener performs the provided event
     * @param evt is the played character event
     */
    public void playCharacterReceiver(PlayCharacterEvent evt) {
        if (gamePaused) return;
        for (ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

    /**
     * The listener performs the provided event
     * @param evt is the character parameters event
     */
    public void characterParametersReceiver(CharacterParametersEvent evt) {
        if (gamePaused) return;
        for (ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

    /**
     * The listener performs the provided event
     * @param evt is the move student event
     */
    public void moveStudentsReceiver(MoveStudentsEvent evt) {
        if (gamePaused) return;
        for (ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

    /**
     * The listener performs the provided event
     * @param evt is the integer event
     */
    public void integerEventReceiver(IntegerEvent evt) {
        if (gamePaused) return;
        for (ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)) {
            event.eventPerformed(evt);
        }
    }

    /**
     * This method is used to inform controller about the new client and the client about his username.
     * The client has to set the username, he hasn't chosen.
     *
     * @param evt contains information about the socket of the new client and his username.
     */
    public void userReconnectedReceiver(ReconnectedEvent evt) {
        message = gson.toJson(new Message(Headers.reconnection, new ReconnectionPayload(evt.getUsername())));
        mss.sendMessage(message, evt.getSocketID().getSocket());
        for (ReconnectionListener event : listeners.getListeners(ReconnectionListener.class)) {
            event.eventPerformed(evt);
        }
    }

    /**
     * Used to notify the disconnection of a player
     * @param evt is the disconnection event
     */
    public void eventPerformed(DisconnectionEvent evt) {
        for (ReconnectionListener event : listeners.getListeners(ReconnectionListener.class)) {
            event.eventPerformed(evt);
        }
    }

    /**
     * Used to put the game on pause
     */
    public void pauseGame() {
        this.gamePaused = true;
        eventPerformed(new BroadcastEvent(this, "Game paused", Headers.errorMessage));
    }

    /**
     * Used to unpause the game
     * @return whether the method was successful
     */
    public boolean resumeGame() {
        if (gamePaused) {
            gamePaused = false;
            return true;
        }
        return false;
    }
}
