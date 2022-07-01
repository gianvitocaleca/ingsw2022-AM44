package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.network.server.CharacterInformation;
import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Professor;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.network.server.networkMessages.payloads.ShowModelPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.utils.TextAssets.*;

public class CliPrinter {
    private final String space = " ";
    private String horizontal = "═";
    private String vertical = "║";
    private String topLeft = "╔";
    private String topRight = "╗";
    private String topMiddle = "╦";
    private String middleLeft = "╠";
    private String middleRight = "╣";
    private String middleMiddle = "╬";
    private String bottomLeft = "╚";
    private String bottomRight = "╝";
    private String bottomMiddle = "╩";
    private final String leftIndex = "[";
    private final String rightIndex = "]";
    private String tower = "t";
    private String motherNature = "m";
    private String noEntry = "n";
    private String cloud = "c";
    private int realCreatureStringLength = 16;
    private int islandStringLength = 18;
    private final int cloudStringLength = 5;
    private final int characterStringLength = 3;
    private final int costStringLength = 8;
    private int islandStringPadding = 2;
    private int noEntryStringLength = 5;
    private int tempStringPadding;
    private StringBuilder top;
    private StringBuilder titles;
    private StringBuilder middle;
    private StringBuilder secondMiddle;
    private StringBuilder contents;
    private StringBuilder bottom;

    /**
     * Changes the characters and colors based on the OS
     */
    public CliPrinter() {
        if (!OS.isWindows()) {
            horizontal = CliColors.FG_BORDER.getCode() + horizontal + CliColors.RST.getCode();
            vertical = CliColors.FG_BORDER.getCode() + vertical + CliColors.RST.getCode();
            topLeft = CliColors.FG_BORDER.getCode() + topLeft + CliColors.RST.getCode();
            topRight = CliColors.FG_BORDER.getCode() + topRight + CliColors.RST.getCode();
            topMiddle = CliColors.FG_BORDER.getCode() + topMiddle + CliColors.RST.getCode();
            middleLeft = CliColors.FG_BORDER.getCode() + middleLeft + CliColors.RST.getCode();
            middleRight = CliColors.FG_BORDER.getCode() + middleRight + CliColors.RST.getCode();
            middleMiddle = CliColors.FG_BORDER.getCode() + middleMiddle + CliColors.RST.getCode();
            bottomLeft = CliColors.FG_BORDER.getCode() + bottomLeft + CliColors.RST.getCode();
            bottomRight = CliColors.FG_BORDER.getCode() + bottomRight + CliColors.RST.getCode();
            bottomMiddle = CliColors.FG_BORDER.getCode() + bottomMiddle + CliColors.RST.getCode();
            tower = "♜";
            motherNature = CliColors.FG_MN.getCode() + "♟" + CliColors.RST.getCode();
            noEntry = CliColors.FG_RED.getCode() + "⊘" + CliColors.RST.getCode();
            cloud = CliColors.FG_CYAN.getCode() + "☁" + CliColors.RST.getCode();
            realCreatureStringLength = 11;
            islandStringLength = 16;
            islandStringPadding = 5;
        }
        tempStringPadding = islandStringPadding;
    }

    /**
     * Prints a box with the title and content for every player
     *
     * @param modelPayload is used to determine the contents
     */
    public void printPlayers(ShowModelPayload modelPayload) {
        for (Player p : modelPayload.getPlayersList()) {
            top = new StringBuilder();
            titles = new StringBuilder();
            middle = new StringBuilder();
            contents = new StringBuilder();
            bottom = new StringBuilder();
            createPlayerSection(usernameHeaderText, p.getUsername(), Section.FIRST);
            createPlayerSection(wizardHeaderText, p.getWizard().toString(), Section.MIDDLE);
            createPlayerSection(coinHeaderText, String.valueOf(p.getMyCoins()), Section.MIDDLE);
            createPlayerSection(entranceHeaderText, createCreatureString(p.getEntrance()), Section.MIDDLE);
            createPlayerSection(diningRoomHeaderText, createCreatureString(p.getDiningRoom()), Section.MIDDLE);
            createPlayerSection(professorsHeaderText, createProfessorString(p.getProfessors()), Section.MIDDLE);
            if (p.getLastPlayedCards().size() > 0) {
                createPlayerSection(assistantHeaderText, p.getLastPlayedCard().toString(), Section.MIDDLE);
            } else {
                createPlayerSection(assistantHeaderText, noAssistant, Section.MIDDLE);
            }
            createPlayerSection(towersHeaderText, String.valueOf(p.getTowers()), Section.LAST);
            System.out.println(top);
            System.out.println(titles);
            System.out.println(middle);
            System.out.println(contents);
            System.out.println(bottom);

        }
    }

