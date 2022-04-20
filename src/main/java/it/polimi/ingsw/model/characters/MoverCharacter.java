package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.messages.CharactersParameters;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.Playable;

public class MoverCharacter implements Character {

    private Name name;
    private Playable model;
    private int updatedCost = 0;

    public MoverCharacter(Name name, Playable model) {
        this.name = name;
        this.model = model;
    }

    @Override
    public boolean canBePlayed(int playerCoins) {
        if (playerCoins >= getCost()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean effect(CharactersParameters answer) {

        switch (name) {
            case JOKER:

                if (!(model.jokerEffect(answer.getProvidedSourceCreatures(), answer.getProvidedDestinationCreatures()))) {
                    return false;
                }
                return true;

            case PRINCESS:

                //check if provided creature is present in princess' attribute students

                if (!(model.princessEffect(answer.getProvidedSourceCreatures()))) {
                    return false;
                }
                return true;
                
            case MONK:

                if (!(model.monkEffect(answer.getProvidedSourceCreatures(), answer.getProvidedIslandIndex()))) {
                    return false;
                }
                return true;

            case THIEF:
                model.thiefEffect(answer.getProvidedSourceCreatures().get(0));
                return true;

            case MINSTREL:
                if (!(model.minstrelEffect(answer.getProvidedSourceCreatures(), answer.getProvidedDestinationCreatures()))) {
                    return false;
                }
                return true;
        }
        return true;
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getCost() {
        return name.getCost() + updatedCost;
    }

    @Override
    public boolean hasCoin() {
        return (updatedCost == 1);
    }

    @Override
    public void setUpdatedCost() {
        updatedCost = 1;
    }

}
