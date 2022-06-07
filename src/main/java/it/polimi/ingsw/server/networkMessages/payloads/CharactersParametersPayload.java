package it.polimi.ingsw.server.networkMessages.payloads;

import it.polimi.ingsw.server.model.enums.Creature;

import java.util.List;

public class CharactersParametersPayload implements Payload {

    private List<Creature> providedSourceCreatures;
    private int providedIslandIndex;
    private int providedMnMovements;
    private List<Creature> providedDestinationCreatures;

    public CharactersParametersPayload(List<Creature> providedSourceCreatures, int providedIslandIndex, int providedMnMovements, List<Creature> providedDestinationCreatures) {
        this.providedSourceCreatures = providedSourceCreatures;
        this.providedIslandIndex = providedIslandIndex;
        this.providedMnMovements = providedMnMovements;
        this.providedDestinationCreatures = providedDestinationCreatures;
    }

    public List<Creature> getProvidedSourceCreatures() {
        return providedSourceCreatures;
    }

    public int getProvidedIslandIndex() {
        return providedIslandIndex;
    }

    public int getProvidedMnMovements() {
        return providedMnMovements;
    }

    public List<Creature> getProvidedDestinationCreatures() {
        return providedDestinationCreatures;
    }
}
