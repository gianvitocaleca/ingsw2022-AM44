package it.polimi.ingsw.pingHandler;

import it.polimi.ingsw.server.states.NetworkState;
import it.polimi.ingsw.server.SocketID;

public class ServerPingHandler extends PingHandler implements Runnable {

    public ServerPingHandler(PingState ps, NetworkState ns, SocketID socketID, int time, int maxNoAnswers) {
        super(ps, ns, socketID, time, maxNoAnswers);
    }

    public boolean checkConnectionStatus(){
        if(!ps.isReceived()){
            noAnswers++;
            if(noAnswers==maxNoAnswers){
                ns.disconnectPlayer(socketID.getId());
                System.out.println("Disconnected player "+socketID.getId()+" ,number of connected players: "+ns.getNumberOfConnectedSocket());
                return true;
            }
        }else{
            ps.setReceived(false);
            noAnswers = 0;
        }
        return false;
    }

}
