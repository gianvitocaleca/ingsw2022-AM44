package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.exceptions.GameEndedException;
import it.polimi.ingsw.server.model.students.Student;
import it.polimi.ingsw.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.Playable;

import java.util.ArrayList;
import java.util.List;

public class Herald implements Character {

    private Name name;
    private Playable model;
    private int updatedCost = 0;

    public Herald(Name name, Playable model) {
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

    @Override
    public boolean effect(CharactersParametersPayload answer) throws GameEndedException {
        if (!(model.setHeraldIsland(answer.getProvidedIslandIndex()))) {
            return false;
        }
        return true;
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getCost() {
        int cost = name.getCost();
        if(updatedCost>0) cost++;
        return cost;
    }

    @Override
    public boolean hasCoin() {
        return (updatedCost == 1);
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
