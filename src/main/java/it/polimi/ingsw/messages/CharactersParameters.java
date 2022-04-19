package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;

import java.util.List;

public class CharactersParameters implements Payload {

    private List<Creature> providedSourceCreatures;
    private int providedIslandIndex;
    private int providedMnMovements;
    private StudentContainer providedDestination;
    private List<Creature> providedDestinationCreatures;

    public CharactersParameters(List<Creature> providedSourceCreatures, int providedIslandIndex, int providedMnMovements, StudentContainer providedDestination, List<Creature> providedDestinationCreatures) {
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
