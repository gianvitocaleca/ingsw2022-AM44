package it.polimi.ingsw.messages;

import java.io.Serializable;

public class Message implements Serializable {
    private Headers header;
    private Payload payload;

    public Message(Headers header, Payload payload) {
        this.header = header;
        this.payload = payload;
    }

    public Headers getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }
}
