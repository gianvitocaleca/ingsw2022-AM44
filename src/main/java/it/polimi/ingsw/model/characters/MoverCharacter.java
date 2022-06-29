package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Playable;
import it.polimi.ingsw.model.exceptions.GameEndedException;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.exceptions.UnplayableEffectException;
import it.polimi.ingsw.model.studentcontainers.DiningRoom;
import it.polimi.ingsw.model.studentcontainers.Entrance;
import it.polimi.ingsw.model.studentcontainers.Island;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.gameboard.Table;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.students.StudentBucket;
import it.polimi.ingsw.network.server.networkMessages.payloads.CharactersParametersPayload;

import java.util.ArrayList;
import java.util.List;

public class MoverCharacter extends StudentContainer implements Character {

    private Name name;
    private Playable model;
    private int updatedCost = 0;

    public MoverCharacter(Name name, Playable model, int capacity) {
        super(capacity);
        this.name = name;
        this.model = model;

        StudentBucket bucket = model.getBucket();

        for (int i = 0; i < capacity; i++) {
            try {
                this.addStudent(bucket.generateStudent());
            } catch (StudentsOutOfStockException e) {
                model.setLastRound(true);
            }
        }
        model.setBucket(bucket);
    }


    @Override
    public boolean canBePlayed(int playerCoins) {
        return playerCoins >= getCost();
    }

    @Override
    public boolean effect(CharactersParametersPayload answer) throws GameEndedException, UnplayableEffectException {

        List<Player> players = model.getPlayers();
        Player currentPlayer = players.get(model.getCurrentPlayerIndex());
        List<Creature> sourceCreatures;
        List<Creature> destinationCreatures = new ArrayList<>();
        Table table = model.getTable();
        List<Island> islands = table.getIslands();
        int destinationIslandIndex = 0;

        sourceCreatures = answer.getProvidedSourceCreatures();

        if (name.isNeedsDestinationCreature()) {
            destinationCreatures = answer.getProvidedDestinationCreatures();
        }
        if (name.isNeedsDestination()) {
            destinationIslandIndex = answer.getProvidedIslandIndex();
        }

        StudentContainer tempSource = null;
        StudentContainer tempDestination = null;

        if (name.isSwap()) {
            if (sourceCreatures.size() != destinationCreatures.size() ||
                    destinationCreatures.size() > name.getMaxMoves()) {
                return false;
            }


            if (name.equals(Name.JOKER)) {
                tempSource = new MoverCharacter(this.getName(), this.model, this.getCapacity());
                tempSource.setStudents(this.getStudents());
            } else if (name.equals(Name.MINSTREL)) {
                tempSource = currentPlayer.getDiningRoom();
            }

            tempDestination = currentPlayer.getEntrance();

            checkPlayable(sourceCreatures, destinationCreatures, tempSource, tempDestination, tempSource == null);


            if (model.moveStudents(tempSource, tempDestination, sourceCreatures)) {
                if (model.moveStudents(tempDestination, tempSource, destinationCreatures)) {
                    Entrance concreteDestination = currentPlayer.getEntrance();
                    concreteDestination.setStudents(tempDestination.getStudents());
                    players.get(model.getCurrentPlayerIndex()).setEntrance(concreteDestination);

                    if (name.equals(Name.JOKER)) {
                        this.setStudents(tempSource.getStudents());
                    } else if (name.equals(Name.MINSTREL)) {
                        DiningRoom concreteSource = players.get(model.getCurrentPlayerIndex()).getDiningRoom();
                        concreteSource.setStudents(tempSource.getStudents());
                        players.get(model.getCurrentPlayerIndex()).setDiningRoom(concreteSource);
                    }
                    model.setPlayers(players);
                }
            } else {
                return false;
            }
        } else if (name.isMove()) {
            if (sourceCreatures.size() > name.getMaxMoves()) return false;

            tempSource = new MoverCharacter(this.getName(), this.model, this.getCapacity());
            tempSource.setStudents(this.getStudents());

            if (name.isNeedsDestination()) {
                tempDestination = islands.get(destinationIslandIndex);
            } else if (name.equals(Name.PRINCESS)) {
                tempDestination = currentPlayer.getDiningRoom();
            }

            checkPlayable(sourceCreatures, destinationCreatures, tempSource, tempDestination, tempDestination == null);

            if (model.moveStudents(tempSource, tempDestination, sourceCreatures)) {

                this.setStudents(tempSource.getStudents());

                if (name.isNeedsDestination()) {
                    Island concreteDestination = islands.get(destinationIslandIndex);
                    concreteDestination.setStudents(tempDestination.getStudents());
                    islands.set(destinationIslandIndex, concreteDestination);
                    table.setIslands(islands);
                    model.setTable(table);
                } else if (name.equals(Name.PRINCESS)) {
                    DiningRoom concreteDestination = currentPlayer.getDiningRoom();
                    concreteDestination.setStudents(tempDestination.getStudents());
                    players.get(model.getCurrentPlayerIndex()).setDiningRoom(concreteDestination);
                    model.setPlayers(players);
                }
                StudentBucket bucket = model.getBucket();
                try {
                    this.addStudent(bucket.generateStudent());
                    model.setBucket(bucket);
                } catch (StudentsOutOfStockException e) {
                    model.setLastRound(true);
                }
            } else {
                return false;
            }

        }

        model.coinGiver();
        model.checkProfessor();

        return true;
    }

    private void checkPlayable(List<Creature> sourceCreatures, List<Creature> destinationCreatures, StudentContainer tempSource, StudentContainer tempDestination, boolean b) throws UnplayableEffectException {
        for (Creature c : Creature.values()) {
            int numberSource = sourceCreatures.stream().filter(s -> s.equals(c)).toList().size();
            int numberDestination = destinationCreatures.stream().filter(s -> s.equals(c)).toList().size();
            if (tempSource.getNumberOfStudentsByCreature(c) < numberSource) throw new UnplayableEffectException();
            if (tempDestination.getNumberOfStudentsByCreature(c) < numberDestination)
                throw new UnplayableEffectException();
        }

        if ((b) || (tempSource.getStudents().size() < sourceCreatures.size()) || (tempDestination.getStudents().size() < destinationCreatures.size())) {
            throw new UnplayableEffectException();
        }
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
    public void setUpdatedCost() {
        updatedCost++;
    }

    @Override
    public void unsetUpdatedCost() {
        updatedCost--;
    }
}
