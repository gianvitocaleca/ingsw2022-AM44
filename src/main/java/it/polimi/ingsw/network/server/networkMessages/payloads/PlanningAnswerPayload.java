package it.polimi.ingsw.network.server.networkMessages.payloads;


public class PlanningAnswerPayload implements Payload {

    private String senderUsername;
    private int indexOfAssistant;

    /**
     * Used to provide the assistant selection info
     * @param senderUsername is the player's name
     * @param indexOfAssistant is the assistant selected
     */
    public PlanningAnswerPayload(String senderUsername, int indexOfAssistant) {
        this.senderUsername = senderUsername;
        this.indexOfAssistant = indexOfAssistant;
    }

    /**
     *
     * @return is the number of the played assistant
     */
    public int getIndexOfAssistant() {
        return indexOfAssistant;
    }

    public String getSenderUsername() {
        return senderUsername;
    }
}
