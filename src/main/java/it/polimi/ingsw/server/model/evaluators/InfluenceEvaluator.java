package it.polimi.ingsw.server.model.evaluators;

import it.polimi.ingsw.server.model.exceptions.GameEndedException;
import it.polimi.ingsw.server.model.GameModel;

public interface InfluenceEvaluator {
    void evaluateInfluence(GameModel model) throws GameEndedException;
}
