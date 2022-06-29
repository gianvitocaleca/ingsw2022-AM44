package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Playable;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.enums.Name;

import java.util.ArrayList;
import java.util.List;

public class Postman implements Character {

    private Name name;
    private Playable model;
    private int updatedCost = 0;

    public Postman(Name name, Playable model) {
        this.name = name;
        this.model = model;

    }

    @Override
    public boolean canBePlayed(int playerCoins) {
        if (playerCoins >= getCost()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean effect(CharactersParametersPayload answer) {
        if (answer.getProvidedMnMovements() > 2) {
            return false;
        }
        model.setPostmanMovements(answer.getProvidedMnMovements());
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
