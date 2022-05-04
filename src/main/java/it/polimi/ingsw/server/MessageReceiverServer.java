package it.polimi.ingsw.server;

import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.controller.GameStatus;
import it.polimi.ingsw.server.controller.events.MessageReceivedEvent;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

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

    private final int pingTime = 10000;
    private final int maxNoAnswers = 4;

    public MessageReceiverServer(SocketID socketId, MessageHandler listener, GameStatus gameStatus, NetworkState networkState) {
        this.socketId = socketId;
        listeners.add(MessageHandler.class, listener);
        this.gameStatus = gameStatus;
        this.networkState = networkState;
        pingState = new PingState();
        serverPingHandler = new ServerPingHandler(pingState,networkState,socketId,pingTime,maxNoAnswers);
        try {
            in = new Scanner(socketId.getSocket().getInputStream());
        } catch (IOException e) {
            System.out.println("Server read error");
        }
    }


    @Override
    public void run() {
        serverPingHandler.start();
        while (true) {
            try{
                String line = in.nextLine();
                pingState.setReceived(true);
                if(!networkState.getServerPhase().equals(ServerPhases.GAME)|| isCurrent()){
                    MessageReceivedEvent evt = new MessageReceivedEvent(this, line);
                    for (MessageHandler event : listeners.getListeners(MessageHandler.class)) {
                        event.eventPerformed(evt, socketId.getSocket());
                    }
                }
            }catch (NoSuchElementException ignore){
                if(pingState.isCloseConnection()){
                    break;
                }
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
