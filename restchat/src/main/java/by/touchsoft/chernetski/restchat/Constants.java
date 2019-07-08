package by.touchsoft.chernetski.restchat;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter TIME_CREATION_PATTERN = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss");
    public static final int PORT = 55555;
    public static final String ID = "localhost";

    private Constants(){}
}
