package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.evaluators.InfluenceEvaluator;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;

import java.util.List;

public interface Playable {
    void addNoEntry(int indexOfIsland);
    void evaluateInfluence();
    void setPostmanMovements( int numberOfSteps);
    void thiefEffect(Creature creature);
    void moveStudents(StudentContainer source, StudentContainer destination, List<Creature> creature);
    void setInfluenceEvaluator(InfluenceEvaluator evaluator);
    void setFarmer();
    void setHeraldIsland(int indexIsland);
}
