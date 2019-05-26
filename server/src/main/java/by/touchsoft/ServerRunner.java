package by.touchsoft;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRunner {

    public static void main(String[] args) {
        Users users = new Users();
        try {
            Socket socket;
            ServerSocket serverSocket = new ServerSocket(5555);
            while (true){
                socket = serverSocket.accept();
                ServerCreator.CreateServer(socket, users);
            }
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }
}