    /**
     * Creates a section for the player
     *
     * @param givenTitle   is the section's title
     * @param givenContent is the section's content
     * @param section      determines the section position
     */
    private void createPlayerSection(String givenTitle, String givenContent, Section section) {
        boolean longerTitle = givenTitle.length() > givenContent.length();

        if (section.equals(Section.FIRST)) {
            top.append(topLeft);
            titles.append(vertical);
            middle.append(middleLeft);
            contents.append(vertical);
            bottom.append(bottomLeft);
        }

        titles.append(space);

        contents.append(space);

        if (givenTitle.equals(entranceHeaderText) || givenTitle.equals(diningRoomHeaderText) || givenTitle.equals(professorsHeaderText)) {
            for (int i = 0; i < Math.max(givenTitle.length(), realCreatureStringLength) + 2; i++) {
                top.append(horizontal);
                middle.append(horizontal);
                bottom.append(horizontal);
            }

            titles.append(givenTitle);
            contents.append(givenContent);

            if (longerTitle) {
                contents.append(space.repeat(Math.max(0, givenTitle.length() - realCreatureStringLength)));
            } else {
                titles.append(space.repeat(Math.max(0, realCreatureStringLength - givenTitle.length())));
            }

        } else {
            for (int i = 0; i < Math.max(givenTitle.length(), givenContent.length()) + 2; i++) {
                top.append(horizontal);
                middle.append(horizontal);
                bottom.append(horizontal);
            }

            titles.append(givenTitle);
            contents.append(givenContent);

            if (longerTitle) {
                contents.append(space.repeat(Math.max(0, givenTitle.length() - givenContent.length())));
            } else {
                titles.append(space.repeat(Math.max(0, givenContent.length() - givenTitle.length())));
            }
        }

        switch (section) {
            case FIRST:
            case MIDDLE:
                top.append(topMiddle);
                middle.append(middleMiddle);
                bottom.append(bottomMiddle);
                break;
            default:
                top.append(topRight);
                middle.append(middleRight);
                bottom.append(bottomRight);
                break;
        }

        titles.append(space);
        titles.append(vertical);

        contents.append(space);
        contents.append(vertical);

    }

    /**
     * Creates a string that contains the number of creatures
     *
     * @param container is where the creatures are
     * @return is the string ready to be printed
     */
    private String createCreatureString(StudentContainer container) {
        tempStringPadding = islandStringPadding;
        StringBuilder creatureString = new StringBuilder();
        Map<Creature, Integer> counter = new HashMap<>();
        for (Creature c : Creature.values()) {
            counter.put(c, 0);
        }
        List<Creature> creatures = container.getStudents().stream().map(Student::getCreature).toList();
        for (Creature c : creatures) {
            counter.put(c, counter.get(c) + 1);
        }
        for (Creature c : Creature.values()) {
            if (counter.get(c) > 9) {
                if (tempStringPadding != 0) {
                    tempStringPadding--;
                }
            }
        }
        if (OS.isWindows()) {
            creatureStringFormatterWin(creatureString, counter);
        } else {
            creatureStringFormatter(creatureString, counter);
        }

        return creatureString.toString();
    }

    /**
     * Creates a string that contains the number of creatures
     *
     * @param creatures is the list of creatures
     * @return is the string ready to be printed
     */
    private String createCreatureString(List<Creature> creatures) {
        StringBuilder creatureString = new StringBuilder();
        Map<Creature, Integer> counter = new HashMap<>();
        for (Creature c : Creature.values()) {
            counter.put(c, 0);
        }
        for (Creature c : creatures) {
            counter.put(c, counter.get(c) + 1);
        }

        if (OS.isWindows()) {
            creatureStringFormatterWin(creatureString, counter);
        } else {
            creatureStringFormatter(creatureString, counter);
        }

        return creatureString.toString();
    }

