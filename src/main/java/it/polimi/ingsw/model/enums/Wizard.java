package it.polimi.ingsw.model.enums;

public enum Wizard {
    GANDALF("Wizards/gandalf2.jpg"), BALJEET("Wizards/baljeet2.jpg"), SABRINA("Wizards/sabrina2.jpg"), KENJI("Wizards/kenji2.jpg"), WRONG("none");

    private final String image;

    Wizard(String string) {
        this.image = string;
    }

    public String getImage() {
        return image;
    }
}
