package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.GameStatus;
import it.polimi.ingsw.server.controller.events.MessageReceivedEvent;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.util.Scanner;

public class MessageReceiverServer extends Thread {

    private SocketID socketId;

    private NetworkState networkState;

    private GameStatus gameStatus;

    private EventListenerList listeners = new EventListenerList();

    private PingHandler pingHandler;

    private PingState pingState;

    public MessageReceiverServer(SocketID socketId, MessageHandler listener, GameStatus gameStatus, NetworkState networkState) {
        this.socketId = socketId;
        listeners.add(MessageHandler.class, listener);
        this.gameStatus = gameStatus;
        this.networkState = networkState;
        pingState = new PingState();
        pingHandler = new PingHandler(networkState,pingState, socketId);
    }


    @Override
    public void run() {
        pingHandler.start();
        while (true) {
            try {
                Scanner in = new Scanner(socketId.getSocket().getInputStream());
                String line = in.nextLine();
                pingState.setReceived(true);
                if(!networkState.getServerPhase().equals(ServerPhases.GAME)|| isCurrent()){
                    MessageReceivedEvent evt = new MessageReceivedEvent(this, line);
                    for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
                        event.eventPerformed(evt, socketId.getSocket());
                    }
                }
            } catch (IOException ignore) {
                System.out.println("Server read error");
            }
        }

    }

    private boolean isCurrent(){
        for(SocketID s : networkState.getSocketIDList()){
            if (s.getPlayerInfo().getUsername().equals(gameStatus.getCurrentPlayerUsername())) return true;
        }
        return false;
    }

}
