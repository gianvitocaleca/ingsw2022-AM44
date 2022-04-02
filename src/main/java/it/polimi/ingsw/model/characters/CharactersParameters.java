package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;

import java.util.List;

public class CharactersParameters {

    private List<Creature> providedCreature;
    private int providedIslandIndex;
    private int providedMnMovements;
    private StudentContainer providedDestination;

    public List<Creature> getProvidedCreature() {
        return providedCreature;
    }

    public void setProvidedCreature(List<Creature> providedCreature) {
        this.providedCreature = providedCreature;
    }

    public int getProvidedIslandIndex() {
        return providedIslandIndex;
    }

    public void setProvidedIslandIndex(int providedIslandIndex) {
        this.providedIslandIndex = providedIslandIndex;
    }

    public int getProvidedMnMovements() {
        return providedMnMovements;
    }

    public void setProvidedMnMovements(int providedMnMovements) {
        this.providedMnMovements = providedMnMovements;
    }

    public StudentContainer getProvidedDestination() {
        return providedDestination;
    }

    public void setProvidedDestination(StudentContainer providedDestination) {
        this.providedDestination = providedDestination;
    }

}