    /**
     * Creates a string that contains the professors of the player
     *
     * @param professors is the list of professors
     * @return is the string ready to be printed
     */
    private String createProfessorString(List<Professor> professors) {
        StringBuilder professorString = new StringBuilder();
        Map<Creature, Integer> counter = new HashMap<>();
        for (Creature c : Creature.values()) {
            counter.put(c, 0);
        }
        List<Creature> creatures = professors.stream().map(Professor::getCreature).toList();
        for (Creature c : creatures) {
            counter.put(c, counter.get(c) + 1);
        }

        if (OS.isWindows()) {
            creatureStringFormatterWin(professorString, counter);
        } else {
            creatureStringFormatter(professorString, counter);
        }


        return professorString.toString();
    }

    /**
     * Formats the string with colors
     *
     * @param stringBuilder where the string is being built
     * @param counter       the map that contains the creatures counter
     */
    private void creatureStringFormatter(StringBuilder stringBuilder, Map<Creature, Integer> counter) {
        stringBuilder.append(space);
        stringBuilder.append(CliColors.FG_RED.getCode());
        stringBuilder.append(counter.get(Creature.RED_DRAGONS));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
        stringBuilder.append(CliColors.FG_YELLOW.getCode());
        stringBuilder.append(counter.get(Creature.YELLOW_GNOMES));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
        stringBuilder.append(CliColors.FG_BLUE.getCode());
        stringBuilder.append(counter.get(Creature.BLUE_UNICORNS));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
        stringBuilder.append(CliColors.FG_GREEN.getCode());
        stringBuilder.append(counter.get(Creature.GREEN_FROGS));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
        stringBuilder.append(CliColors.FG_PINK.getCode());
        stringBuilder.append(counter.get(Creature.PINK_FAIRIES));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
    }

    /**
     * Formats the string with letters
     *
     * @param stringBuilder where the string is being built
     * @param counter       the map that contains the creatures counter
     */
    private void creatureStringFormatterWin(StringBuilder stringBuilder, Map<Creature, Integer> counter) {
        stringBuilder.append(space);
        stringBuilder.append(creatureToCli(Creature.RED_DRAGONS));
        stringBuilder.append(counter.get(Creature.RED_DRAGONS));
        stringBuilder.append(space);
        stringBuilder.append(creatureToCli(Creature.YELLOW_GNOMES));
        stringBuilder.append(counter.get(Creature.YELLOW_GNOMES));
        stringBuilder.append(space);
        stringBuilder.append(creatureToCli(Creature.BLUE_UNICORNS));
        stringBuilder.append(counter.get(Creature.BLUE_UNICORNS));
        stringBuilder.append(space);
        stringBuilder.append(creatureToCli(Creature.GREEN_FROGS));
        stringBuilder.append(counter.get(Creature.GREEN_FROGS));
        stringBuilder.append(space);
        stringBuilder.append(creatureToCli(Creature.PINK_FAIRIES));
        stringBuilder.append(counter.get(Creature.PINK_FAIRIES));
        stringBuilder.append(space);
    }

    /**
     * Matches the creature with a letter
     */
    private String creatureToCli(Creature creature) {
        switch (creature) {
            case RED_DRAGONS:
                return "R";
            case YELLOW_GNOMES:
                return "Y";
            case BLUE_UNICORNS:
                return "B";
            case GREEN_FROGS:
                return "G";
            case PINK_FAIRIES:
                return "P";
            default:
                return "";
        }
    }

    /**
     * Prints all the islands inside their own box
     *
     * @param modelPayload is used to determine the contents
     */
    public void printIslands(ShowModelPayload modelPayload) {
        top = new StringBuilder();
        middle = new StringBuilder();
        secondMiddle = new StringBuilder();
        bottom = new StringBuilder();

        int j = 0;
        int motherNaturePosition = modelPayload.getMotherNature();
        for (Island i : modelPayload.getIslands()) {
            createIsland(j + 1, i.getNumberOfTowers(), i.getColorOfTowers(), i.getNumberOfNoEntries(), (motherNaturePosition == j), createCreatureString(i));
            if ((j + 1) == (modelPayload.getIslands().size() / 2) + (modelPayload.getIslands().size() % 2)
                    || (j + 1) == modelPayload.getIslands().size()) {
                System.out.println(top);
                System.out.println(middle);
                System.out.println(secondMiddle);
                System.out.println(bottom);
                top = new StringBuilder();
                middle = new StringBuilder();
                secondMiddle = new StringBuilder();
                bottom = new StringBuilder();
            }
            j++;
        }

    }

