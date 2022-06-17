package it.polimi.ingsw.client.CLI;

import java.util.Locale;

public class OS {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
    }
}
