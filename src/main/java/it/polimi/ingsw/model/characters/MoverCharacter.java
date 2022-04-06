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

public class MoverCharacter extends StudentContainer implements Character {

    private Name name;
    private Playable model;
    private int updatedCost = 0;

    public MoverCharacter(Name name, Playable model, int capacity) {
        super(capacity);
        this.name = name;
        this.model = model;
        StudentBucket sb = StudentBucket.getInstance();
        for (int i = 0; i < getCapacity(); i++) {
            try {
                addStudent(sb.generateStudent());
            } catch (StudentsOutOfStockException ignore) {
                //this should not happen, there are enough student at the beginning of the game
            }
        }
    }

    @Override
    public boolean canBePlayed(int playerCoins) {
        if(playerCoins>=getCost()){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean effect(CharactersParameters answer) {
        List<Creature> creatures = getStudents().stream().map(s -> s.getCreature()).collect(Collectors.toList());


        switch (name) {
            case JOKER:

                if(!(model.jokerEffect(this, answer.getProvidedSourceCreatures(), answer.getProvidedDestinationCreatures()))){
                    return false;
                }
                return true;

            case MINSTREL:
                if(!model.minstrelEffect(answer.getProvidedSourceCreatures(), answer.getProvidedDestinationCreatures())){
                    return false;
                }
                return true;

            case PRINCESS:

                //check if provided creature is present in princess' attribute students

                if(creatures.containsAll(answer.getProvidedSourceCreatures())){
                    model.princessEffect(this, answer.getProvidedSourceCreatures());
                }else{
                    return false;
                }

                try {
                    this.addStudent(StudentBucket.generateStudent());
                } catch (StudentsOutOfStockException e) {
                    model.checkEndGame();
                }
                break;
            case MONK:

                if(creatures.containsAll(answer.getProvidedSourceCreatures())){
                    model.moveStudents(this, answer.getProvidedDestination(), answer.getProvidedSourceCreatures());

                    try {
                        this.addStudent(StudentBucket.generateStudent());
                    } catch (StudentsOutOfStockException e) {
                        model.checkEndGame();
                    }

                    return true;
                }
                return false;
        }

        return false;
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

    @Override
    public List<Student> getStudents() {
        return super.getStudents();
    }

    @Override
    public Student removeStudent(Creature creature) {
        return super.removeStudent(creature);
    }

    @Override
    public void addStudents(List<Student> newStudents) {
        super.addStudents(newStudents);
    }
}
