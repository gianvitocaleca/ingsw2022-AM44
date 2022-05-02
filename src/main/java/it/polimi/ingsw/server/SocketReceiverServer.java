package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Listeners.CreationPhaseListener;
import it.polimi.ingsw.server.controller.Listeners.LoginPhaseListener;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketReceiverServer {

    private final int port;
    private final NetworkState networkState;
    private ServerSocket serverSocket;
    private Socket socket;
    private MessageHandler messageHandler;
    private int id = 100;

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

        LoginPhaseListener lpl = new LoginPhaseListener(loginState);


        while (true) {
            try {
                SocketID socketId;
                while(true){
                    //accept the incoming connection socket
                    socket = serverSocket.accept();
                    //create a new object, and link it to the id
                    socketId = new SocketID(id, socket);
                    if(networkState.getNumberOfConnectedSocket() >= networkState.getNumberOfPlayers()){
                        socket.close();
                        System.out.println("Client rejected , number of clients " + networkState.getNumberOfConnectedSocket());
                    }else{
                        break;
                    }
                }
                //add the new object to the status list
                networkState.addSocket(socketId);
                Thread t2 = new Thread(new MessageReceiverServer(socket, messageHandler));
                t2.start();

                switch (networkState.getServerPhase()) {
                    case READY:
                        networkState.setServerPhase(ServerPhases.LOGIN);
                        System.out.println("Starting Creation");
                        //class that
                        CreationHandler creator = new CreationHandler(networkState, messageHandler,cs,id);
                        messageHandler.addListener(new CreationPhaseListener(creator,cs));
                        creator.start();
                        messageHandler.addListener(lpl);
                    case LOGIN:

                        System.out.println("Starting Login for player " + id);
                        LoginHandler login = new LoginHandler(networkState, socketId, messageHandler, loginState,cs);
                        messageHandler.addLoginHandler(login);
                        Thread t = new Thread(login);
                        t.start();
                        break;
                    case GAME:

                    default:
                        socket.close();
                        System.out.println("Client rejected , number of clients " + networkState.getNumberOfConnectedSocket());
                        break;
                }
                id++;
            } catch (IOException e) {
                break;
            }
        }

        serverSocket.close();
    }

    public NetworkState getNetworkState() {
        return networkState;
    }

}
