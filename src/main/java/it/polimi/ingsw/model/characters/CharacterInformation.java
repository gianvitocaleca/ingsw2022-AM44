package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;

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
}
