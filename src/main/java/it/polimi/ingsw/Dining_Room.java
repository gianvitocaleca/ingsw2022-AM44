package it.polimi.ingsw;

import java.util.*;

public class Dining_Room extends StudentContainer {
    private Map<Creature, ArrayList<Student>> diners = createMap();

    /**
     *
     * @return this map is used to keep track of the students by creature
     */
    private Map<Creature, ArrayList<Student>> createMap(){
        Map<Creature,ArrayList<Student>> newMap = new HashMap<>();
        for(Creature c : Creature.values()){
            newMap.put(c, new ArrayList<Student>());
        }
        return newMap;
    }

    public Dining_Room(int capacity){
        super(capacity);
    }

    @Override
    public void addStudents(List<Student> newStudents){

        for(Student st : newStudents){
            diners.get(st.getCreature()).add(st);
        }
    }

    @Override
    public Student removeStudent(Creature creature) {
        return diners.get(creature).remove(diners.get(creature).size()-1);
    }
}
