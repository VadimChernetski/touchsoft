package by.touchsoft.chernetski;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Class contains constants for creating and connecting users
 */
public class UserConstants {

    /** server port */
    public static final int PORT = 55555;

    /** formatter for output time */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** pattern for check registration message */
    public static final String PATTERN_REGISTRATION = "/register (agent|client) [A-z0-9]*";

    /** server id */
    public static final String ID = "localhost";

    private UserConstants() {
    }
}
