package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.characters.CharactersParameters;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.view.View;

import java.util.Observable;
import java.util.Observer;

public class Controller extends Observable implements Observer {
    /*
    Controller must:
    -reset the standard evaluator at the end of every ActionPhase
    -reset postmanMovements at the end of the ActionPhase
    -do all resets with a proper reset method in GameModel
    -check if a player needs to earn a coin and give it to him, if possible, and proceeds to remove it from the coin reserve
     */

    private GameModel model;
    private View view;

    public Controller(GameModel model, View view){

        this.model = model;
        this.view = view;

    }


    @Override
    public void update(Observable o, Object arg) {
        if((o instanceof GameModel) && (arg instanceof Name)){
            setChanged();
            notifyObservers(arg);
        }

        if((o instanceof View) && (arg instanceof CharactersParameters)){
            model.setCharacterParameters((CharactersParameters) arg);
        }
    }
}
