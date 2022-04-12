package it.polimi.ingsw.model;

import java.util.Observable;
import java.util.Observer;

public class ModelView extends Observable implements Observer {

    private GameModel modelCopy;


    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof GameModel){
            modelCopy = ((GameModel) o).clone();
            setChanged();
            notifyObservers();
        }
    }


}
