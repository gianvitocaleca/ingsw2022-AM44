package it.polimi.ingsw;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StudentBucket {
    private final int max_students = 26;
    private Map<Creature,Integer> generated_students = createMap();

    /**
     *
     * this method initializes generated_students
     */
    private Map<Creature,Integer> createMap(){
        Map<Creature,Integer> newMap = new HashMap<>();
        for( Creature c : Creature.values()){
            newMap.put(c , 0);
        }
        return newMap;
    }
    public int getNumberOfCreature(Creature creature){
        return generated_students.get(creature);
    }
    /**
     *
     * This method is used to create students with a random creature.
     * @throws StudentsOutOfStockException when there are no more students to create
     */
    public Student generateStudent() throws StudentsOutOfStockException{
        boolean ok = false;
        Student s = null;
        Creature futureStudent;
        for(Creature c : Creature.values()){
            if(generated_students.get(c)<max_students){
                ok = true;
                break;
            }
        }
        if(!ok){
            throw new StudentsOutOfStockException();
        }
        do {
            futureStudent = Creature.values()[new Random().nextInt(Creature.values().length)];
            if (generated_students.get(futureStudent) < max_students) {
                s = new Student(futureStudent);
            }
        }while(s == null);
        generated_students.put(futureStudent, generated_students.get(futureStudent)+1);
        return s;
    }
}
