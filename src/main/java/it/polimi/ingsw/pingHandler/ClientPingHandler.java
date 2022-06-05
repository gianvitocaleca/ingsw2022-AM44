package it.polimi.ingsw.pingHandler;

import it.polimi.ingsw.pingHandler.PingHandler;
import it.polimi.ingsw.pingHandler.PingState;

import java.net.Socket;


public class ClientPingHandler extends PingHandler implements Runnable{

    public ClientPingHandler(PingState ps, Socket socket, int time, int maxNoAnswers) {
        super(ps, socket, time, maxNoAnswers);
    }

    public boolean checkConnectionStatus(){
        if(!ps.isReceived()){
            noAnswers++;
            if(noAnswers==maxNoAnswers){
                return true;
            }
        }else{
            ps.setReceived(false);
            noAnswers = 0;
        }
        return false;
    }
}