package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;

import java.util.ArrayList;
import java.util.List;

public class CharacterInformation {
    private Name name;
    private boolean updatedCost;
    private int deactivators;
    private int index;
    private List<Creature> moverContent = new ArrayList<>();

    public CharacterInformation(Name name, boolean updatedCost, int deactivators, int index, List<Creature> moverContent) {
        this.name = name;
        this.updatedCost = updatedCost;
        this.deactivators = deactivators;
        this.index = index;
        this.moverContent = moverContent;
    }

    public Name getName() {
        return name;
    }

    public boolean isUpdatedCost() {
        return updatedCost;
    }

    public int getDeactivators() {
        return deactivators;
    }

    public int getIndex() {
        return index;
    }

    public List<Creature> getMoverContent() {
        return moverContent;
    }
}
