package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.SocketID;

import java.util.EventObject;

public class ReconnectedEvent extends EventObject {

    private SocketID socketID;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ReconnectedEvent(Object source,SocketID socketID) {
        super(source);
        this.socketID = socketID;
    }

    public SocketID getSocketID() {
        return socketID;
    }
}
