package it.polimi.ingsw;

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
