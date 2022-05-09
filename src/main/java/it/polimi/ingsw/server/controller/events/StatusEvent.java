package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.SocketID;
import it.polimi.ingsw.server.networkMessages.Headers;

import java.net.Socket;
import java.util.EventObject;

public class StatusEvent extends EventObject {

    private Headers header;
    private Socket socket;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public StatusEvent(Object source, Headers header, Socket socket) {
        super(source);
        this.header = header;
        this.socket = socket;
    }

    public Headers getHeader() {
        return header;
    }
}
