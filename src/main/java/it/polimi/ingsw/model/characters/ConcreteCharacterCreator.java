package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Playable;
import it.polimi.ingsw.model.enums.Name;

public class ConcreteCharacterCreator implements CharacterCreator {

    public ConcreteCharacterCreator() {

    }

    /**
     * Factory for characters
     *
     * @param name is the name of the character to create
     * @return the created character
     */

    @Override
    public Character createCharacter(Name name, Playable model) {
        if(name.equals(Name.HERALD)) return new Herald(name,model);
        else if(name.equals(Name.MAGICPOSTMAN)) return new Postman(name,model);
        else if(name.equals(Name.HERBALIST)) return new Herbalist(name,model);
        else if(name.equals(Name.THIEF)) return new Thief(name, model);
        else if(name.equals(Name.KNIGHT)||name.equals(Name.CENTAUR)||name.equals(Name.FUNGARO)) return new InfluenceCharacter(name,model);
        else if(name.equals(Name.FARMER)) return new Farmer(name,model);
        else if(name.equals(Name.JOKER)||name.equals(Name.MINSTREL)||name.equals(Name.MONK)||name.equals(Name.PRINCESS)) return new MoverCharacter(name,model);
        else return null;
    }
}
