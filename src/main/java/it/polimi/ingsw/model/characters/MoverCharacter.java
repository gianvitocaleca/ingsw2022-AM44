package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.Playable;
import it.polimi.ingsw.model.exceptions.StudentsOutOfStockException;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;
import it.polimi.ingsw.model.students.Student;
import it.polimi.ingsw.model.students.StudentBucket;

import java.util.List;
import java.util.stream.Collectors;

public class MoverCharacter implements Character {

    private Name name;
    private Playable model;
    private int updatedCost = 0;
    private StudentContainer container;

    public MoverCharacter(Name name, Playable model, StudentContainer container) {
        this.name = name;
        this.model = model;
        this.container = container;
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
    public boolean effect(CharactersParameters answer) {
        List<Creature> creatures = getStudents().stream().map(s -> s.getCreature()).collect(Collectors.toList());
        StudentBucket bucket = model.getBucket();

        switch (name) {
            case JOKER:

                if (!(model.jokerEffect(answer.getProvidedSourceCreatures(), answer.getProvidedDestinationCreatures()))) {
                    return false;
                }
                return true;

            case PRINCESS:

                //check if provided creature is present in princess' attribute students

                if (creatures.containsAll(answer.getProvidedSourceCreatures())) {
                    model.princessEffect(container, answer.getProvidedSourceCreatures());

                    try {
                        container.addStudent(bucket.generateStudent());
                    } catch (StudentsOutOfStockException e) {
                        model.checkEndGame();
                    }

                } else {
                    return false;
                }
                break;
            case MONK:

                if (creatures.containsAll(answer.getProvidedSourceCreatures())) {
                    model.moveStudents(container, answer.getProvidedDestination(), answer.getProvidedSourceCreatures());

                    try {
                        container.addStudent(bucket.generateStudent());
                    } catch (StudentsOutOfStockException e) {
                        model.checkEndGame();
                    }

                } else {
                    return false;
                }
                break;

        }
        model.setBucket(bucket);
        return true;
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public int getCost() {
        return name.getCost() + updatedCost;
    }

    @Override
    public boolean hasCoin() {
        return (updatedCost == 1);
    }

    @Override
    public void setUpdatedCost() {
        updatedCost = 1;
    }

    public List<Student> getStudents() {
        return container.getStudents();
    }

    public Student removeStudent(Creature creature) {
        return container.removeStudent(creature);
    }

    public void addStudents(List<Student> newStudents) {
        container.addStudents(newStudents);
    }
}
