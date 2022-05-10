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

    public GameHandler(NetworkState networkState, GameStatus gameStatus){
        this.networkState = networkState;
        this.gameStatus = gameStatus;
    }

    public void run(){
        networkState.getLoginPhaseEnded();

        List<PlayerInfo> playerInfos = networkState.getConnectedPlayerInfo();
        List<String> usernames = playerInfos.stream().map(s -> s.getUsername()).toList();
        List<Color> color = playerInfos.stream().map(s -> s.getColor()).toList();
        List<Wizard> wizards = playerInfos.stream().map(s -> s.getWizard()).toList();

        GameModel model = new GameModel(networkState.isAdvancedRules(), usernames, networkState.getNumberOfPlayers(),
                color, wizards);
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.setNetworkState(networkState);
        Controller controller = new Controller(model, messageHandler, gameStatus);
        controller.start();
        System.out.println("Game is starting");
    }
}
