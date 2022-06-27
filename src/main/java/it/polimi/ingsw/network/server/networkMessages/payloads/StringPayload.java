package it.polimi.ingsw.network.server.networkMessages.payloads;

public class StringPayload implements Payload {
    private String string;

    /**
     * This message is used to send a String
     *
     * @param string is the actual message
     */

    public StringPayload(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
