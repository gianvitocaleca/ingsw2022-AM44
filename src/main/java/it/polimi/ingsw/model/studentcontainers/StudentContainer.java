package it.polimi.ingsw.model.studentcontainers;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.students.Student;

import java.util.*;

public abstract class StudentContainer {
    protected List<Student> students;
    private final int capacity;

    /**
     * The constructor creates a new StudentContainer with given capacity and an empty ArrayList
     * @param capacity is the given capacity, different for every specialization of the class
     */
    public StudentContainer(int capacity){
        students = new ArrayList<>();
        this.capacity=capacity;
    }

    /**
     * @param newStudents is a given ArrayList with new students to add
     */
    public void addStudents(List<Student> newStudents){
        students.addAll(newStudents);
    }

    /**
     *
     * @param student is a single student to add to the container
     */
    public void addStudent(Student student){
        students.add(student);
    }

    /**
     *
     * @param students is the list of students to be set in the container
     */
    public void setStudents(List<Student> students){
        this.students = students;
    }

    /**
     * Used to remove a student of the given creature
     * @param creature is the type of student to remove
     * @return is the student removed
     */
    public Student removeStudent(Creature creature){
        Student temp = students.stream().filter(s -> s.getCreature()==creature).findFirst().orElse(null);
        students.remove(temp);
        return temp;
    }

    /**
     *
     * @return is the max number of students allowed in the container
     */
    public int getCapacity(){
        return capacity;
    }

    /**
     *
     * @return is the list of students in the container
     */
    public List<Student> getStudents(){

        List<Student> temp = new ArrayList<>();
        for(Student s : students){
            temp.add(new Student(s.getCreature()));
        }
        return temp;

    }

    /**
     *
     * @param c the type of student
     * @return is the number of students, of the provided type, in the container
     */
    public int getNumberOfStudentsByCreature(Creature c){
        return getStudents().stream().filter(s -> s.getCreature().equals(c)).toList().size();
    }
}
