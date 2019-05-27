package by.touchsoft.agent;

import by.touchsoft.UserConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class SendMessageAgent extends Thread{

    private BufferedWriter out;
    private List<String> responseTemplates;
    private Scanner scanner;
    private String name;

    public SendMessageAgent(Scanner scanner, BufferedWriter out, String name, List<String> responseTemplates){
        this.responseTemplates = responseTemplates;
        this.out = out;
        this.scanner = scanner;
        this.name = name;
    }

    @Override
    public void run() {
        LocalTime time;
        String message;
        String timeLine;
        while(true){
            try {
                message = scanner.nextLine();
                if(message.equals("/exit")){
                    out.write("/exit\n");
                    out.flush();
                    break;
                }
                if(message.matches("[0-9]{1,2}")){
                    int answerNumber = Integer.valueOf(message);
                    message = responseTemplates.get(--answerNumber);
                }
                time = LocalTime.now();
                timeLine = time.format(UserConstants.TIME_FORMATTER);
                out.write(name + " (" + timeLine + "): " + message + "\n");
                out.flush();
            } catch (IOException exception){
                exception.printStackTrace();
            }
        }
    }
}
