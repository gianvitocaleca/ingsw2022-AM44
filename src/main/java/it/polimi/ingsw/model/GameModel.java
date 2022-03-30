package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;

import java.util.*;

public class GameModel implements Playable {

    private boolean advanced_rules;
    private List<Player> players;
    private Table table;
    private int num_of_players;
    private Character[][] characters;
    private Character played_character;

    public GameModel(boolean advanced_rules, List<String> name_of_players, int num_of_players) {
        this.advanced_rules = advanced_rules;
        this.num_of_players = num_of_players;
    }

    public void fillClouds() {

    }

    public void playAssistant() {

    }

    public void establishRoundOrder() {

    }

    public void moveStudents(StudentContainer source, StudentContainer destination, List<Creature> creatures) {

    }

    public boolean moveMotherNature(int jumps) {
        return true;
    }

    public boolean checkEndGame() {
        return false;
    }

    public Player findWinner() {
        return null;
    }

    public void checkTower() {

    }

    public void checkNeighbourIslands() {

    }

    public void modifyCostOfCharacter(Character character) {

    }

    @Override
    public void addNoEntry(int indexOfIsland) {
        
    }

    @Override
    public void evaluateInfluence() {

    }

    @Override
    public void setPostmanMovements(int numberOfSteps) {

    }

    @Override
    public void thiefEffect(Creature creature) {

    }

    @Override
    public void moveStudents(StudentContainer source, StudentContainer destination, Creature creature) {

    }

    @Override
    public void setInfluenceCharacter(int typeOfInfluenceCharacter) {

    }
}
