package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.Playable;

public class Postman implements Character {

    private Name name;
    private Playable model;
    private int updatedCost=0;

    public Postman(Name name, Playable model) {
        this.name = name;
        this.model = model;

    }

    @Override
    public boolean canBePlayed(int playerCoins) {
        if(playerCoins>=getCost()){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean effect(CharactersParametersPayload answer) {
        if(answer.getProvidedMnMovements()>2){
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
