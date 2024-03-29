package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameStatus;
import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.controller.events.CloseConnectionEvent;
import it.polimi.ingsw.controller.events.ReconnectedEvent;

import static it.polimi.ingsw.network.server.enums.ServerPhases.*;

import it.polimi.ingsw.network.server.handlers.CreationHandler;
import it.polimi.ingsw.network.server.handlers.GameHandler;
import it.polimi.ingsw.network.server.handlers.LoginHandler;
import it.polimi.ingsw.network.server.handlers.MessageHandler;
import it.polimi.ingsw.network.server.receiver.MessageReceiverServer;
import it.polimi.ingsw.network.server.states.CreationState;
import it.polimi.ingsw.network.server.states.LoginState;
import it.polimi.ingsw.network.server.states.NetworkState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketReceiverServer {
    private static NetworkState networkState;
    private static MessageHandler messageHandler;
    private static LoginState loginState;
    private static CreationState creationState;
    private int id = 100;
    private final GameStatus gameStatus;
    private final int port;
    private ServerSocket serverSocket;
    private Thread gameHandlerThread;

    /**
     * Used to handle all the new socket connections
     * @param port is the port for the server to bind to
     */
    public SocketReceiverServer(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Socket Receiver ready");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        messageHandler = new MessageHandler(networkState);
        loginState = new LoginState();
        networkState = new NetworkState(READY,serverSocket,loginState);
        messageHandler.setLoginState(loginState);
        creationState = new CreationState();
        messageHandler.setCreationState(creationState);
        this.gameStatus = new GameStatus(GamePhases.PLANNING, false);
    }

    /**
     * Creates the game thread and starts to listen for connections
     */
    public void startServer(){

        gameHandlerThread = new Thread(new GameHandler(networkState, gameStatus, messageHandler));
        gameHandlerThread.start();

        acceptConnections();
    }

    /**
     * Used to accept connection, it needs to always accept the connection.
     */
    private void acceptConnections(){
        SocketID socketId;
        while (true) {
            try{
                Socket socket = serverSocket.accept();
                //create a new object, and link it to the id
                socketId = new SocketID(id, socket);
                int numberOfClients = networkState.getNumberOfConnectedSocket() + 1;
                System.out.println("Client " + id + " connected, number of clients " + numberOfClients);
                Thread t2 = new Thread(new MessageReceiverServer(socketId, messageHandler, gameStatus, networkState));
                t2.start();
                SocketID finalSocketId = socketId;
                Thread clientHandler = new Thread(() -> clientHandler(finalSocketId));
                clientHandler.start();
                id++;
            }catch(IOException e){
                System.out.println("No one is connected, server is closing");
                break;
            }
        }
        System.exit(0);
    }

    /**
     * Used to assign the client to the correct handler
     * @param socketId is the newly connected socketID
     */
    private void clientHandler(SocketID socketId) {
        boolean isKicked = false;
        while (!isKicked || socketId.isConnected()) {
            try {
                if (socketId.isNeedsReplacement()) {
                    switch (networkState.getServerPhase()) {
                        case READY:
                            networkState.addSocket(socketId);
                            networkState.setServerPhase(CREATION);
                            System.out.println("Starting Creation");
                            socketId.setNeedsReplacement(false);
                            CreationHandler creator = new CreationHandler(networkState, messageHandler, creationState, socketId);
                            creator.start();
                            break;
                        case WAITING:
                            if (networkState.reconnectPlayer(socketId)) {
                                System.out.println("Re-connected player " + socketId.getPlayerInfo().getUsername());
                                messageHandler.userReconnectedReceiver(new ReconnectedEvent(this, socketId));
                                socketId.setNeedsReplacement(false);
                            } else {
                                System.out.println("Re-connection failed");
                            }
                            break;
                        case LOGIN:
                            if (networkState.getNumberOfConnectedSocket() <= networkState.getNumberOfPlayers()) {
                                networkState.addSocket(socketId);
                                System.out.println("Starting Login for player " + id);
                                socketId.setNeedsReplacement(false);
                                LoginHandler login = new LoginHandler(networkState, socketId, messageHandler, loginState);
                                Thread t = new Thread(login);
                                t.start();
                                break;
                            }
                        case CREATION:
                        case GAME:
                            messageHandler.eventPerformed(new CloseConnectionEvent(this, socketId.getSocket()));
                            System.out.println("Client rejected , number of clients " + networkState.getNumberOfConnectedSocket());
                            socketId.setConnected(false);
                            isKicked = true;
                            socketId.setConnected(false);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
