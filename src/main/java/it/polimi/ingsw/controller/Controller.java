package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.enums.GamePhases;
import it.polimi.ingsw.controller.events.StatusEvent;
import it.polimi.ingsw.controller.events.StringEvent;
import it.polimi.ingsw.messages.ActionPayload;
import it.polimi.ingsw.messages.CharactersParameters;
import it.polimi.ingsw.messages.Headers;
import it.polimi.ingsw.messages.StringPayload;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.model.exceptions.PlanningPhaseEndedException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.view.ViewProxy;
import it.polimi.ingsw.controller.Listeners.*;

public class Controller {
    /*
    Controller must:
    -reset the standard evaluator at the end of every ActionPhase
    -reset postmanMovements at the end of the ActionPhase
    -do all resets with a proper reset method in GameModel
    -check if a player needs to earn a coin and give it to him, if possible, and proceeds to remove it from the coin reserve
     */

    private GameModel model;
    private ViewProxy viewProxy;
    private Status currentPhase;

    private boolean currentPlayerPlayedCharacter = false;




    public Controller(GameModel model, ViewProxy viewProxy){

        this.model = model;
        this.viewProxy = viewProxy;

        currentPhase = new Status(GamePhases.LOGIN);
        this.viewProxy.setGamePhase(currentPhase);
        this.viewProxy.addListener(new ActionPhaseListener(this));
        this.viewProxy.addListener(new PlanningPhaseListener(this));

        /*List< PropertyChangeListener> temp = viewProxy.getListeners();
        temp.add(new PlanningPhaseListener(this));
        viewProxy.setListeners(temp);*/



    }

    public void run(){

        if(currentPhase.equals(GamePhases.LOGIN)){
            //

            currentPhase.setPhase(GamePhases.PLANNING);
            sendPhaseMessage(Headers.PLANNING);
        }

        while(true){
            if(currentPhase.equals(GamePhases.PLANNING)){
                model.fillClouds();
                if(waitAssistants()){
                    currentPhase.setPhase(GamePhases.ACTION_STUDENTSMOVEMENT);
                    sendPhaseMessage(Headers.ACTION_STUDENTSMOVEMENT);
                }
            }

        }
    }


    private boolean waitAssistants(){

        //genera un messaggio CurrentPlayer e lo invia a tutti
        sendCurrentPlayerMessage();
        //controller rimane in ascolto e si aspetta un messaggio playassistant dal currentplayer (update)



        return true;
    }

    /**
     * This method is called ad the end of the action phase to check if the game has ended in case of StudentOutOfStockException and to notify the clients about
     * the winner of the game
     */
    private void checkIfLastRound(){
        if(model.checkIfLastRound()){
            Player winner = model.findWinner();
            sendWinnerPlayerMessage(winner);
        }
    }

    private void sendCurrentPlayerMessage(){
        Player curr = model.getPlayers().get(model.getCurrentPlayerIndex());
        viewProxy.eventStringPerformed(new StringEvent(this,curr.getUsername(),Headers.currentPlayer));
    }

    private void sendWinnerPlayerMessage(Player winner){
        viewProxy.eventStringPerformed(new StringEvent(this,winner.getUsername(),Headers.winnerPlayer));
    }

    private void sendPhaseMessage (Headers phase){

        if(phase.equals(Headers.action)){
            if(currentPhase.equals(GamePhases.ACTION_STUDENTSMOVEMENT)){
                viewProxy.eventStatusPerformed(new StatusEvent(this,phase),new ActionPayload(true,false,false, true));
            }else if (currentPhase.equals(GamePhases.ACTION_MOVEMOTHERNATURE)){
                if(currentPlayerPlayedCharacter){
                    viewProxy.eventStatusPerformed(new StatusEvent(this,phase),new ActionPayload(false,true,false, false));
                }else{
                    viewProxy.eventStatusPerformed(new StatusEvent(this,phase),new ActionPayload(false,true,false, true));
                }
            }else{
                if(currentPlayerPlayedCharacter){
                    viewProxy.eventStatusPerformed(new StatusEvent(this,phase),new ActionPayload(false,false,true, false));
                }else{
                    viewProxy.eventStatusPerformed(new StatusEvent(this,phase),new ActionPayload(false,false,true, true));
                }
            }
        }else{
            viewProxy.eventStatusPerformed(new StatusEvent(this,phase),new StringPayload(""));
        }

    }

    public void playAssistant(int indexOfAssistant){
        try{
            if(!(model.playAssistant(indexOfAssistant))){
                viewProxy.eventStringPerformed(new StringEvent(this, "Non existent assistant, play another one", Headers.errorMessage));
            }else{
                sendCurrentPlayerMessage();
            }
        }catch (AssistantAlreadyPlayedException a){
            viewProxy.eventStringPerformed(new StringEvent(this, "Already played assistant, play another one", Headers.errorMessage));
        }catch (PlanningPhaseEndedException p){
            model.establishRoundOrder();
            currentPhase.setPhase(GamePhases.ACTION_STUDENTSMOVEMENT);
            sendPhaseMessage(Headers.action);
            sendCurrentPlayerMessage();
        }
    }

    public void playCharacter(int indexOfCharacter){
        try{

            if(!(model.playCharacter(indexOfCharacter))){
                viewProxy.eventStringPerformed(new StringEvent(this,"You don't have enough coins", Headers.errorMessage));
            }else{
                currentPhase.toggleWaitingForParameters();
            }

        }catch(IndexOutOfBoundsException e){
            viewProxy.eventStringPerformed(new StringEvent(this,"Non existent character, try again", Headers.errorMessage));
        }

    }

    public void effect(CharactersParameters parameters){
        model.effect(parameters);
    }

    public GamePhases getCurrentPhase() {
        return currentPhase.getPhase();
    }

    public boolean isWaitingForParameters(){
        return currentPhase.isWaitingForParameters();
    }

}
