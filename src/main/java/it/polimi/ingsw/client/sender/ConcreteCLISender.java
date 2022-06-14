package it.polimi.ingsw.client.sender;

import it.polimi.ingsw.client.receiver.ConcreteCLIReceiver;
import it.polimi.ingsw.server.networkMessages.Headers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ConcreteCLISender extends AbstractSender {
    private String inputLine;
    private Scanner stdin;
    private final String error = "error";

    public ConcreteCLISender(String ip, int port) {
        super(ip,port);
    }

    @Override
    public void startClient() throws IOException {
        super.startClient();
        Scanner s = new Scanner(socket.getInputStream());
        Thread t = new Thread(new ConcreteCLIReceiver(s, cs, ps));
        t.start();
        socketOut = new PrintWriter(socket.getOutputStream());
        stdin = new Scanner(System.in);
        play(socket);
    }

    public void play(Socket socket) {
        if(!cs.getDisconnection()){
            while (!ps.isCloseConnection()) {
                String result;
                do {
                    inputLine = stdin.nextLine();
                    result = encodeMessage(inputLine);
                } while (result.equals(error));
                socketOut.println(result);
                if (cs.getHeaders().equals(Headers.loginMessage_Username)) {
                    setUsername(inputLine);
                }
                if (socketOut.checkError()) {
                    System.out.println("There is an error with the server.. closing connection");
                    break;
                }
            }
            while (!ps.isCloseConnection()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("La connessione è chiusa: " + ps.isCloseConnection());
        }


        stdin.close();
        socketOut.close();
        try {
            socket.close();
            System.out.println("Connection closed, server unreachable");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
