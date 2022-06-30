package it.polimi.ingsw.network.server.networkMessages.payloads;


public class LoginPayload implements Payload {

    private String string;

    /**
     * Used to store the username of the player
     * @param string is the player's name
     */
    public LoginPayload(String string) {
        this.string = string;
    }

    /**
     *
     * @return is the player's name
     */
    public String getString() {
        return string;
    }
}
