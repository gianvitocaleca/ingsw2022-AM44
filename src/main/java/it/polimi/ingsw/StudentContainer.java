package it.polimi.ingsw;

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

    public Student removeStudent(Creature creature){
        Optional<Student> temp = students.stream().filter(s -> s.getCreature()==creature).findFirst();
        students.remove(temp);
        return temp.get();
    }

    public int getCapacity(){
        return capacity;
    }

    public Student getStudentByPosition(int position) {
        return students.get(position);
    }

    public List<Student> getStudents(){
        return students;
    }
}
