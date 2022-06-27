package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.enums.Name;

public class CharactersTooltips {
    /**
     * Gets the correct tooltip text for each character
     *
     * @param name is the given character
     * @return is the tooltip string
     */
    public static String getToolTip(Name name) {
        switch (name) {
            case PRINCESS:
                return "Choose a student from this card to be placed in your Dining Room";
            case MONK:
                return "Choose a student from this card and place it on an Island of your choice";
            case JOKER:
                return "Choose up to three students from this card and exchange them with as many students in your Entrance";
            case HERALD:
                return "Choose an addition Island on which to evaluate the Influence. Mother Nature will still move to the intended Island";
            case MAGICPOSTMAN:
                return "Move Mother Nature up to two additional Islands than is indicated on the Assistant you've played";
            case MINSTREL:
                return "Exchange up to two students between your Entrance and your Dining Room";
            case KNIGHT:
                return "During the Influence evaluation of this turn you have two additional points";
            case CENTAUR:
                return "During the Influence evaluation on an Island the towers will not count";
            case FUNGARO:
                return "Choose a Creature: during this turn's Influence evaluation it will not count";
            case HERBALIST:
                return "Place a No Entry on an Island. Once Mother Nature ends on that Island the Influence evaluation is skipped and the No Entry removed";
            case THIEF:
                return "Choose a Creature: every Player loses three students of that Creature from the Dining Room";
            case FARMER:
                return "Take control of the Professors even with the same number of Students of the current owner of the Professor";
            default:
                return "Nothing to see here, move along";
        }
    }
}
