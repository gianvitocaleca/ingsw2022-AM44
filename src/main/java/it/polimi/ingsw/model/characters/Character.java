package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.enums.Name;

import java.util.List;

public interface Character {
    /**
     * @param playerCoins the provided coins
     * @return whether the character can be played with the provided coins
     */
    boolean canBePlayed(int playerCoins);

    /**
     * Represents the effect of the character card
     *
     * @param answer is the provided parameters selection
     * @return whether the operation was successful
     * @throws GameEndedException        is thrown if an end game condition is met
     * @throws UnplayableEffectException is thrown if the parameters are not compatible with the effect
     */
    boolean effect(CharactersParametersPayload answer) throws GameEndedException, UnplayableEffectException;

    /**
     * @return is the character's name
     */
    Name getName();

    /**
     * @return is the current cost of the character
     */
    int getCost();

    /**
     * Whether the cost has been updated
     */
    void setUpdatedCost();

    /**
     * Reset the cost
     */
    void unsetUpdatedCost();

    /**
     * @return is the list of students on the character
     */
    List<Student> getStudents();
}
