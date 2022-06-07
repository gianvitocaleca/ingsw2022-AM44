package it.polimi.ingsw.server.networkMessages.payloads;


public class LoginPayload implements Payload{

    private String string;


    public LoginPayload(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
