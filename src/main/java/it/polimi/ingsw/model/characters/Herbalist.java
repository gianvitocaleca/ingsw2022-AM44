package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Playable;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.enums.Name;

import java.util.ArrayList;
import java.util.List;

public class Herbalist implements Character {
    private Name name;
    private Playable model;
    private int updatedCost = 0;
    private final int DEACTIVATORS_DEFAULT = 4;

    public Herbalist(Name name, Playable model) {
        this.name = name;
        this.model = model;
        this.model.setDeactivators(DEACTIVATORS_DEFAULT);
    }

    @Override
    public boolean canBePlayed(int playerCoins) {
        if (playerCoins >= getCost() && model.getDeactivators() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean effect(CharactersParametersPayload answer) {
        model.setDeactivators(model.getDeactivators() - 1);
        model.addNoEntry(answer.getProvidedIslandIndex());

        return true;
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getCost() {
        int cost = name.getCost();
        if (updatedCost > 0) cost++;
        return cost;
    }

    @Override
    public void setUpdatedCost() {
        updatedCost++;
    }

    @Override
    public void unsetUpdatedCost() {
        updatedCost--;
    }

    @Override
    public List<Student> getStudents() {
        return new ArrayList<>();
    }
}
