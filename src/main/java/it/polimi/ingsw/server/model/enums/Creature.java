package it.polimi.ingsw.server.model.enums;

public enum Creature {
    YELLOW_GNOMES("Table/yellow.png"),
    RED_DRAGONS("Table/red.png"),
    BLUE_UNICORNS("Table/blue.png"),
    GREEN_FROGS("Table/green.png"),
    PINK_FAIRIES("Table/pink.png");

    private final String image;

    Creature(String s) {
        this.image = s;
    }

    public String getImage() {
        return image;
    }

}
