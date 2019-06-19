package by.touchsoft.chernetski.agent;

import by.touchsoft.chernetski.UserConstants;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

/**
 *  Class for sending messages to server
 * @author Vadim Chernetski
 */
public class MessageSender extends Thread{

    /** Stream sending messages */
    private BufferedWriter out;
    /** message templates */
    private List<String> responseTemplates;
    /** Log4j instance */
    private Logger logger;
    /** Scanner instance */
    private Scanner scanner;
    /** Name of agent */
    private String name;

    /**
     * Constructor
     * @param scanner - Scanner instance
     * @param out - stream sending messages
     * @param name - name of client
     * @param responseTemplates - message templates
     * @param logger - Log4j instance
     */
    public MessageSender(Scanner scanner, BufferedWriter out, String name, List<String> responseTemplates, Logger logger){
        this.responseTemplates = responseTemplates;
        this.out = out;
        this.scanner = scanner;
        this.name = name;
        this.logger = logger;
    }

    /**
     * Method for starting thread, that sends messages
     */
    @Override
    public void run() {
        LocalTime time;
        String message;
        String timeLine;
        while(true){
            try {
                message = scanner.nextLine().trim();
                if(message.isEmpty()){
                    continue;
                }
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
                logger.error(exception.getMessage());
                System.out.println("some problems with server");
                System.exit(0);
            }
        }
    }
}
