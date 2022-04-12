package it.polimi.ingsw.model.students;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;

import java.util.*;

public class StudentBucket {
    private static final int MAX_STUDENTS = 26;

    private Map<Creature, Integer> generatedStudents;

    public StudentBucket(){
        generatedStudents = createMap();
    }


    /**
     * @param creature to ask for a specific Creature
     * @return int of generated students of given creature
     */
    public int getNumberOfGeneratedStudentsByCreature(Creature creature) {
        return generatedStudents.get(creature);
    }

    /**
     * This method is used to generate students giving them a random creature
     *
     * @throws StudentsOutOfStockException when there are no more students to create
     */
    public Student generateStudent() throws StudentsOutOfStockException {
        boolean ok = false;
        Student s = null;
        Creature futureStudent;
        for (Creature c : Creature.values()) {
            if (generatedStudents.get(c) < MAX_STUDENTS) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            throw new StudentsOutOfStockException();
        }
        do {
            futureStudent = Creature.values()[new Random().nextInt(Creature.values().length)];
            if (generatedStudents.get(futureStudent) < MAX_STUDENTS) {
                s = new Student(futureStudent);
            }
        } while (s == null);
        generatedStudents.put(futureStudent, generatedStudents.get(futureStudent) + 1);
        return s;
    }


    public void resetMap() {
        for (Creature c : Creature.values()) {
            generatedStudents.put(c, 0);
        }
    }

    /**
     * This method initializes generated_students
     */
    private Map<Creature, Integer> createMap() {
        Map<Creature, Integer> newMap = new HashMap<>();
        for (Creature c : Creature.values()) {
            newMap.put(c, 0);
        }
        return newMap;
    }

    public void setMap(Map map){
        this.generatedStudents=map;
    }

    public Map getMap(){
        Map<Creature, Integer> temp = new HashMap<>();
        for(Creature c: Creature.values()){
            temp.put(c,generatedStudents.get(c));
        }
        return temp;
    }

    /**
     * Gives back a student to the student bucket
     *
     * @param creature is the type of student to be put back
     */

    public void putBackCreature(Creature creature) {
        generatedStudents.put(creature, generatedStudents.get(creature) - 1);
    }
}
