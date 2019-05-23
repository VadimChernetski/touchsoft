package by.touchsoft;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Scanner;

public class SendMessage extends Thread{

    private BufferedWriter out;
    private Scanner scanner;
    private String name;
    private String role;

    public SendMessage(Scanner scanner, BufferedWriter out, String name, String role){
        this.out = out;
        this.scanner = scanner;
        this.name = name;
        this.role = role;
    }

    @Override
    public void run() {
        String message;
        LocalTime time;
        String timeLine;
        while(true){
            System.out.print("you: ");
            message = scanner.nextLine();
            time = LocalTime.now();
            timeLine = time.format(ClientConstants.TIME_FORMATTER);
            try {
                out.write(name + " (" + timeLine + "): " + message + "\n");
                out.flush();
            } catch (IOException exception){
                exception.printStackTrace();
            }
        }
    }
}
