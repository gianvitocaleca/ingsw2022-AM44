package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

class CharacterCreatorTest {
    public Name name;
    public Character character;

    @BeforeEach
    public void Initialize() {
        name = Name.values()[new Random().nextInt(Name.values().length)];
    }

    @Test
    void createCharacter(Name name) {

    }
}