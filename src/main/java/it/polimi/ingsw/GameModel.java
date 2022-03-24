package it.polimi.ingsw;

import java.util.*;

public class GameModel {

    private boolean advanced_rules;
    private List<Player> players;
    private Table table;
    private int num_of_players;
    private Character[][] characters;
    private Character played_character;
    private ProfessorChecker professor_checker;
    private InfluenceCalculator influence_calculator;

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
}
