package it.polimi.ingsw.network.server.networkMessages.payloads;

import it.polimi.ingsw.model.enums.Creature;

import java.util.List;

public class CharactersParametersPayload implements Payload {

    private List<Creature> providedSourceCreatures;
    private int providedIslandIndex;
    private int providedMnMovements;
    private List<Creature> providedDestinationCreatures;

    /**
     * Contains the info necessary to play the characters
     * @param providedSourceCreatures is the list of selected creatures from the first student container
     * @param providedIslandIndex is the index of the selected island
     * @param providedMnMovements is the number of provided steps for mother nature
     * @param providedDestinationCreatures is the list of selected creatures from the second student container
     */
    public CharactersParametersPayload(List<Creature> providedSourceCreatures, int providedIslandIndex, int providedMnMovements, List<Creature> providedDestinationCreatures) {
        this.providedSourceCreatures = providedSourceCreatures;
        this.providedIslandIndex = providedIslandIndex;
        this.providedMnMovements = providedMnMovements;
        this.providedDestinationCreatures = providedDestinationCreatures;
    }

    /**
     *
     * @return is a list of creatures
     */
    public List<Creature> getProvidedSourceCreatures() {
        return providedSourceCreatures;
    }

    /**
     *
     * @return is the index of the selected island
     */
    public int getProvidedIslandIndex() {
        return providedIslandIndex;
    }

    /**
     *
     * @return is the number of provided steps for mother nature
     */
    public int getProvidedMnMovements() {
        return providedMnMovements;
    }

    /**
     *
     * @return is a list of creatures
     */
    public List<Creature> getProvidedDestinationCreatures() {
        return providedDestinationCreatures;
    }
}
