package it.polimi.ingsw.controller.events;

import java.util.EventObject;

public class IntegerEvent extends EventObject {

    Integer value;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public IntegerEvent(Object source, Integer value) {
        super(source);
        this.value=value;
    }

    public Integer getValue() {
        return value;
    }
}
