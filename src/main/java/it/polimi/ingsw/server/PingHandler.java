package it.polimi.ingsw.server;

import com.google.gson.Gson;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.Message;
import it.polimi.ingsw.server.networkMessages.StringPayload;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PingHandler extends Thread {
    private PingState ps;
    private NetworkState ns;
    private Timer timer;
    private SocketID socketid;
    private PrintWriter out;
    private Gson gson;

    public PingHandler(NetworkState ns,PingState ps, SocketID socket) {
        this.ps = ps;
        this.ns = ns;
        this.socketid = socket;
        timer = new Timer();
        try{
            out = new PrintWriter(socketid.getSocket().getOutputStream());
        }catch(IOException ex){
            ex.printStackTrace();
        }
        gson = new Gson();
    }

    private TimerTask setTimerTask(){
        return new TimerTask() {
            @Override
            public void run() {
                if(!ps.isReceived()){
                    ns.disconnectPlayer(socketid.getPlayerInfo().getUsername());
                }
            }
        };
    }
    @Override
    public void run(){
        while(true){
            sendPingMessage();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*LocalDateTime twoSecondsLater = LocalDateTime.now().plusSeconds(15);
            Date twoSecondsLaterAsDate = Date.from(twoSecondsLater.atZone(ZoneId.systemDefault()).toInstant());
            timer.schedule(setTimerTask(),twoSecondsLaterAsDate);*/
        }
    }

    private void sendPingMessage(){
        Message mex = new Message(Headers.ping,new StringPayload("Ping"));
        out.println(gson.toJson(mex));
        out.flush();
    }
}
