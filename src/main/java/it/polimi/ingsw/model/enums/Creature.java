package it.polimi.ingsw.model.enums;

public enum Creature {
    YELLOW_GNOMES("Yellow Gnomes"),
    RED_DRAGONS("Red Dragons"),
    BLUE_UNICORNS("Blue Unicorns"),
    GREEN_FROGS("Green Frogs"),
    PINK_FAIRIES("Pink Fairies");

    String name;

    Creature(String s) {
        this.name = s;
    }

    public String getName() {
        return name;
    }
}
