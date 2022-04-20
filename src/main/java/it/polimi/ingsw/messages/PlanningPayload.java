package it.polimi.ingsw.messages;


public class PlanningPayload implements Payload {

    private String senderUsername;
    private int indexOfAssistant;

    public PlanningPayload(String senderUsername, int indexOfAssistant) {
        this.senderUsername = senderUsername;
        this.indexOfAssistant = indexOfAssistant;
    }

    public int getIndexOfAssistant() {
        return indexOfAssistant;
    }

    public String getSenderUsername() {
        return senderUsername;
    }
}
