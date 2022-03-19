package it.polimi.ingsw;

import java.util.*;

public class StudentContainer {
    /**
     * Array of students for all the subclasses
     */
    protected List<Student> students;

    public StudentContainer() {
        this.students = new ArrayList<Student>();
    }

    public void addStudents(List<Student> new_students) {
        this.students.addAll(new_students);
    }

    public Student removeStudent(Creature creature) {
        return new Student();
    }

    public List<Student> getStudents() {
        return students;
    }
}
