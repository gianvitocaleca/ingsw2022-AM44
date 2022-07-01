package it.polimi.ingsw.network.client.sender;

import it.polimi.ingsw.view.GUI.ClientGui;
import it.polimi.ingsw.network.client.receiver.ConcreteGUIReceiver;
import it.polimi.ingsw.network.server.networkMessages.Headers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.Scanner;

import static it.polimi.ingsw.utils.Commands.error;

public class ConcreteGUISender extends AbstractSender {

    private Queue<String> guiEvents;
    private String inputLine;
    private ClientGui clientGui;

    /**
     * Waits for commands from the player.
     * Encodes and sends the messages on the network.
     * @param ip the server address
     * @param port the server port
     * @param clientGui is the graphical interface class
     * @param guiEvents is the queue that contains the commands
     */
    public ConcreteGUISender(String ip, int port, ClientGui clientGui, Queue guiEvents) {
        super(ip, port);
        this.guiEvents = guiEvents;
        this.clientGui = clientGui;

    }

    /**
     * Starts to handle the player's commands
     * @throws IOException
     */
    @Override
    public void startClient() throws IOException {
        super.startClient();
        Thread t = new Thread(new ConcreteGUIReceiver(scanner, cs, ps, clientGui));
        t.start();
        socketOut = new PrintWriter(socket.getOutputStream());
        play(socket);
    }

    /**
     * Starts to listen for user inputs from a queue, until the connection is closed
     *
     * @param socket
     */
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
        socketOut.close();
        try {
            socket.close();
            System.out.println("Connection closed, server unreachable");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
