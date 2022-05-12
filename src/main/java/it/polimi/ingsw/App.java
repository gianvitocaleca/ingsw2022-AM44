package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientMain;
import it.polimi.ingsw.server.ServerMain;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.viewProxy.MessageHandler;

import java.util.*;

public class App {

    private static String client = "client";

    private static String server = "server";

    private static String address = "loopback";

    private static String loopback = "127.0.0.1";

    private static String port = "default";

    private static int defaultPort = 1337;

    public static void main(String[] args) {

        if (args.length > 0) {
            if (args[0].equals(client)) {
                ClientMain clientMain = new ClientMain();
                if (args[1].equals(address)) {
                    if (args[2].equals(port)) {
                        clientMain.start(loopback, defaultPort);
                    } else {
                        int temp = Integer.parseInt(args[2]);
                        clientMain.start(loopback, temp);
                    }
                } else {
                    if (args[2].equals(port)) {
                        clientMain.start(args[1], defaultPort);
                    } else {
                        int temp = Integer.parseInt(args[2]);
                        clientMain.start(args[1], temp);
                    }
                }

            } else if (args[0].equals(server)) {
                ServerMain serverMain = new ServerMain();

                if (args[1].equals(port)) {
                    serverMain.start(defaultPort);
                } else {
                    int temp = Integer.parseInt(args[1]);
                    serverMain.start(temp);
                }

            } else {
                System.out.println("Invalid arguments");
                System.out.println("Please provide");
                System.out.println("For client :=: client <address> <port>");
                System.out.println("For server :=: server <port>");
            }
        }

    }
}
