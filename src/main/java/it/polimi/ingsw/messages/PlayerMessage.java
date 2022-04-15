package it.polimi.ingsw.messages;

public class PlayerMessage {

    private Headers header;
    private String username;

    public PlayerMessage(Headers header, String username) {
        this.header = header;
        this.username = username;
    }

    public Headers getHeader() {
        return header;
    }

    public String getUsername() {
        return username;
    }
}
