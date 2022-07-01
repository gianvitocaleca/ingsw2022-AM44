package it.polimi.ingsw.controller.events;

import it.polimi.ingsw.network.server.SocketID;

import java.util.EventObject;

/**
 * This event contains the socketID of the reconnected player. It is used to
 * send him a message about his username.
 */
public class ReconnectedEvent extends EventObject {

    private SocketID socketID;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ReconnectedEvent(Object source, SocketID socketID) {
        super(source);
        this.socketID = socketID;
    }

    public SocketID getSocketID() {
        return socketID;
    }

    public String getUsername() {
        return socketID.getPlayerInfo().getUsername();
    }
}
