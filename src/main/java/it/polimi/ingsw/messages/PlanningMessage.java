package it.polimi.ingsw.messages;

public class PlanningMessage {

    private Headers header;

    private String senderUsername;
    private int indexOfAssistant;

    public PlanningMessage(Headers header, String senderUsername, int indexOfAssistant) {
        this.header = header;
        this.senderUsername = senderUsername;
        this.indexOfAssistant = indexOfAssistant;
    }

    public Headers getHeader() {
        return header;
    }

    public int getIndexOfAssistant() {
        return indexOfAssistant;
    }

    public String getSenderUsername() {
        return senderUsername;
    }
}
