package by.touchsoft.chernetski;

import by.touchsoft.chernetski.connection.Connector;
import by.touchsoft.chernetski.connection.Users;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRunner {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("Server");
        logger.info("Server start");
        Users users = new Users(logger);
        Connector connector = new Connector(users);
        connector.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
        connector.start();
        try {
            Socket socket;
            ServerSocket serverSocket = new ServerSocket(ServerConstants.PORT);
            while (true){
                socket = serverSocket.accept();
                ServerCreator.CreateServer(socket, users, logger);
            }
        } catch (IOException exception){
            logger.error(exception.getMessage() + "\n" + exception.getStackTrace());
        }
    }
}
