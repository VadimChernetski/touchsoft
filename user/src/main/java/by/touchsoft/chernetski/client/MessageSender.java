package by.touchsoft.chernetski.client;

import by.touchsoft.chernetski.UserConstants;
import by.touchsoft.chernetski.message.Message;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Scanner;

public class MessageSender extends Thread {

    private BufferedWriter out;
    private Logger logger;
    private Message message;
    private Scanner scanner;
    private String name;

    public MessageSender(Scanner scanner, BufferedWriter out, String name, Logger logger, Message message) {
        this.message = message;
        this.out = out;
        this.scanner = scanner;
        this.name = name;
        this.logger = logger;
    }

    @Override
    public void run() {
        String context;
        LocalTime time;
        String timeLine;
        while (true) {
            time = LocalTime.now();
            timeLine = time.format(UserConstants.TIME_FORMATTER);
            context = scanner.nextLine();
            try {
                if (context.equals("/exit")){
                    out.write("/exit\n");
                    out.flush();
                    break;
                }
                if(context.equals("/leave")){
                    out.write("/leave\n");
                    out.flush();
                    continue;
                }
                context = name + " (" + timeLine + "): " + context + "\n";
                out.write(name + " (" + timeLine + "): " + context + "\n");
                out.flush();
            } catch (IOException exception) {
                logger.error(exception.getMessage());
                System.out.println("some problems with server");
                System.exit(0);
            }
        }
    }
}
