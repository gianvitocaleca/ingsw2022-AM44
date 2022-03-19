package it.polimi.ingsw;

import java.util.*;

public class DiningRoom extends StudentContainer {
    private Map<Creature, ArrayList<Student>> diners;

    public DiningRoom() {
        super();
        this.diners = new HashMap<Creature, ArrayList<Student>>();
    }

    public int getNumberOfCreature(Creature creature) {
        return 0;
    }
}
