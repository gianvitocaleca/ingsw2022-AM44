package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.evaluators.*;

public class BehaviorCharacter implements Character {

    private Name name;
    private Playable model;
    private InfluenceEvaluator evaluator = new StandardEvaluator();
    private int updatedCost=0;

    public BehaviorCharacter(Name name, Playable model) {
        this.name = name;
        this.model = model;
    }

    private void setEvaluator(CharactersParameters answer){
        if(name.equals(Name.CENTAUR)){
            evaluator = new CentaurEvaluator();
        }else if(name.equals(Name.KNIGHT)){
            evaluator = new KnightEvaluator();
        }else if(name.equals(Name.FUNGARO)){
            evaluator = new FungaroEvaluator(answer.getProvidedCreature().get(0));
        }
    }

    @Override
    public void effect(CharactersParameters answer) {
        setEvaluator(answer);
        model.setInfluenceEvaluator(evaluator);
        if(name.equals(Name.FARMER)){
            model.setFarmer();
        }
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getCost() {
        return name.getCost()+updatedCost;
    }

    @Override
    public boolean hasCoin() {
        return (updatedCost==1);
    }

    @Override
    public void setUpdatedCost() {
        updatedCost=1;
    }

}
