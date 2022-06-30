package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.enums.Assistants;

public class Assistant {
    private final Assistants name;

    /**
     * Is the assistant card.
     * @param input is the name of the assistant
     */
    public Assistant(Assistants input){
        name=input;
    }

    /**
     *
     * @return is the name of the assistant
     */
    public Assistants getName(){
        return name;
    }

    /**
     *
     * @return is the value of the assistant
     */
    public int getValue(){
        return name.getValue();
    }

    /**
     *
     * @return is the number of steps allowed by the assistant
     */
    public int getMovements(){
        return name.getMovements();
    }

    /**
     * Used for test purposes.
     * @return
     */
    @Override
    public String toString() {
        return ""+name;
    }
}
