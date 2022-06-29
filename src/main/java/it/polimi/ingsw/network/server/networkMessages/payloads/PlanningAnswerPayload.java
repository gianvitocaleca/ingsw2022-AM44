package it.polimi.ingsw.network.server.networkMessages.payloads;


public class PlanningAnswerPayload implements Payload {
    private int indexOfAssistant;

    public PlanningAnswerPayload(int indexOfAssistant) {
        this.indexOfAssistant = indexOfAssistant;
    }

    public int getIndexOfAssistant() {
        return indexOfAssistant;
    }

}
