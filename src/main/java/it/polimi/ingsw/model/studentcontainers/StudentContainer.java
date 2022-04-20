package it.polimi.ingsw.model.studentcontainers;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;

import java.util.*;

public abstract class StudentContainer {
    private final List<Student> students;
    private final int capacity;

    /**
     * The constructor creates a new StudentContainer with given capacity and an empty ArrayList<Student>
     * @param capacity is the given capacity, different for every specialization of the class
     */
    public StudentContainer(int capacity){
        students = new ArrayList<>();
        this.capacity=capacity;
    }

    /**
     * @param newStudents is a given ArrayList<Student> with new students to add
     */
    public void addStudents(List<Student> newStudents){
        students.addAll(newStudents);
    }

    public void addStudent(Student student){
        students.add(student);
    }

    public Student removeStudent(Creature creature){
        Student temp = students.stream().filter(s -> s.getCreature()==creature).findFirst().orElse(null);
        students.remove(temp);
        return temp;
    }


    public int getCapacity(){
        return capacity;
    }

    public List<Student> getStudents(){

        List<Student> temp = new ArrayList<>();
        for(Student s : students){
            temp.add(new Student(s.getCreature()));
        }
        return temp;

    }
}
