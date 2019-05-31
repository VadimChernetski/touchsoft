package by.touchsoft.chernetski;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class ServerCreator {

    public static void CreateServer(Socket socket, Users users, Logger logger) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            String message = in.readLine();
            String role = getRole(message);
            String name = getName(message);
            switch (role){
                case "client":
                    ClientServer client = new ClientServer(in, out, socket, users, name, logger);
                    client.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
                    client.start();
                    break;
                case "agent":
                    AgentServer agent = new AgentServer(in, out, socket, users, name, logger);
                    agent.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
                    agent.start();
                    break;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static String getRole(String message){
        return message.split(" ")[1];
    }

    private static String getName(String message){
        return message.split(" ")[2];
    }
}
