package it.polimi.ingsw.client;

import it.polimi.ingsw.server.networkMessages.Headers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class ConcreteGUISender extends AbstractSender{

    private Queue<String> guiEvents;
    private String inputLine;
    private ClientGui clientGui;
    public ConcreteGUISender(String ip, int port, ClientGui clientGui) {
        super(ip, port);
        guiEvents = new LinkedList<>();
        this.clientGui = clientGui;

    }

    @Override
    public void startClient() throws IOException {
        super.startClient();
        Scanner s = new Scanner(socket.getInputStream());
        Thread t = new Thread(new ConcreteGUIReceiver(s, cs, ps, clientGui));
        t.start();
        socketOut = new PrintWriter(socket.getOutputStream());
        play(socket);
    }

    @Override
    void play(Socket socket) {
        while (!ps.isCloseConnection()) {
            String result;
            do {
                inputLine = guiEvents.poll();
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
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        socketOut.close();
        try {
            socket.close();
            System.out.println("Connection closed, server unreachable");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
