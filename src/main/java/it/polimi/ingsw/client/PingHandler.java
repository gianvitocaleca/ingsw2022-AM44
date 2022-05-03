package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.server.NetworkState;
import it.polimi.ingsw.server.PingState;
import it.polimi.ingsw.server.SocketID;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.Message;
import it.polimi.ingsw.server.networkMessages.StringPayload;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PingHandler extends Thread{
    private PingState ps;
    private Timer timer;
    private Socket socket;
    private PrintWriter out;
    private Gson gson;

    public PingHandler(PingState ps, Socket socket) {
        this.ps = ps;
        this.socket = socket;
        timer = new Timer();
        try{
            out = new PrintWriter(socket.getOutputStream());
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
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
    @Override
    public void run(){
        while(true){
            sendPingMessage();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*LocalDateTime twoSecondsLater = LocalDateTime.now().plusSeconds(7);
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
