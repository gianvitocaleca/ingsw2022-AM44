package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.Playable;

import java.util.ArrayList;
import java.util.List;

public class Thief implements Character {

    private Name name;
    private Playable model;
    private int updatedCost = 0;
    private final int numberOfStudentsToRemove = 3;

    public Thief(Name name, Playable model) {
        this.name = name;
        this.model = model;
    }

    @Override
    public boolean canBePlayed(int playerCoins) {
        if (playerCoins >= getCost()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean effect(CharactersParametersPayload answer) {
        thiefEffect(answer.getProvidedSourceCreatures().get(0));
        return true;
    }

    /**
     * Removes 3 students from all the player's dining room
     * If less than 3 students are present, remove all
     *
     * @param creature is the type of student to be removed
     */
    private void thiefEffect(Creature creature) {
        StudentBucket sb = model.getBucket();
        List<Player> players = model.getPlayers();
        for (Player p : players) {
            int numberToRemove = Math.min(numberOfStudentsToRemove, p.getDiningRoom().getNumberOfStudentsByCreature(creature));
            for (int i = 0; i < numberToRemove; i++) {
                //removes the student from the dining room
                DiningRoom oldDiningRoom = p.getDiningRoom();
                Student removedStudent = oldDiningRoom.removeStudent(creature);
                p.setDiningRoom(oldDiningRoom);
                sb.putBackCreature(removedStudent.getCreature());
            }
        }
        model.setPlayers(players);
        model.setBucket(sb);
        model.setLastRound(false);
        model.checkProfessor();
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getCost() {
        int cost = name.getCost();
        if (updatedCost > 0) cost++;
        return cost;
    }

    @Override
    public boolean hasCoin() {
        return (updatedCost == 1);
    }

    @Override
    public void setUpdatedCost() {
        updatedCost++;
    }

    @Override
    public void unsetUpdatedCost() {
        updatedCost--;
    }

    @Override
    public List<Student> getStudents() {
        return new ArrayList<>();
    }
}
