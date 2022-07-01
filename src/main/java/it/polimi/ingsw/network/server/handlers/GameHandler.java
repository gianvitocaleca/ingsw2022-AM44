package it.polimi.ingsw.network.server.handlers;

import it.polimi.ingsw.model.exceptions.PausedException;
import it.polimi.ingsw.network.server.states.NetworkState;
import it.polimi.ingsw.network.server.PlayerInfo;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.GameStatus;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Wizard;

import java.util.List;

public class GameHandler implements Runnable {

    private NetworkState networkState;
    private GameStatus gameStatus;
    private MessageHandler messageHandler;

    /**
     * It handles the actual game mechanics
     * @param networkState is the current network state
     * @param gameStatus is the current game state
     * @param messageHandler handles the messages
     */
    public GameHandler(NetworkState networkState, GameStatus gameStatus, MessageHandler messageHandler) {
        this.networkState = networkState;
        this.gameStatus = gameStatus;
        this.messageHandler = messageHandler;
    }

    /**
     * Starts the game controller with the necessary info
     */
    public void run() {
        networkState.getLoginPhaseEnded();

        List<PlayerInfo> playerInfos = networkState.getConnectedPlayerInfo();
        List<String> usernames = playerInfos.stream().map(PlayerInfo::getUsername).toList();
        List<Color> color = playerInfos.stream().map(PlayerInfo::getColor).toList();
        List<Wizard> wizards = playerInfos.stream().map(PlayerInfo::getWizard).toList();

        GameModel model = new GameModel(networkState.isAdvancedRules(), usernames, networkState.getNumberOfPlayers(),
                color, wizards);
        messageHandler.setNetworkState(networkState);
        Controller controller = new Controller(model, messageHandler, gameStatus, networkState);
        try {
            controller.startController();
            System.out.println("Game is starting");
        } catch (PausedException e) {
            System.out.println("Waiting for other players");
        }

    }
}
