package by.touchsoft.chernetski;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class UserConstants {

    public static final int PORT = 55555;
    public static final String ID = "localhost";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final Pattern PATTERN_REGISTRATION = Pattern.compile("/register (agent|client) [A-z]*");

    private UserConstants() {
    }
}
