package it.polimi.ingsw;

public class MoverCharacter implements Character {

    private Name name;

    public MoverCharacter(Name name){
        this.name = name;
    }
    @Override
    public void effect() {

    }

    @Override
    public Name getName() {
        return this.name;
    }
}
