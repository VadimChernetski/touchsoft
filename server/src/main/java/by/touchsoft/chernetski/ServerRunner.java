package by.touchsoft.chernetski;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRunner {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("Server");
        logger.info("Server start");
        Users users = new Users(logger);
        new Connector(users).start();
        try {
            Socket socket;
            ServerSocket serverSocket = new ServerSocket(55555);
            while (true){
                socket = serverSocket.accept();
                ServerCreator.CreateServer(socket, users, logger);
            }
        } catch (IOException exception){
            exception.printStackTrace();
            logger.error(exception.getMessage() + "\n" + exception.getStackTrace());
        }
    }
}
