package by.touchsoft;

import java.util.regex.Pattern;

public class ServerConstants {

    public static final Pattern PATTERN_ROLE = Pattern.compile("(?<=/register )[a-z]*");
    public static final Pattern PATTERN_NAME = Pattern.compile("(?<=/register (agent|client) )[A-z]*");
}
