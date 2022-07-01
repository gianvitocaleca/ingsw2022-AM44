package it.polimi.ingsw.controller.events;

import it.polimi.ingsw.network.server.SocketID;

import java.util.EventObject;

/**
 * This event contains the socketID of the disconnected player, notified by the server ping handler
 */
public class DisconnectionEvent extends EventObject {

    private SocketID socketID;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public DisconnectionEvent(Object source, SocketID socketID) {
        super(source);
        this.socketID = socketID;
    }

    public SocketID getSocketID() {
        return socketID;
    }
}
