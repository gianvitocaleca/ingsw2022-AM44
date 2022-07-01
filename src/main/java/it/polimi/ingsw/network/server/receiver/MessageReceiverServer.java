package it.polimi.ingsw.network.server.receiver;

import it.polimi.ingsw.network.ping.PingState;
import it.polimi.ingsw.network.ping.ServerPingHandler;
import it.polimi.ingsw.network.server.enums.ServerPhases;
import it.polimi.ingsw.network.server.handlers.MessageHandler;
import it.polimi.ingsw.network.server.states.NetworkState;
import it.polimi.ingsw.network.server.SocketID;
import it.polimi.ingsw.controller.GameStatus;
import it.polimi.ingsw.controller.events.MessageReceivedEvent;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MessageReceiverServer extends Thread {

    private SocketID socketId;

    private NetworkState networkState;

    private GameStatus gameStatus;

    private EventListenerList listeners = new EventListenerList();

    private ServerPingHandler serverPingHandler;

    private PingState pingState;
    private Scanner in;

    private final int pingTime = 1000;
    private final int maxNoAnswers = 4;

    /**
     * Used to receive the messages and create the events to handle
     * @param socketId is the socket of the player that's sending the messages
     * @param messageHandler is the listener for the events
     * @param gameStatus is the current game state
     * @param networkState is the current network state
     */
    public MessageReceiverServer(SocketID socketId, MessageHandler messageHandler, GameStatus gameStatus, NetworkState networkState) {
        this.socketId = socketId;
        listeners.add(MessageHandler.class, messageHandler);
        this.gameStatus = gameStatus;
        this.networkState = networkState;
        pingState = new PingState();
        try {
            in = new Scanner(socketId.getSocket().getInputStream());
        } catch (IOException e) {
            System.out.println("Server read error");
        }
        serverPingHandler = new ServerPingHandler(pingState, networkState, socketId, pingTime, maxNoAnswers,in);
        serverPingHandler.addListener(messageHandler);

    }

    /**
     * Used to handle the ping mechanism and to create the events for the listener
     */
    @Override
    public void run() {
        serverPingHandler.start();
        while (true) {
            try {
                String line = in.nextLine();
                pingState.setReceived(true);
                if (socketId.isConnected()) {
                    if (!networkState.getServerPhase().equals(ServerPhases.GAME) || isCurrent()) {
                        MessageReceivedEvent evt = new MessageReceivedEvent(this, line);
                        for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
                            event.eventPerformed(evt, socketId.getSocket());
                        }
                    }
                } else {
                    break;
                }

            } catch (NoSuchElementException ignore) {
                if (pingState.isCloseConnection()) {
                    break;
                }
            } catch (IllegalStateException e){
                break;
            }

        }

    }

    /**
     *
     * @return whether the player is the current
     */
    private boolean isCurrent() {
        return socketId.getPlayerInfo().getUsername().equals(gameStatus.getCurrentPlayerUsername());
    }

}
