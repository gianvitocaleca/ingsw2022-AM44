package it.polimi.ingsw.server.networkMessages;

import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.studentcontainers.StudentContainer;

import java.util.List;

public class CharactersParametersPayload implements Payload {

    private List<Creature> providedSourceCreatures;
    private int providedIslandIndex;
    private int providedMnMovements;
    private StudentContainer providedDestination;
    private List<Creature> providedDestinationCreatures;

    public CharactersParametersPayload(List<Creature> providedSourceCreatures, int providedIslandIndex, int providedMnMovements, StudentContainer providedDestination, List<Creature> providedDestinationCreatures) {
        this.providedSourceCreatures = providedSourceCreatures;
        this.providedIslandIndex = providedIslandIndex;
        this.providedMnMovements = providedMnMovements;
        this.providedDestination = providedDestination;
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

    public StudentContainer getProvidedDestination() {
        return providedDestination;
    }

    public List<Creature> getProvidedDestinationCreatures() {
        return providedDestinationCreatures;
    }
}
