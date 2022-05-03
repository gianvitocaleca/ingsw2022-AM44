package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.server.PingState;
import it.polimi.ingsw.server.networkMessages.Message;
import it.polimi.ingsw.server.networkMessages.StringPayload;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LineClient {
    private String ip;
    private int port;
    private String inputLine;
    private ClientState cs;
    private Gson gson;
    private PingState ps;

    private Scanner stdin;
    private PrintWriter socketOut;

    public LineClient(String ip, int port){
        this.ip = ip;
        this.port = port;
        gson = new Gson();
        ps = new PingState();
    }

    public void startClient() throws IOException {
        cs = new ClientState();
        Socket socket = new Socket(ip, port);
        System.out.println("Connection established");
        System.out.println("Client dynamic port number: " + socket.getLocalPort());
        Scanner s = new Scanner(socket.getInputStream());
        Thread t = new Thread(new MessageReceiverClient(s,cs,ps));
        t.start();
        Thread t1 = new Thread(new PingHandler(ps,socket));
        t1.start();
        socketOut = new PrintWriter(socket.getOutputStream());
        stdin = new Scanner(System.in);

        play(socket);


    }

    private void play(Socket socket){
        try{
            while (true){
                inputLine = stdin.nextLine();
                String result = encodeMessage(inputLine);
                socketOut.println(result);
                socketOut.flush();
            }

        } catch(NoSuchElementException e){
            System.out.println("Connection closed");
        } finally {
            stdin.close();
            socketOut.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeMessage(String string){
        return gson.toJson(new Message(cs.getHeaders(),new StringPayload(string)));
    }
}
