package it.polimi.ingsw.server.model.students;

import it.polimi.ingsw.server.model.enums.Creature;

public class Student {
    private final Creature creature;

    public Student(Creature creature){
        this.creature=creature;
    }

    public Creature getCreature() {
        return creature;
    }

}
