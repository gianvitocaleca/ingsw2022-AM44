package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.enums.Name;

import java.util.List;

public interface Character {

    boolean canBePlayed(int playerCoins);

    boolean effect(CharactersParametersPayload answer) throws GameEndedException, UnplayableEffectException;

    Name getName();

    int getCost();

    void setUpdatedCost();

    void unsetUpdatedCost();

    List<Student> getStudents();
}
