package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.GameStatus;
import it.polimi.ingsw.server.controller.Listeners.CreationPhaseListener;
import it.polimi.ingsw.server.controller.Listeners.LoginPhaseListener;
import it.polimi.ingsw.server.controller.enums.GamePhases;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
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
        GameStatus gameStatus = new GameStatus(GamePhases.PLANNING,false);


        while (true) {
            try {
                SocketID socketId;
                do{
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
                }while(!networkState.getServerPhase().equals(ServerPhases.GAME));
                //add the new object to the status list
                networkState.addSocket(socketId);
                Thread t2 = new Thread(new MessageReceiverServer(socket, messageHandler,gameStatus,networkState));
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
                }

                id++;

            } catch (IOException e) {
                break;
            }
        }
        List<PlayerInfo> playerInfos = networkState.getConnectedPlayerInfo();
        List<String> usernames = playerInfos.stream().map(s->s.getUsername()).toList();
        List<Color> color = playerInfos.stream().map(s->s.getColor()).toList();
        List<Wizard> wizards = playerInfos.stream().map(s->s.getWizard()).toList();
        GameModel model = new GameModel(networkState.isAdvancedRules(), usernames,networkState.getNumberOfPlayers(),
                color,wizards);
        Controller controller = new Controller(model,messageHandler,gameStatus);
        controller.start();
        System.out.println("Game is starting");
        while(!networkState.getServerPhase().equals(ServerPhases.GAME_ENDED)){
            switch (networkState.getServerPhase()){
                case GAME:
                    System.out.println("Game running");
                    break;
                case WAITING:
                    List<SocketID> disconnectedSocketIDList = networkState.getDisconnectedSocketIDs();
                    while (disconnectedSocketIDList.size()>0){
                        socket = serverSocket.accept();
                        disconnectedSocketIDList.get(0).setSocket(socket);
                        disconnectedSocketIDList.remove(0);
                    }
                    networkState.setServerPhase(ServerPhases.GAME);
                    controller.start();
                    break;
            }
        }

        serverSocket.close();
    }

    public NetworkState getNetworkState() {
        return networkState;
    }

}
