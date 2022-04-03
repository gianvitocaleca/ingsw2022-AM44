package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.model.students.Student;

import java.util.List;

public class CharactersParameters {

    private List<Creature> providedCreature;
    private int providedIslandIndex;
    private int providedMnMovements;
    private StudentContainer providedDestination;

    public CharactersParameters(List<Creature> providedCreature, int providedIslandIndex, int providedMnMovements, StudentContainer providedDestination){
        this.providedCreature=providedCreature;
        this.providedIslandIndex=providedIslandIndex;
        this.providedMnMovements=providedMnMovements;
        this.providedDestination=providedDestination;
    }

    public List<Creature> getProvidedCreature() {
        return providedCreature;
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
}
