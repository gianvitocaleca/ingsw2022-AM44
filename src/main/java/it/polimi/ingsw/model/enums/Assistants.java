package it.polimi.ingsw.model.enums;

public enum Assistants {
    CHEETAH("Assistants/cheetah.png", 1, 1),
    OSTRICH("Assistants/ostrich.png", 2, 1),
    CAT("Assistants/cat.png", 3, 2),
    EAGLE("Assistants/eagle.png", 4, 2),
    FOX("Assistants/fox.png", 5, 3),
    LIZARD("Assistants/lizard.png", 6, 3),
    OCTOPUS("Assistants/octopus.png", 7, 4),
    DOG("Assistants/dog.png", 8, 4),
    ELEPHANT("Assistants/elephant.png", 9, 5),
    TURTLE("Assistants/turtle.png", 10, 5);

    private final String assistant;
    private final int value;
    private final int movements;

    Assistants(String assistant, int value, int movements) {
        this.assistant = assistant;
        this.movements = movements;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int getMovements() {
        return movements;
    }

    public String getAssistant() {
        return assistant;
    }

}
