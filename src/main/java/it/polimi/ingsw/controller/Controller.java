package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.messages.Headers;
import it.polimi.ingsw.messages.PhaseMessage;
import it.polimi.ingsw.messages.PlanningMessage;
import it.polimi.ingsw.messages.PlayerMessage;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.model.exceptions.PlanningPhaseEndedException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.view.ViewProxy;

import java.util.Observable;
import java.util.Observer;

import static sun.security.krb5.KrbException.errorMessage;

public class Controller extends Observable implements Observer {
    /*
    Controller must:
    -reset the standard evaluator at the end of every ActionPhase
    -reset postmanMovements at the end of the ActionPhase
    -do all resets with a proper reset method in GameModel
    -check if a player needs to earn a coin and give it to him, if possible, and proceeds to remove it from the coin reserve
     */

    private GameModel model;
    private ViewProxy viewProxy;

    private GamePhases currentPhase;


    public Controller(GameModel model, ViewProxy viewProxy) {

        this.model = model;
        this.viewProxy = viewProxy;
        this.currentPhase = GamePhases.LOGIN;

    }

    public void run() {

        if (currentPhase.equals(GamePhases.LOGIN)) {
            //

            currentPhase = GamePhases.PLANNING;
            setChanged();
            notifyObservers(new PhaseMessage(Headers.PLANNING));
        }

        while (true) {
            if (currentPhase.equals(GamePhases.PLANNING)) {
                model.fillClouds();
                if (waitAssistants()) {
                    currentPhase = GamePhases.ACTION_STUDENTSMOVEMENT;
                    setChanged();
                    notifyObservers(new PhaseMessage(Headers.PLANNING));
                }
            }

        }
    }


    private boolean waitAssistants() {

        //genera un messaggio CurrentPlayer e lo invia a tutti
        sendCurrentPlayerMessage();
        //controller rimane in ascolto e si aspetta un messaggio playassistant dal currentplayer (update)


        return true;
    }

    /**
     * This method is called ad the end of the action phase to check if the game has ended in case of StudentOutOfStockException and to notify the clients about
     * the winner of the game
     */
    private void checkIfLastRound() {
        if (model.checkIfLastRound()) {
            Player winner = model.findWinner();
            setChanged();
            notifyObservers(new PlayerMessage(Headers.winnerPlayer, winner.getUsername()));
        }
    }

    private void sendCurrentPlayerMessage() {
        Player curr = model.getPlayers().get(model.getCurrentPlayerIndex());
        setChanged();
        notifyObservers(new PlayerMessage(Headers.currentPlayer, curr.getUsername()));
    }

    private void playAssistant(int indexOfAssistant) {
        try {
            if (!(model.playAssistant(indexOfAssistant))) {
                errorMessage(1);
            } else {
                sendCurrentPlayerMessage();
            }
        } catch (AssistantAlreadyPlayedException a) {
            errorMessage(2);
        } catch (PlanningPhaseEndedException p) {
            model.establishRoundOrder();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if ((o instanceof GameModel) && (arg instanceof Name)) {
            setChanged();
            notifyObservers(arg);
        }
        if ((o instanceof ViewProxy) && (arg instanceof PlanningMessage)) {
            playAssistant(((PlanningMessage) arg).getIndexOfAssistant());
        }

        if ((o instanceof ViewProxy) && (arg instanceof CharactersParameters)) {
            if (!(model.effect((CharactersParameters) arg))) {
                setChanged();
                notifyObservers(model.getCharacters().get(model.getPlayedCharacter()).getName());
            }
        }
    }
}
