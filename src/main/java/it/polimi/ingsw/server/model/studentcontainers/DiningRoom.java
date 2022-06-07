package it.polimi.ingsw.server.model.studentcontainers;

import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.students.Student;

import java.util.*;

public class DiningRoom extends StudentContainer {
    private Map<Creature, ArrayList<Student>> diners;
    private static final int capacity = 9;

    public DiningRoom() {
        super(DiningRoom.capacity);
        this.diners = createMap();
    }

    /**
     * @return this map is used to keep track of the students by creature
     */
    private Map<Creature, ArrayList<Student>> createMap() {
        Map<Creature, ArrayList<Student>> newMap = new HashMap<>();
        for (Creature c : Creature.values()) {
            newMap.put(c, new ArrayList<>());
        }
        return newMap;
    }

    @Override
    public void addStudents(List<Student> newStudents) {
        super.addStudents(newStudents);
        for (Student st : newStudents) {
            diners.get(st.getCreature()).add(st);
        }
    }

    @Override
    public void addStudent(Student student) {
        super.addStudent(student);
        diners.get(student.getCreature()).add(student);
    }

    @Override
    public Student removeStudent(Creature creature) {
        super.removeStudent(creature);
        return diners.get(creature).remove(diners.get(creature).size() - 1);
    }

    @Override
    public int getNumberOfStudentsByCreature(Creature creature) {
        return diners.get(creature).size();
    }

    @Override
    public void setStudents(List<Student> students) {
        super.setStudents(students);
        Map<Creature, ArrayList<Student>> newMap = createMap();
        for(Student s: students){
            newMap.get(s.getCreature()).add(s);
        }
        this.diners = newMap;
    }
}
