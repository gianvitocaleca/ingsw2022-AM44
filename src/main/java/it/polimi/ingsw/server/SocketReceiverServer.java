package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.GameStatus;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.controller.events.CloseConnectionEvent;
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

public class SocketReceiverServer {

    private final int port;
    private final NetworkState networkState;
    private ServerSocket serverSocket;
    private Socket socket;
    private MessageHandler messageHandler;
    private int id = 100;

    private boolean gameStarted = false;

    public SocketReceiverServer(int port) {
        this.port = port;
        this.networkState = new NetworkState();

    }

    public void startServer() throws IOException {
        //It creates a new socket for every new connection
        //It disconnects the unnecessary sockets

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); //port not available
            return;
        }

        //the converts message objects to string and vice-versa.
        messageHandler = new MessageHandler();

        networkState.setServerPhase(ServerPhases.READY);

        System.out.println("Socket Receiver ready");

        messageHandler.setNetworkState(networkState);

        LoginState loginState = new LoginState();
        messageHandler.setLoginState(loginState);
        CreationState cs = new CreationState();
        messageHandler.setCreationState(cs);

        GameStatus gameStatus = new GameStatus(GamePhases.PLANNING, false);
        GameHandler gameHandler = new GameHandler(networkState, gameStatus, messageHandler);
        Thread gameHandlerThread = new Thread(gameHandler);
        gameHandlerThread.start();

        while (true) {
            try {
                SocketID socketId;
                do {
                    //accept the incoming connection socket
                    socket = serverSocket.accept();
                    //create a new object, and link it to the id
                    socketId = new SocketID(id, socket);
                    if (networkState.getNumberOfConnectedSocket() >= networkState.getNumberOfPlayers()) {
                        messageHandler.eventPerformed(new CloseConnectionEvent(socket));
                        System.out.println("Client rejected , number of clients " + networkState.getNumberOfConnectedSocket());
                    } else {
                        //add the new object to the status list
                        networkState.addSocket(socketId);
                        System.out.println("Client " + id + " connected, number of clients " + networkState.getNumberOfConnectedSocket());
                        Thread t2 = new Thread(new MessageReceiverServer(socketId, messageHandler, gameStatus, networkState));
                        t2.start();

                        switch (networkState.getServerPhase()) {
                            case READY:
                                networkState.setServerPhase(ServerPhases.LOGIN);
                                System.out.println("Starting Creation");
                                //class that
                                CreationHandler creator = new CreationHandler(networkState, messageHandler, cs, id);
                                creator.start();
                            case LOGIN:
                                System.out.println("Starting Login for player " + id);
                                LoginHandler login = new LoginHandler(networkState, socketId, messageHandler, loginState, cs);
                                Thread t = new Thread(login);
                                t.start();
                            case GAME:
                                System.out.println("Game started for player " + id);
                        }

                        id++;
                    }
                } while (!networkState.getServerPhase().equals(ServerPhases.GAME));

            } catch (IOException e) {
                break;
            }
        }

        serverSocket.close();
    }

}
