package it.polimi.ingsw;

public enum Value {
    CHEETAH(1, 1),
    OSTRICH(2, 1),
    CAT(3, 2),
    EAGLE(4, 2),
    FOX(5, 3),
    LIZARD(6, 3),
    OCTOPUS(7, 4),
    DOG(8, 4),
    ELEFANT(9, 5),
    TURTLE(10, 5);

    public final int value;
    public final int mother_nature_movement;

    private Value(int value, int mother_nature_movement) {
        this.value = value;
        this.mother_nature_movement = mother_nature_movement;
    }
}
