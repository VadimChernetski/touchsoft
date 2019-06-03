package by.touchsoft.chernetski.client;

import by.touchsoft.chernetski.UserConstants;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Scanner;

public class MessageSender extends Thread {

    private BufferedWriter out;
    private Logger logger;
    private Scanner scanner;
    private String name;

    public MessageSender(Scanner scanner, BufferedWriter out, String name, Logger logger) {
        this.out = out;
        this.scanner = scanner;
        this.name = name;
        this.logger = logger;
    }

    @Override
    public void run() {
        String message;
        LocalTime time;
        String timeLine;
        while (true) {
            try {
                time = LocalTime.now();
                timeLine = time.format(UserConstants.TIME_FORMATTER);
                message = scanner.nextLine().trim();
                if(message.isEmpty()){
                    continue;
                }
                if (message.equals("/exit")) {
                    out.write("/exit\n");
                    out.flush();
                    break;
                }
                if (message.equals("/leave")) {
                    out.write("/leave\n");
                    out.flush();
                    continue;
                }
                message = name + " (" + timeLine + "): " + message + "\n";
                out.write(message);
                out.flush();
            } catch (IOException exception) {
                logger.error(exception.getMessage());
                System.out.println("some problems with server");
                System.exit(0);
            }
        }
    }
}
