package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.server.model.exceptions.GameEndedException;
import it.polimi.ingsw.server.model.students.Student;
import it.polimi.ingsw.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.server.model.enums.Name;

import java.util.List;

public interface Character {

    boolean canBePlayed(int playerCoins);

    boolean effect(CharactersParametersPayload answer) throws GameEndedException, UnplayableEffectException;

    Name getName();

    int getCost();

    boolean hasCoin();

    void setUpdatedCost();

    void unsetUpdatedCost();

    List<Student> getStudents();
}
