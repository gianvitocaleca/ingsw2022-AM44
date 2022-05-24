package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.GameStatus;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import java.util.List;

public class GameHandler implements Runnable{

    private NetworkState networkState;
    private GameStatus gameStatus;
    private MessageHandler messageHandler;

    public GameHandler(NetworkState networkState, GameStatus gameStatus, MessageHandler messageHandler){
        this.networkState = networkState;
        this.gameStatus = gameStatus;
        this.messageHandler = messageHandler;
    }

    public void run(){
        networkState.getLoginPhaseEnded();

        List<PlayerInfo> playerInfos = networkState.getConnectedPlayerInfo();
        List<String> usernames = playerInfos.stream().map(s -> s.getUsername()).toList();
        List<Color> color = playerInfos.stream().map(s -> s.getColor()).toList();
        List<Wizard> wizards = playerInfos.stream().map(s -> s.getWizard()).toList();

        GameModel model = new GameModel(networkState.isAdvancedRules(), usernames, networkState.getNumberOfPlayers(),
                color, wizards);
        messageHandler.setNetworkState(networkState);
        Controller controller = new Controller(model, messageHandler, gameStatus, networkState);
        controller.start();
        System.out.println("Game is starting");
    }
}