    /**
     * Creates the box of the island with its contents
     */
    private void createIsland(int index, int towers, Color color, int noEntry, boolean hasMotherNature, String creatures) {
        top.append(topLeft);
        middle.append(vertical);
        middle.append(space);
        secondMiddle.append(vertical);
        if (OS.isWindows() && tempStringPadding > 0) {
            secondMiddle.append(space);
        }
        bottom.append(bottomLeft);

        for (int i = 0; i < islandStringLength; i++) {
            top.append(horizontal);
            bottom.append(horizontal);
        }

        middle.append(leftIndex);
        middle.append(index);
        if (index < 10) {
            middle.append(space);
        }
        middle.append(rightIndex);
        middle.append(space);
        if (towers > 0) {
            if (OS.isWindows()) {
                middle.append(colorToCli(color));
            } else {
                switch (color) {
                    case WHITE:
                        middle.append(CliColors.FG_WHITE.getCode());
                        break;
                    case BLACK:
                        middle.append(CliColors.FG_BLACK.getCode());
                        break;
                    case GREY:
                        middle.append(CliColors.FG_GRAY.getCode());
                        break;
                    default:
                        middle.append(CliColors.FG_RED.getCode());
                        break;
                }
                middle.append(tower);
                middle.append(CliColors.RST.getCode());
            }
        } else {
            middle.append(space);
        }

        middle.append(space);
        if (towers > 0) {
            middle.append(towers);
        } else {
            middle.append(space);
        }

        middle.append(space);
        if (hasMotherNature) {
            middle.append(motherNature);
        } else {
            middle.append(space);
        }
        middle.append(space);
        if (noEntry > 0) {
            middle.append(this.noEntry);
            middle.append(space);
            middle.append(noEntry);
        } else {
            middle.append(space);
            middle.append(space);
            middle.append(space);

        }
        if (OS.isWindows()) {
            middle.append(space);
            middle.append(space);
        }
        middle.append(space);
        middle.append(vertical);

        secondMiddle.append(creatures);
        if (!OS.isWindows()) {
            for (int i = 0; i < tempStringPadding; i++) {
                secondMiddle.append(space);
            }
        }
        if (OS.isWindows() && tempStringPadding > 1) {
            secondMiddle.append(space);
        }
        secondMiddle.append(vertical);

        top.append(topRight);
        bottom.append(bottomRight);
    }

    /**
     * Matches the color with a letter
     */
    private String colorToCli(Color color) {
        switch (color) {
            case WHITE:
                return "W";
            case BLACK:
                return "B";
            case GREY:
                return "G";
            default:
                return "";
        }
    }

    /**
     * Prints all the clouds inside their own box
     *
     * @param modelPayload is used to determine the contents
     */
    public void printClouds(ShowModelPayload modelPayload) {
        top = new StringBuilder();
        middle = new StringBuilder();
        bottom = new StringBuilder();

        int i = 0;
        for (Cloud c : modelPayload.getClouds()) {
            createCloud(i, createCreatureString(c));
            i++;
        }
        System.out.println(top);
        System.out.println(middle);
        System.out.println(bottom);
    }

    /**
     * Creates the box of the cloud with its contents
     */
    private void createCloud(int index, String creatures) {
        smallComponent(topLeft, bottomLeft, cloudStringLength, cloud, index, false);
        /*
        top.append(topLeft);
        middle.append(vertical);
        bottom.append(bottomLeft);

        for (int i = 0; i < cloudStringLength; i++) {
            top.append(horizontal);
            bottom.append(horizontal);
        }

        middle.append(space);
        middle.append(cloud);
        middle.append(space);
        middle.append(index);
        middle.append(space);
        */
        top.append(topMiddle);
        middle.append(vertical);
        bottom.append(bottomMiddle);

        for (int i = 0; i < realCreatureStringLength + 2; i++) {
            top.append(horizontal);
            bottom.append(horizontal);
        }

        middle.append(space);
        middle.append(creatures);
        middle.append(space);

        top.append(topRight);
        middle.append(vertical);
        bottom.append(bottomRight);
    }

