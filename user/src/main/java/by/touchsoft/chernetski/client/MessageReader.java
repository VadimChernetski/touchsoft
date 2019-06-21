package by.touchsoft.chernetski.client;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Class for reading messages from server
 *
 * @author Vadim Chernetski
 */
public class MessageReader extends Thread {

    /**
     * Stream receiving messages
     */
    private BufferedReader in;

    /**
     * Log4j instance
     */
    private Logger logger;

    /**
     * Constructor
     *
     * @param in     - stream receiving messages
     * @param logger - Log4j instance
     */
    public MessageReader(BufferedReader in, Logger logger) {
        this.in = in;
        this.logger = logger;
    }

    /**
     * Method for starting thread, that receives messages
     */
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
