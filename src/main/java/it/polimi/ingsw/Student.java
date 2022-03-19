package it.polimi.ingsw;

import java.util.*;

public class Student {
    private Creature creature;
    private Map<Creature, Integer> generated_students;

    public Student() {
        this.generated_students = new HashMap<Creature, Integer>();
    }

    public Student generateStudent() {
        return new Student();
    }
}
