package it.polimi.ingsw.model.enums;

public enum Value {
    CHEETAH("cheetah", 1, 1),
    OSTRICH("ostrich", 2, 1),
    CAT("cat", 3, 2),
    EAGLE("eagle", 4,2),
    FOX("fox", 5,3),
    LIZARD("lizard", 6,3),
    OCTOPUS("octopus", 7,4),
    DOG("dog", 8,4),
    ELEFANT("elefant", 9,5),
    TURTLE("turtle", 10,5);

    private final String assistant;
    private final int value;
    private final int movements;

    Value(String assistant, int value, int movements) {
        this.assistant=assistant;
        this.movements=movements;
        this.value=value;
    }
    public int getValue(){
        return value;
    }
    public int getMovements(){
        return movements;
    }
}
