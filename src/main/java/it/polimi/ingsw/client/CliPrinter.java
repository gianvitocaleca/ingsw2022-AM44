package it.polimi.ingsw.client;

import it.polimi.ingsw.server.CharacterInformation;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.enums.Name;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.Professor;
import it.polimi.ingsw.server.model.studentcontainers.Cloud;
import it.polimi.ingsw.server.model.studentcontainers.Island;
import it.polimi.ingsw.server.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.server.model.students.Student;
import it.polimi.ingsw.server.networkMessages.payloads.ShowModelPayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final String cost = "Cost";
    private final String username = "Username";
    private final String wizard = "Wizard";
    private final String coins = "Coins";
    private final String entrance = "Entrance";
    private final String diningRoom = "Dining Room";
    private final String professors = "Professors";
    private final String lastAssistant = "Last Assistant";
    private final String towers = "Towers";
    private final String noAssistant = "none";
    private int realCreatureStringLength = 16;
    private int islandStringLength = 18;
    private final int cloudStringLength = 5;
    private final int characterStringLength = 3;
    private final int costStringLength = 8;
    private StringBuilder top;
    private StringBuilder titles;
    private StringBuilder middle;
    private StringBuilder secondMiddle;
    private StringBuilder contents;
    private StringBuilder bottom;

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
            noEntry = CliColors.FG_RED.getCode() + "⃠" + CliColors.RST.getCode();
            cloud = CliColors.FG_CYAN.getCode() + "☁" + CliColors.RST.getCode();
            realCreatureStringLength = 11;
            islandStringLength = 16;
        }
    }

    public void printPlayers(ShowModelPayload modelPayload) {
        for (Player p : modelPayload.getPlayersList()) {
            top = new StringBuilder();
            titles = new StringBuilder();
            middle = new StringBuilder();
            contents = new StringBuilder();
            bottom = new StringBuilder();
            createPlayerSection(username, p.getUsername(), Section.FIRST);
            createPlayerSection(wizard, p.getWizard().toString(), Section.MIDDLE);
            createPlayerSection(coins, String.valueOf(p.getMyCoins()), Section.MIDDLE);
            createPlayerSection(entrance, createCreatureString(p.getEntrance()), Section.MIDDLE);
            createPlayerSection(diningRoom, createCreatureString(p.getDiningRoom()), Section.MIDDLE);
            createPlayerSection(professors, createProfessorString(p.getProfessors()), Section.MIDDLE);
            if (p.getLastPlayedCards().size() > 0) {
                createPlayerSection(lastAssistant, p.getLastPlayedCard().toString(), Section.MIDDLE);
            } else {
                createPlayerSection(lastAssistant, noAssistant, Section.MIDDLE);
            }
            createPlayerSection(towers, String.valueOf(p.getTowers()), Section.LAST);
            System.out.println(top);
            System.out.println(titles);
            System.out.println(middle);
            System.out.println(contents);
            System.out.println(bottom);

        }
    }

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

        if (givenTitle.equals(entrance) || givenTitle.equals(diningRoom) || givenTitle.equals(professors)) {
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

    private String createCreatureString(StudentContainer container) {

        StringBuilder creatureString = new StringBuilder();
        Map<Creature, Integer> counter = new HashMap<>();
        for (Creature c : Creature.values()) {
            counter.put(c, 0);
        }
        List<Creature> creatures = container.getStudents().stream().map(Student::getCreature).toList();
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

    public void printIslands(ShowModelPayload modelPayload) {
        top = new StringBuilder();
        middle = new StringBuilder();
        secondMiddle = new StringBuilder();
        bottom = new StringBuilder();

        int j = 0;
        int motherNaturePosition = modelPayload.getMotherNature();
        for (Island i : modelPayload.getIslands()) {
            if (motherNaturePosition == j) {
                createIsland(j + 1, i.getNumberOfTowers(), i.getColorOfTowers(), i.getNumberOfNoEntries(), true, createCreatureString(i));
            } else {
                createIsland(j + 1, i.getNumberOfTowers(), i.getColorOfTowers(), i.getNumberOfNoEntries(), false, createCreatureString(i));
            }
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

    private void createIsland(int index, int towers, Color color, int noEntry, boolean hasMotherNature, String creatures) {
        top.append(topLeft);
        middle.append(vertical);
        middle.append(space);
        secondMiddle.append(vertical);
        secondMiddle.append(space);
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
        middle.append(space);
        middle.append(towers);
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
            secondMiddle.append(space);
            secondMiddle.append(space);
            secondMiddle.append(space);
        }
        secondMiddle.append(space);
        secondMiddle.append(vertical);

        top.append(topRight);
        bottom.append(bottomRight);
    }

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

    private void createCloud(int index, String creatures) {
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

    private void createCharacter(int index, Name name, int cost, ShowModelPayload modelPayload) {
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

        for (int i = 0; i < name.toString().length() + 1; i++) {
            top.append(horizontal);
            bottom.append(horizontal);
        }
        middle.append(name);
        middle.append(space);

        top.append(topMiddle);
        middle.append(vertical);
        bottom.append(bottomMiddle);

        for (int i = 0; i < costStringLength; i++) {
            top.append(horizontal);
            bottom.append(horizontal);
        }

        middle.append(space);
        middle.append(this.cost);
        middle.append(space);
        middle.append(cost);
        middle.append(space);

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

        } else {
            top.append(topRight);
            middle.append(vertical);
            bottom.append(bottomRight);
        }

    }
}
