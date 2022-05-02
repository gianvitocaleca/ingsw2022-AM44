package it.polimi.ingsw.server.networkMessages;


public class PlanningAnswerPayload implements Payload {

    private String senderUsername;
    private int indexOfAssistant;

    public PlanningAnswerPayload(String senderUsername, int indexOfAssistant) {
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
