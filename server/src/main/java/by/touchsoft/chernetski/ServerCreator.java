package by.touchsoft.chernetski;

import java.io.*;
import java.net.Socket;

public class ServerCreator {

    public static void CreateServer(Socket socket, Users users) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            String message = in.readLine();
            String role = getRole(message);
            switch (role){
                case "client":
                    new ClientServer(in, out, socket, users).start();
                    break;
                case "agent":
                    new AgentServer(in, out, socket, users).start();
                    break;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static String getRole(String message){
        String[] userData = message.split(" ");
        return userData[1];
    }
}
