package it.polimi.ingsw.client;

import java.util.Locale;

public class OS {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
    }
}
