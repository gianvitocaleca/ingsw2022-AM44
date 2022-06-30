package it.polimi.ingsw.network.server.networkMessages.payloads;

/**
 * This payload is used to give client information about his username after the reconnection.
 */
public class ReconnectionPayload implements Payload {
    String username;

    /**
     * Used when the player has reconnected into the game
     * @param string is the username of the player
     */
    public ReconnectionPayload(String string) {
        this.username = string;
    }

    /**
     *
     * @return is the username of the player
     */
    public String getUsername() {
        return username;
    }
}
