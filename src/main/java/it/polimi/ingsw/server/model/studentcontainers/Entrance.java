package it.polimi.ingsw.server.model.studentcontainers;

import it.polimi.ingsw.server.model.enums.Creature;

public class Entrance extends StudentContainer {

    public Entrance(int capacity){
        super(capacity);
    }

    public int getNumberOfStudentsByCreature(Creature c){
        return getStudents().stream().filter(s -> s.getCreature().equals(c)).toList().size();
    }

}
