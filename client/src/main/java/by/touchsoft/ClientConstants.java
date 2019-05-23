package by.touchsoft;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class ClientConstants {

    public static final int PORT = 5555;
    public static final String ID = "localhost";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final Pattern PATTERN_REGISTRATION = Pattern.compile("/register (agent|client) [A-z]*");

    private ClientConstants() {
    }
}
