package it.polimi.ingsw.network.server.networkMessages.payloads;


public class PlanningAnswerPayload implements Payload {
    private int indexOfAssistant;

    /**
     * Used to provide the assistant selection info
     * @param indexOfAssistant is the assistant selected
     */
    public PlanningAnswerPayload(int indexOfAssistant) {
        this.indexOfAssistant = indexOfAssistant;
    }

    /**
     *
     * @return is the number of the played assistant
     */
    public int getIndexOfAssistant() {
        return indexOfAssistant;
    }

}
