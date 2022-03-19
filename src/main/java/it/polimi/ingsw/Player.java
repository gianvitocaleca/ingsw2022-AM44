package it.polimi.ingsw;

import java.util.*;

public class Player {
    private final String username;
    private final Color my_color;
    private final String wizard;
    private final Entrance entrance;
    private final DiningRoom dining_room;
    private Assistant last_played_card;
    private int my_coins;
    private List<Assistant> assistant_deck;
    private List<Professor> professors;
    private int towers;

    public Player(String username, Color my_color, Assistant last_played_card,
                  int my_coins, List<Assistant> assistant_deck,
                  List<Professor> professors, String wizard, int towers,
                  Entrance entrance, DiningRoom dining_room) {

        this.username = username;
        this.my_color = my_color;
        this.last_played_card = last_played_card;
        this.my_coins = my_coins;
        this.assistant_deck = assistant_deck;
        this.professors = professors;
        this.wizard = wizard;
        this.towers = towers;
        this.entrance = entrance;
        this.dining_room = dining_room;
    }

    public String getUsername() {
        return username;
    }

    public Color getMy_color() {
        return my_color;
    }

    public Assistant getLast_played_card() {
        return last_played_card;
    }

    public int getMy_coins() {
        return my_coins;
    }

    public List<Assistant> getAssistant_deck() {
        return assistant_deck;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public String getWizard() {
        return wizard;
    }

    public int getTowers() {
        return towers;
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public DiningRoom getDining_room() {
        return dining_room;
    }

    public void addCoin() {
        this.my_coins++;
    }

    public void removeCoin(int character_cost) {
        my_coins = my_coins - character_cost;
    }

    public void setAssistantCard(Assistant assistant) {
        last_played_card = assistant;
    }

    public void modifyTower(int num_of_towers) {
        towers = towers + num_of_towers;
    }

    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    public Professor removeProfessor(Creature creature) {
        return new Professor(creature);
    }

}
