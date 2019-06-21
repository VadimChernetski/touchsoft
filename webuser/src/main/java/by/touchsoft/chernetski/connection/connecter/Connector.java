package by.touchsoft.chernetski.connection.connecter;

import by.touchsoft.chernetski.connection.constants.ConnectionConstants;
import by.touchsoft.chernetski.websocket.endpoint.ChatEndPoint;
import by.touchsoft.chernetski.websocket.message.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for connecting to server
 *
 * @author Vadim Chernetski
 */
@NoArgsConstructor
public class Connector extends Thread {

    /**
     * Stream receiving messages
     */
    private BufferedReader in;

    /**
     * Stream sending messages
     */
    @Setter
    private BufferedWriter out;

    /**
     * WebSocket endpoint
     */
    private ChatEndPoint endPoint;

    /**
     * Log4j instance
     */
    private Logger logger;

    /**
     * Name of user
     */
    private String name;

    /**
     * Socket for connection with server
     */
    private Socket socket;

    /**
     * Constructor
     *
     * @param endPoint - WebSocket endpoint
     * @param logger   - Log4j instance
     * @param name     - name of user
     */
    public Connector(ChatEndPoint endPoint, Logger logger, String name) {
        this.endPoint = endPoint;
        this.name = name;
        this.logger = logger;
        logger.info(name + " connected");
        try {
            this.socket = new Socket(ConnectionConstants.ID, ConnectionConstants.PORT);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }

    /**
     * Method for starting thread, that receives messages
     */
    @Override
    public void run() {
        String input;
        Matcher matcher;
        Pattern pattern = Pattern.compile("[A-z]+ (?=\\()");
        Message message;
        String name;
        while (true) {
            System.out.println("start" + this.name);
            try {
                input = in.readLine();
                if (input.equals("/exit")) {
                    exit();
                    break;
                }
                matcher = pattern.matcher(input);
                if (matcher.find()) {
                    name = matcher.group();
                    message = new Message(input.replaceAll(name, ""), name);
                } else {
                    message = new Message(input, "server");
                }
                endPoint.sendMessage(message);
            } catch (IOException exception) {
                logger.error(exception.getMessage());
                endPoint.sendMessage(new Message("some problems with server", "server"));
                break;
            }
        }
    }

    /**
     * Method sends messages to server
     *
     * @param message - message context
     */
    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }

    /**
     * Method stops work of user
     */
    private void exit() {
        logger.info(name + " exit");
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
