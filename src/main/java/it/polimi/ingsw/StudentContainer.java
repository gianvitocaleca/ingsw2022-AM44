package it.polimi.ingsw;

import java.util.*;

public class StudentContainer {
    protected List<Student> students;
    protected final int capacity;

    public StudentContainer(int capacity){
        students = new ArrayList<Student>();
        this.capacity=capacity;
    }

    public int getCapacity(){ return capacity;}

    public void addStudents(List<Student> newStudents){

        for (Student s : newStudents) {
            students.add(s);
        }

    }

    public Student removeStudent(Creature creature){
        Optional<Student> temp = students.stream().filter(s -> s.getCreature()==creature).findFirst();
        students.remove(temp);
        return temp.get();
    }

    public Student getStudent(int i) {
        return students.get(i);
    }

    public List<Student> getStudents(){
        return students;
    }
}
