package it.polimi.ingsw.model.studentcontainers;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.students.Student;

import java.util.*;

public class DiningRoom extends StudentContainer {
    private Map<Creature, ArrayList<Student>> diners = createMap();
    private boolean[][] givenCoins = {
            {false, false, false},
            {false, false, false},
            {false, false, false},
            {false, false, false},
            {false, false, false}};

    public DiningRoom(int capacity) {
        super(capacity);
    }

    /**
     * @return this map is used to keep track of the students by creature
     */
    private Map<Creature, ArrayList<Student>> createMap() {
        Map<Creature, ArrayList<Student>> newMap = new HashMap<>();
        for (Creature c : Creature.values()) {
            newMap.put(c, new ArrayList<Student>());
        }
        return newMap;
    }

    @Override
    public void addStudents(List<Student> newStudents) {

        for (Student st : newStudents) {
            diners.get(st.getCreature()).add(st);
        }
    }

    @Override
    public Student removeStudent(Creature creature) {
        return diners.get(creature).remove(diners.get(creature).size() - 1);
    }

    public int getNumberOfStudentsByCreature(Creature creature) {
        return diners.get(creature).size();
    }
}
