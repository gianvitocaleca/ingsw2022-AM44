package it.polimi.ingsw.model.students;

import it.polimi.ingsw.model.enums.Creature;

public class Student {
    private final Creature creature;

    public Student(Creature creature){
        this.creature=creature;
    }

    public Creature getCreature() {
        return creature;
    }

    @Override
    public String toString() {
        return ""+creature;
    }
}
