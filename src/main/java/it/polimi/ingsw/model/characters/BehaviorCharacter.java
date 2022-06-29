package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Playable;
import it.polimi.ingsw.model.evaluators.*;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.enums.Name;

import java.util.ArrayList;
import java.util.List;

public class BehaviorCharacter implements Character {

    private Name name;
    private Playable model;
    private InfluenceEvaluator evaluator = new StandardEvaluator();
    private int updatedCost = 0;

    public BehaviorCharacter(Name name, Playable model) {
        this.name = name;
        this.model = model;
    }

    private void setEvaluator(CharactersParametersPayload answer) {
        if (name.equals(Name.CENTAUR)) {
            evaluator = new CentaurEvaluator();
        } else if (name.equals(Name.KNIGHT)) {
            evaluator = new KnightEvaluator();
        } else if (name.equals(Name.FUNGARO)) {
            evaluator = new FungaroEvaluator(answer.getProvidedSourceCreatures().get(0));
        }
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
    public boolean effect(CharactersParametersPayload answer) {
        setEvaluator(answer);
        model.setInfluenceEvaluator(evaluator);
        if (name.equals(Name.FARMER)) {
            model.setFarmer();
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
