package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.enums.Assistants;

public class Assistant {
    private final Assistants name;

    public Assistant(Assistants input){
        name=input;
    }

    public Assistants getName(){
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
