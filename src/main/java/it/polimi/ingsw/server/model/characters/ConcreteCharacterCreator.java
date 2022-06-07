package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.Playable;
import it.polimi.ingsw.server.model.enums.Name;

public class ConcreteCharacterCreator implements CharacterCreator {

    private final int JOKER_CAPACITY = 6;
    private final int PRINCESS_MONK_CAPACITY = 4;
    private final int MINSTREL_CAPACITY = 0;

    /**
     * Factory for characters
     *
     * @param name is the name of the character to create
     * @return the created character
     */

    @Override
    public Character createCharacter(Name name, Playable model) {
        if (name != null) {
            if (name.equals(Name.HERALD)) return new Herald(name, model);
            else if (name.equals(Name.MAGICPOSTMAN)) return new Postman(name, model);
            else if (name.equals(Name.HERBALIST)) return new Herbalist(name, model);
            else if (name.equals(Name.THIEF)) return new Thief(name, model);
            else if (name.equals(Name.KNIGHT) || name.equals(Name.CENTAUR) || name.equals(Name.FUNGARO) ||
                    name.equals(Name.FARMER)) return new BehaviorCharacter(name, model);

            else if (name.equals(Name.JOKER)) return new MoverCharacter(name, model,JOKER_CAPACITY);
            else if (name.equals(Name.MINSTREL)) return new MoverCharacter(name, model,MINSTREL_CAPACITY);
            else if (name.equals(Name.MONK)||name.equals(Name.PRINCESS)) return new MoverCharacter(name, model,PRINCESS_MONK_CAPACITY);
        }
        return null;
    }
}
