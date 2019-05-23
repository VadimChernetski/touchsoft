package by.touchsoft;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerRunner {

    public static Users users = new Users();
    public static void main(String[] args) {
        try {

            ServerSocket serverSocket = new ServerSocket(5555);
            while (true){
                new Server(serverSocket.accept()).start();
                System.out.println(users.getClients());
            }
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }
}
