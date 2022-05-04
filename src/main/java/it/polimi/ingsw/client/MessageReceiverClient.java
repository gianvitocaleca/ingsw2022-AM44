package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.pingHandler.PingState;
import it.polimi.ingsw.server.networkMessages.Headers;
import it.polimi.ingsw.server.networkMessages.StringPayload;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MessageReceiverClient extends Thread{
    private Scanner socketIn;
    private Gson gson;
    private String socketLine;
    private ClientState cs;
    private PingState ps;

    public MessageReceiverClient(Scanner socketIn, ClientState cs, PingState ps) {
        this.socketIn = socketIn;
        gson = new Gson();
        this.cs = cs;
        this.ps = ps;
    }

    @Override
    public void run(){
        while(true){
            try{
                socketLine = socketIn.nextLine();
                ps.setReceived(true);
                translateMessage(socketLine);
            }
            catch (NoSuchElementException ignore){
                if(ps.isCloseConnection()){
                    break;
                }
            }
        }

    }

    private void translateMessage(String socketLine){
        JsonObject jsonTree = JsonParser.parseString(socketLine).getAsJsonObject();
        JsonElement jsonHeader = jsonTree.get("header");
        Headers header = gson.fromJson(jsonHeader, Headers.class);
        if(!header.equals(Headers.errorMessage)&&!header.equals(Headers.ping)){
            cs.setHeaders(header);
        }
        if(!header.equals(Headers.ping)){
            System.out.println(header);
            JsonElement jsonPayload = jsonTree.get("payload");
            StringPayload stringPayload = gson.fromJson(jsonPayload, StringPayload.class);
            System.out.println(stringPayload.getString());
        }

    }
}
