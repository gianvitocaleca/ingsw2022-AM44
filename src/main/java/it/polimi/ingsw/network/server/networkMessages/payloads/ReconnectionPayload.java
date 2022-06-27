package it.polimi.ingsw.network.server.networkMessages.payloads;

/**
 * This payload is used to give client information about his username after the reconnection.
 */
public class ReconnectionPayload implements Payload {
    String username;

    public ReconnectionPayload(String string) {
        this.username = string;
    }

    public String getUsername() {
        return username;
    }
}
