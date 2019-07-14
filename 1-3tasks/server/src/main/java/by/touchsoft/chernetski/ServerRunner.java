package by.touchsoft.chernetski;

import by.touchsoft.chernetski.connection.Users;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class which starts server for console and web chats
 *
 * @author Vadim Chernetski
 */
public class ServerRunner {

    /**
     * main method of server. It creates ServerSocket, accept connections.
     */
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("Server");
        logger.info("Server start");
        Users users = Users.getInstance(logger);
        try {
            Socket socket;
            ServerSocket serverSocket = new ServerSocket(ServerConstants.PORT);
            while (true) {
                socket = serverSocket.accept();
                ServerCreator.CreateServer(socket, logger, users);
            }
        } catch (IOException exception) {
            logger.error(exception.getMessage() + "\n" + exception.getStackTrace());
        }
    }
}