    /**
     * Prints all the characters and their information
     *
     * @param modelPayload is used to determine the contents
     */
    public void printTable(ShowModelPayload modelPayload) {
        top = new StringBuilder();
        middle = new StringBuilder();
        bottom = new StringBuilder();

        for (CharacterInformation ci : modelPayload.getCharacters()) {
            createCharacter(ci.getIndex(), ci.getName(), ci.getCost(), modelPayload);
        }

        System.out.println(top);
        System.out.println(middle);
        System.out.println(bottom);
    }

    /**
     * Creates the box of the character with its contents
     */
    private void createCharacter(int index, Name name, int cost, ShowModelPayload modelPayload) {
        smallComponent(topLeft, bottomLeft, characterStringLength, "", index, true);
        /*
        top.append(topLeft);
        middle.append(vertical);
        bottom.append(bottomLeft);

        for (int i = 0; i < characterStringLength; i++) {
            top.append(horizontal);
            bottom.append(horizontal);
        }

        middle.append(space);
        middle.append(index);
        middle.append(space);
        */
        for (int i = 0; i < name.toString().length() + 1; i++) {
            top.append(horizontal);
            bottom.append(horizontal);
        }
        middle.append(name);
        middle.append(space);
        smallComponent(topMiddle, bottomMiddle, costStringLength, costContentText, cost, false);
        /*
        top.append(topMiddle);
        middle.append(vertical);
        bottom.append(bottomMiddle);

        for (int i = 0; i < costStringLength; i++) {
            top.append(horizontal);
            bottom.append(horizontal);
        }

        middle.append(space);
        middle.append(costContentText);
        middle.append(space);
        middle.append(cost);
        middle.append(space);
        */
        if (name.equals(Name.MONK) || name.equals(Name.PRINCESS) || name.equals(Name.JOKER)) {
            top.append(topMiddle);
            middle.append(vertical);
            bottom.append(bottomMiddle);

            for (int i = 0; i < realCreatureStringLength; i++) {
                top.append(horizontal);
                bottom.append(horizontal);
            }

            switch (name) {
                case JOKER:
                    middle.append(createCreatureString(modelPayload.getJokerCreatures()));
                    break;
                case MONK:
                    middle.append(createCreatureString(modelPayload.getMonkCreatures()));
                    break;
                case PRINCESS:
                    middle.append(createCreatureString(modelPayload.getPrincessCreatures()));
                    break;
                default:
                    break;
            }

            top.append(topRight);
            middle.append(vertical);
            bottom.append(bottomRight);

        } else if (name.equals(Name.HERBALIST)) {
            smallComponent(topMiddle, bottomMiddle, noEntryStringLength, noEntry, modelPayload.getDeactivators(), false);
            /*
            top.append(topMiddle);
            middle.append(vertical);
            bottom.append(bottomMiddle);

            for (int i = 0; i < noEntryStringLength; i++) {
                top.append(horizontal);
                bottom.append(horizontal);
            }

            middle.append(space);
            middle.append(noEntry);
            middle.append(space);
            middle.append(modelPayload.getDeactivators());
            middle.append(space);
            */
            top.append(topRight);
            middle.append(vertical);
            bottom.append(bottomRight);
        } else {
            top.append(topRight);
            middle.append(vertical);
            bottom.append(bottomRight);
        }

    }

    /**
     * Used for formatting cell components
     * @param topString is the string to be added on top
     * @param bottomString is the string to be added on bottom
     * @param length is the length of the component
     * @param image is the visual information string
     * @param value is the value to be shown
     * @param isShort whether the component is the short one
     */
    private void smallComponent(String topString, String bottomString, int length, String image, int value, boolean isShort){
        top.append(topString);
        middle.append(vertical);
        bottom.append(bottomString);

        for (int i = 0; i < length; i++) {
            top.append(horizontal);
            bottom.append(horizontal);
        }
        if(isShort){
            middle.append(space);
            middle.append(value);
            middle.append(space);
        }else{
            middle.append(space);
            middle.append(image);
            middle.append(space);
            middle.append(value);
            middle.append(space);
        }

    }
}
