package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.GameStatus;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.controller.events.CloseConnectionEvent;
import it.polimi.ingsw.server.controller.events.ReconnectedEvent;
import it.polimi.ingsw.server.enums.ServerPhases;
import it.polimi.ingsw.server.handlers.CreationHandler;
import it.polimi.ingsw.server.handlers.GameHandler;
import it.polimi.ingsw.server.handlers.LoginHandler;
import it.polimi.ingsw.server.receiver.MessageReceiverServer;
import it.polimi.ingsw.server.handlers.MessageHandler;
import it.polimi.ingsw.server.states.CreationState;
import it.polimi.ingsw.server.states.LoginState;
import it.polimi.ingsw.server.states.NetworkState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public class SocketReceiverServer {
    private static NetworkState networkState;
    private static MessageHandler messageHandler;
    private static LoginState loginState;
    private static CreationState creationState;
    private static int id = 100;
    private final GameStatus gameStatus;
    private final int port;
    private ServerSocket serverSocket;

    public SocketReceiverServer(int port) {
        this.port = port;
        networkState = new NetworkState(ServerPhases.READY);
        messageHandler = new MessageHandler(networkState);
        loginState = new LoginState();
        messageHandler.setLoginState(loginState);
        creationState = new CreationState();
        messageHandler.setCreationState(creationState);
        this.gameStatus = new GameStatus(GamePhases.PLANNING, false);
    }

    public void startServer() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Socket Receiver ready");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        Thread gameHandlerThread = new Thread(new GameHandler(networkState, gameStatus, messageHandler));
        gameHandlerThread.start();

        acceptConnections();

        serverSocket.close();
    }

    private void acceptConnections() throws IOException {
        SocketID socketId;
        while(true){
            Socket socket = serverSocket.accept();
            //create a new object, and link it to the id
            socketId = new SocketID(id, socket);
            int numberofclients = networkState.getNumberOfConnectedSocket()+1;
            System.out.println("Client " + id + " connected, number of clients " + numberofclients);
            Thread t2 = new Thread(new MessageReceiverServer(socketId, messageHandler, gameStatus, networkState));
            t2.start();
            SocketID finalSocketId = socketId;
            Thread clientHandler = new Thread(()->clientHandler(finalSocketId));
            clientHandler.start();
            id++;
        }

    }

    private void clientHandler(SocketID socketId){
        boolean isKicked = false;
        while (!isKicked||socketId.isConnected()){
            try {
                if(socketId.isNeedsReplacement()){
                    System.out.println(networkState.getServerPhase());
                    switch (networkState.getServerPhase()) {
                        case READY:
                            networkState.addSocket(socketId);
                            networkState.setServerPhase(ServerPhases.CREATION);
                            System.out.println("Starting Creation");
                            socketId.setNeedsReplacement(false);
                            CreationHandler creator = new CreationHandler(networkState, messageHandler, creationState, socketId);
                            creator.start();
                            break;
                        case WAITING:
                            if(networkState.reconnectPlayer(socketId)){
                                System.out.println("Re-connected player "+socketId.getPlayerInfo().getUsername());
                                messageHandler.userReconnectedReceiver(new ReconnectedEvent(this,socketId));
                                socketId.setNeedsReplacement(false);
                            }else{
                                System.out.println("Re-connection failed");
                            }
                            break;
                        case LOGIN:
                            if(networkState.getNumberOfConnectedSocket()<= networkState.getNumberOfPlayers()){
                                networkState.addSocket(socketId);
                                System.out.println("Starting Login for player " + id);
                                socketId.setNeedsReplacement(false);
                                LoginHandler login = new LoginHandler(networkState, socketId, messageHandler, loginState, creationState);
                                Thread t = new Thread(login);
                                t.start();
                                break;
                            }
                        case CREATION:
                        case GAME:
                            messageHandler.eventPerformed(new CloseConnectionEvent(this,socketId.getSocket()));
                            System.out.println("Client rejected , number of clients " + networkState.getNumberOfConnectedSocket());
                            isKicked = true;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
