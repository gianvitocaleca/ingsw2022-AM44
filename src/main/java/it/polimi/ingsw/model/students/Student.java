package it.polimi.ingsw.model.students;

import it.polimi.ingsw.model.enums.Creature;

public class Student {
    private final Creature creature;

    /**
     * Represents the student pawn.
     * @param creature is the type of student
     */
    public Student(Creature creature){
        this.creature=creature;
    }

    /**
     *
     * @return is the student type
     */
    public Creature getCreature() {
        return creature;
    }

    /**
     * Used for test purposes.
     * @return is the creature type
     */
    @Override
    public String toString() {
        return ""+creature;
    }
}
