package it.polimi.ingsw.network.ping;

import com.google.gson.Gson;
import it.polimi.ingsw.network.server.states.NetworkState;
import it.polimi.ingsw.network.server.SocketID;
import it.polimi.ingsw.network.server.networkMessages.Headers;
import it.polimi.ingsw.network.server.networkMessages.Message;
import it.polimi.ingsw.network.server.networkMessages.payloads.StringPayload;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class PingHandler extends Thread {
    protected PingState ps;
    protected NetworkState ns;
    protected SocketID socketID;
    protected PrintWriter out;
    protected Gson gson;
    protected int noAnswers;
    protected int time;
    protected int maxNoAnswers;

    /**
     * Used for the client handler
     */
    public PingHandler(PingState ps, Socket socket, int time, int maxNoAnswers) {
        this.ps = ps;
        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        gson = new Gson();
        this.noAnswers = 0;
        this.time = time;
        this.maxNoAnswers = maxNoAnswers;
    }

    /**
     * Used for the server handler
     */
    public PingHandler(PingState ps, NetworkState ns, SocketID socketID, int time, int maxNoAnswers) {
        this.ps = ps;
        this.socketID = socketID;
        try {
            out = new PrintWriter(socketID.getSocket().getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        gson = new Gson();
        this.noAnswers = 0;
        this.time = time;
        this.maxNoAnswers = maxNoAnswers;
        this.ns = ns;
    }

    /**
     * Manages the state of the connection
     */
    @Override
    public void run() {
        while (true) {
            sendPingMessage();
            try {
                Thread.sleep(time);
                if (checkConnectionStatus()) {
                    ps.setCloseConnection(true);
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Used to send a message
     */
    private void sendPingMessage() {
        Message mex = new Message(Headers.ping, new StringPayload("Ping"));
        out.println(gson.toJson(mex));
        out.flush();
    }

    /**
     * Used to check if the player is connected
     * @return whether the player has left the game
     */
    protected abstract boolean checkConnectionStatus();
}
