package it.polimi.ingsw.model;

import it.polimi.ingsw.model.evaluators.InfluenceEvaluator;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.students.StudentBucket;

import java.util.List;

public interface Playable {
    void addNoEntry(int indexOfIsland);

    void evaluateInfluence() throws GameEndedException;

    List<Player> getPlayers();

    int getCurrentPlayerIndex();

    void setPlayers(List<Player> players);

    Table getTable();

    void setTable(Table table);

    void setLastRound(boolean value);

    void coinGiver();

    void checkProfessor();

    void setPostmanMovements(int numberOfSteps);

    boolean moveStudents(StudentContainer source, StudentContainer destination, List<Creature> creature);

    void setInfluenceEvaluator(InfluenceEvaluator evaluator);

    void setFarmer();

    boolean setHeraldIsland(int indexIsland) throws GameEndedException;

    boolean checkEndGame();

    int getDeactivators();

    boolean setDeactivators(int deactivators);

    StudentBucket getBucket();

    void setBucket(StudentBucket bucket);

}
