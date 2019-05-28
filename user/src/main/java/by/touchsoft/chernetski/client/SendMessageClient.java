package by.touchsoft.chernetski.client;

import by.touchsoft.chernetski.UserConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Scanner;

public class SendMessageClient extends Thread {

    private BufferedWriter out;
    private Scanner scanner;
    private String name;

    public SendMessageClient(Scanner scanner, BufferedWriter out, String name) {
        this.out = out;
        this.scanner = scanner;
        this.name = name;
    }

    @Override
    public void run() {
        String message;
        LocalTime time;
        String timeLine;
        while (true) {
            time = LocalTime.now();
            timeLine = time.format(UserConstants.TIME_FORMATTER);
            message = scanner.nextLine();
            try {
                if (message.equals("/exit")){
                    out.write("/exit\n");
                    out.flush();
                    break;
                }
                if(message.equals("/leave")){
                    out.write("/leave\n");
                    out.flush();
                    continue;
                }
                out.write(name + " (" + timeLine + "): " + message + "\n");
                out.flush();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
