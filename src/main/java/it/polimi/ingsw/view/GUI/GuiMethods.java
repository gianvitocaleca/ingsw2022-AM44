package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.ClientState;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.studentcontainers.Island;
import javafx.scene.layout.HBox;

import java.util.List;

import static it.polimi.ingsw.utils.Commands.*;
import static it.polimi.ingsw.view.GUI.GuiComponents.creatureCounter;

public class GuiMethods {

    /**
     * @param islandIndex is the index of the island selected by the player
     * @return is the correct number of jumps for MN to make
     */
    public static int evaluateMnJumps(int mnPosition, int islandIndex, ClientState clientState) {
        int jumps;
        if (mnPosition < islandIndex) {
            jumps = islandIndex - mnPosition;
        } else {
            jumps = clientState.getModelPayload().getIslands().size() - mnPosition;
            jumps += islandIndex;
        }
        return jumps;
    }

    /**
     * @param c is the given creature
     * @return is the corresponding string
     */
    public static String creatureCode(Creature c) {
        String creatureLetter = "";

        switch (c) {
            case PINK_FAIRIES:
                creatureLetter = pinkCreatureText;
                break;
            case GREEN_FROGS:
                creatureLetter = greenCreatureText;
                break;
            case BLUE_UNICORNS:
                creatureLetter = blueCreatureText;
                break;
            case YELLOW_GNOMES:
                creatureLetter = yellowCreatureText;
                break;
            case RED_DRAGONS:
                creatureLetter = redCreatureText;
                break;
        }

        return creatureLetter;
    }

    /**
     * Used to verify if an island has no towers
     *
     * @param i the island
     * @return
     */
    public static boolean hasTowers(Island i) {
        return i.getNumberOfTowers() > 0;
    }

    /**
     * @param hBoxComponents  is the list of boxes to populate with content
     * @param radius
     * @param numOfComponents
     * @param i
     * @param c
     * @param num
     * @return is the next component index
     */
    public static int componentsDisposition(List<HBox> hBoxComponents, int radius, int numOfComponents, int i, Creature c, int num) {
        if (num > 0) {
            HBox creature = creatureCounter(c, num);
            if (creature != null) {
                creature.relocate(
                        getCoordinatesX(5, radius, i, numOfComponents),
                        getCoordinatesY(5, radius, i, numOfComponents));
            }
            hBoxComponents.add(creature);
            i++;
        }
        return i;
    }

    /**
     * @param island
     * @return is true if the island has one or more noEntry
     */
    public static boolean hasNoEntry(Island island) {
        if (island.getNumberOfNoEntries() > 0) {
            return true;
        }
        return false;
    }

    /**
     * @param center       the X coordinate of the center of the polygon
     * @param radius       of the polygon
     * @param index        the vertex position on the regular polygon
     * @param totalIndexes the number of total vertices in the polygon
     * @return the X coordinate of a vertex in the regular polygon
     */
    public static double getCoordinatesX(int center, int radius, int index, int totalIndexes) {
        return center + radius * Math.cos(2 * Math.PI * index / totalIndexes);
    }

    /**
     * @param center       the Y coordinate of the center of the polygon
     * @param radius       of the polygon
     * @param index        the vertex position on the regular polygon
     * @param totalIndexes the number of total vertices in the polygon
     * @return the Y coordinate of a vertex in the regular polygon
     */
    public static double getCoordinatesY(int center, int radius, int index, int totalIndexes) {
        return center + radius * Math.sin(2 * Math.PI * index / totalIndexes);
    }
}
