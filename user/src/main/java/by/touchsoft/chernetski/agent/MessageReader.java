package by.touchsoft.chernetski.agent;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class MessageReader extends Thread {

    private BufferedReader in;
    private Logger logger;

    public MessageReader(BufferedReader in, Logger logger) {
        this.in = in;
        this.logger = logger;
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                message = in.readLine();
                if (message.equals("/exit")) {
                    break;
                }
                System.out.println(message);
            } catch (IOException exception) {
                logger.error(exception.getMessage());
                System.out.println("some problems with server");
                System.exit(0);
            }
        }
    }

}
