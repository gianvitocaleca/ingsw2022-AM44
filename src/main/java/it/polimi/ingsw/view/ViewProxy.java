package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.Listeners.ActionPhaseListener;
import it.polimi.ingsw.controller.Listeners.PlanningPhaseListener;
import it.polimi.ingsw.controller.Status;
import it.polimi.ingsw.controller.events.*;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.enums.Creature;
import it.polimi.ingsw.model.enums.Name;
import it.polimi.ingsw.model.studentcontainers.Cloud;
import it.polimi.ingsw.model.studentcontainers.StudentContainer;

import javax.swing.event.EventListenerList;
import java.util.*;

public class ViewProxy implements EventListener {

    private Scanner scanner;
    private Gson gson;
    private EventListenerList listeners = new EventListenerList();

    private Status gamePhase;


    public ViewProxy() {
        scanner = new Scanner(System.in);
        gson = new Gson();
    }

    public void run() {

        //aspetta messaggi
        //controlla header
        //sulla base della fase in cui si trova esegue controlli, che superati generano eventi

    }

    public void setGamePhase(Status gamePhase) {
        this.gamePhase = gamePhase;
    }

    public void addListener(PlanningPhaseListener listener){

        listeners.add(PlanningPhaseListener.class,listener);
    }
    public void addListener(ActionPhaseListener listener){

        listeners.add(ActionPhaseListener.class,listener);
    }



    public void eventStringPerformed(StringEvent evt){
        String message;
        switch (evt.getHeader()){
            case errorMessage:
                message = gson.toJson(new Message(Headers.errorMessage,new StringPayload(evt.getMessage())));
                break;
            case currentPlayer:
                message = gson.toJson(new Message(Headers.currentPlayer,new StringPayload(evt.getMessage())));
                break;
        }

        System.out.println(evt.getMessage());
        //socket
    }

    public void eventStatusPerformed(StatusEvent evt, Payload payload){
        String message = gson.toJson(new Message(evt.getHeader(), payload));
    }

    public void eventCharacterParameters(CharacterPlayedEvent evt, Name charactersName){
        Payload payload = new CharacterPlayedPayload(charactersName);
        String message = gson.toJson(new Message(Headers.characterPlayed, payload));
    }

    public void playAssistantReceiver(PlanningEvent evt){
        for(PlanningPhaseListener event : listeners.getListeners(PlanningPhaseListener.class)){
            event.eventPerformed(evt);
        }
    }

    public void playCharacterReceiver(PlayCharacterEvent evt){
        for(ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)){
            event.eventPerformed(evt);
        }
    }

    public void characterParametersReceiver(CharacterParametersEvent evt){
        if(gamePhase.isWaitingForParameters()){  // && su header
            for(ActionPhaseListener event : listeners.getListeners(ActionPhaseListener.class)){
                event.eventPerformed(evt);
            }
        }
    }


    private void askForSourceCreature(List<Creature> creatures) {
        System.out.print("Inserisci la creatura dalla sorgente:");
        String s = scanner.next();

        for (Creature c : Creature.values()) {
            if (c.getName().equals(s)) {
                creatures.add(c);
            }
        }

    }

    private int askForIslandIndex() {

        System.out.print("Scegli un'isola:");
        int i = scanner.nextInt();
        //l'isola scelta deve essere una di quelle possibili
        return i;
    }

    private StudentContainer askForDestination() {
        //Chiediamo all'utente di inserire un numero in base alla destinazione scelta
        //Mostriamo le possibili destinazioni all'utente
        return new Cloud(10);
    }

    private int askForMnMovements() {
        System.out.println("Quanti passi in più deve compiere madre natura? (0-2)");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Wrong value provided");
            return 1;
        }

    }

    private void askForDestinationCreatures(List<Creature> creatures) {
        System.out.println("Inserisci la creatura dalla destinazione");
        String s = scanner.next();

        for (Creature c : Creature.values()) {
            if (c.getName().equals(s)) {
                creatures.add(c);
            }
        }
    }




}
