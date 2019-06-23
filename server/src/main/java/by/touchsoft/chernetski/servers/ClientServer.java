package by.touchsoft.chernetski.servers;

import by.touchsoft.chernetski.connection.Users;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

/**
 * Class that works with client's messages
 *
 * @author Vadim Chernetski
 */
public class ClientServer extends Thread {

    /**
     * Field that displays connection with agent
     */
    @Setter
    private boolean connectionStatus;

    /**
     * Displays client is in queue
     */
    @Setter
    private boolean inQueue;
    /**
     * Stream receiving messages
     */
    private BufferedReader in;

    /**
     * Stream sending messages
     */
    private BufferedWriter out;

    /**
     * Log4j logger
     */
    private Logger logger;

    /**
     * Instance of agent
     */
    @Getter
    @Setter
    private Optional<AgentServer> agent;

    /**
     * Name of current client
     */
    @Getter
    private String clientName;

    /**
     * Socket for interaction with chat application
     */
    private Socket socket;

    /**
     * All messages that wasn't sent until client didn't connect to agent
     */
    @Setter
    private StringBuilder messagesBeforeAgentConnect;

    /**
     * Instance of Users class
     */
    @Getter
    private Users users;

    public ClientServer(BufferedReader in, BufferedWriter out, Socket socket, String name, Logger loger) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.users = Users.getInstance(logger);
        this.clientName = name;
        this.logger = loger;
        this.inQueue = false;
        agent = Optional.empty();
        connectionStatus = false;
        messagesBeforeAgentConnect = new StringBuilder();
    }

    /**
     * Method for starting thread
     */
    @Override
    public void run() {
        this.sendMessage("waiting for agent");
        this.sendMessage("Type message for start chat");
        String message;
        while (true) {
            try {
                message = in.readLine();
                if(!inQueue){
                    users.addUser(this);
                    inQueue = true;
                }
                if (message.equals("/leave")) {
                    if (connectionStatus) {
                        users.disconnectClient(this);
                        continue;
                    } else {
                        continue;
                    }
                }
                if (message.equals("/exit")) {
                    exit();
                    break;
                }
                if (connectionStatus) {
                    sendMessages();
                    agent.get().sendMessage(message);
                } else {
                    messagesBeforeAgentConnect.append(message).append("\n");
                }
            } catch (IOException exception) {
                users.userExit(this);
                logger.error("incorrect exit " + exception.getMessage());
                break;
            }
        }
    }

    /**
     * Method sends all missed messages
     */
    public void sendMessages() {
        if ((messagesBeforeAgentConnect.length() > 0) && agent.isPresent()) {
            String history = messagesBeforeAgentConnect.toString();
            history = history.substring(0,history.length()-1);
            messagesBeforeAgentConnect = new StringBuilder();
            agent.get().sendMessage(history);
        }
    }

    /**
     * Method sends message to companion
     *
     * @param message - context of message
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
     * Method that stops client
     */
    private void exit() {
        try {
            out.write("/exit\n");
            out.flush();
            users.userExit(this);
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
