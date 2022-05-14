package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.enums.Value;

public class Assistant {
    private final Value name;

    public Assistant(Value input){
        name=input;
    }

    public Value getName(){
        return name;
    }

    public int getValue(){
        return name.getValue();
    }
    public int getMovements(){
        return name.getMovements();
    }

    @Override
    public String toString() {
        return ""+name;
    }
}
