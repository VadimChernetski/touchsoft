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
 * Class that works with agent's messages
 * @author Vadim Chernetski
 */
public class AgentServer extends Thread implements  UserServer{

    /** Field that displays connection with client */
    @Setter
    private boolean connectionStatus;
    /** Stream receiving messages */
    private BufferedReader in;
    /** Stream sending messages */
    private BufferedWriter out;
    /** Log4j logger */
    private Logger logger;
    /** Instance of client */
    @Setter @Getter
    private Optional<ClientServer> client;
    /** Name of current agent */
    @Getter
    private String agentName;
    /** Socket for interaction with chat application */
    private Socket socket;
    /** Instance of Users class */
    private Users users;

    public AgentServer(BufferedReader in, BufferedWriter out, Socket socket, Users users, String name, Logger logger) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.users = users;
        this.agentName = name;
        this.logger = logger;
        client = Optional.empty();      //Для читаемости кода лучше инициализировать эти 2 поля в полях класса,
        connectionStatus = false;       // а не в конструкторе
    }

    /**
     * Method for starting thread
     */
    @Override
    public void run() {
        users.addUser(this);
        String message;
        while (true) {
            try {
                message = in.readLine();
                if (message.equals("/exit")) {
                    exit();
                    break;
                }
                if (!connectionStatus) {
                    continue;
                }
                if (client.isPresent()) {
                    client.get().sendMessage(message);
                }
            } catch (IOException exception) {
                users.userExit(this);
                logger.error("incorrect exit " + exception.getMessage());
                break;
            }
        }
    }

    /**
     * Method sends message to companion
     * @param message - context of message
     */
    @Override
    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }

    /**
     * Method that stops agent
     */
    private void exit() {
        try {
            out.write("/exit\n");
            out.flush();
            users.userExit(this);
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (!socket.isClosed()) {
                socket.close();
            }
            logger.info(agentName + " exit");
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
