package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.enums.Value;

public class Assistant {
    private final Value name;

    public Assistant(Value input){
        name=input;
    }

    public int getValue(){
        return name.getValue();
    }
    public int getMovements(){
        return name.getMovements();
    }
}
