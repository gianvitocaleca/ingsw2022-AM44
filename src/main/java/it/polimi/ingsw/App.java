package it.polimi.ingsw;

import it.polimi.ingsw.utils.CommandParser;
import it.polimi.ingsw.view.ViewSelector;
import it.polimi.ingsw.network.server.Server;

import java.util.*;

import static it.polimi.ingsw.utils.TextAssets.*;

public class App {
    private static CommandParser cp;

    /**
     * Start point of the game.
     *
     * @param args are given by the player
     */
    public static void main(String[] args) {
        cp = new CommandParser(args);
        if (cp.isServer()) {
            Server server = new Server();
            server.start(getPort());
        } else {
            if (cp.isGui()) {
                ViewSelector.startGui(getAddress(), getPort(), getScale());
            } else {
                ViewSelector.startCli(getAddress(), getPort());
            }
        }
    }

    /**
     * @return is the correct given port
     */
    private static int getPort() {
        int port = Integer.parseInt(cp.getPort());
        if (port < 1023 || port > 65535) {
            printFlags();
        }
        return port;
    }

    /**
     * @return is the correct given address
     */
    private static String getAddress() {
        List<String> providedAddress = Arrays.stream(cp.getAddress().split("\\.")).toList();
        if (providedAddress.size() != 4) {
            System.out.println(validAddress);
            printFlags();
        }
        for (String s : providedAddress) {
            try {
                int addressNumber = Integer.parseInt(s);
                if (addressNumber < 0 || addressNumber > 255) {
                    System.out.println(validAddress);
                    printFlags();
                }
            } catch (NumberFormatException ignore) {
                System.out.println(validAddress);
                printFlags();
            }
        }
        return cp.getAddress();
    }

    /**
     * @return is the correct given scale
     */
    private static String getScale() {
        String scale = cp.getScale();
        try {
            int ans = Integer.parseInt(scale);
            if (ans < 0 || ans > 300) {
                printFlags();
            }
            return scale + "%";
        } catch (NumberFormatException ignore) {
            System.out.println("Provide a valid scale");
            printFlags();
            return "";
        }
    }

    /**
     * Prints useful info on how to start the jar.
     */
    private static void printFlags() {
        System.out.println("Invalid arguments");
        System.out.println("Please provide");
        System.out.println("For client :=: -" + client + " <address> -" + port + " <port>");
        System.out.println("If Gui :=: -" + gui + " -" + scale + " <scale>");
        System.out.println("The scale is used to better fit the game to your screen.");
        System.out.println("For server :=: -" + server + " -" + port + " <port>");
        System.exit(0);
    }

}
