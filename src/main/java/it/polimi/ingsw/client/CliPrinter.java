package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.enums.Creature;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.Professor;
import it.polimi.ingsw.server.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.server.model.students.Student;
import it.polimi.ingsw.server.networkMessages.payloads.ShowModelPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CliPrinter {
    private final String space = " ";
    private final String horizontal = "═";
    private final String vertical = "║";
    private final String topLeft = "╔";
    private final String topRight = "╗";
    private final String topMiddle = "╦";
    private final String middleLeft = "╠";
    private final String middleRight = "╣";
    private final String middleMiddle = "╬";
    private final String bottomLeft = "╚";
    private final String bottomRight = "╝";
    private final String bottomMiddle = "╩";
    private final String username = "Username";
    private final String wizard = "Wizard";
    private final String coins = "Coins";
    private final String entrance = "Entrance";
    private final String diningRoom = "Dining Room";
    private final String professors = "Professors";
    private final String lastAssistant = "Last Assistant";
    private final String towers = "Towers";
    private final String noAssistant = "none";
    private final int realCreatureStringLength = 11;
    private StringBuilder top;
    private StringBuilder titles;
    private StringBuilder middle;
    private StringBuilder contents;
    private StringBuilder bottom;

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

        creatureStringFormatter(creatureString, counter);

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

        creatureStringFormatter(professorString, counter);

        return professorString.toString();
    }

    private void creatureStringFormatter(StringBuilder stringBuilder, Map<Creature, Integer> counter) {
        stringBuilder.append(space);
        stringBuilder.append(CliColors.BG_RED.getCode());
        stringBuilder.append(counter.get(Creature.RED_DRAGONS));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
        stringBuilder.append(CliColors.BG_YELLOW.getCode());
        stringBuilder.append(counter.get(Creature.YELLOW_GNOMES));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
        stringBuilder.append(CliColors.BG_BLUE.getCode());
        stringBuilder.append(counter.get(Creature.BLUE_UNICORNS));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
        stringBuilder.append(CliColors.BG_GREEN.getCode());
        stringBuilder.append(counter.get(Creature.GREEN_FROGS));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
        stringBuilder.append(CliColors.BG_PINK.getCode());
        stringBuilder.append(counter.get(Creature.PINK_FAIRIES));
        stringBuilder.append(CliColors.RST.getCode());
        stringBuilder.append(space);
    }
}
