package it.polimi.ingsw.server.model.enums;

public enum Wizard {
    GANDALF("Wizards/gandalf.png"), BALJEET("Wizards/baljeet.png"), SABRINA("Wizards/sabrina.png"),KENJI("Wizards/kenji.png"),WRONG("none");

    private final String image;

    Wizard(String string) {
        this.image = string;
    }

    public String getImage() {
        return image;
    }
}
