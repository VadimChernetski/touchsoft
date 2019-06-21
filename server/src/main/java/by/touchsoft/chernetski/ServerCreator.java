package by.touchsoft.chernetski;

import by.touchsoft.chernetski.connection.Users;
import by.touchsoft.chernetski.servers.AgentServer;
import by.touchsoft.chernetski.servers.ClientServer;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Class creates server-side users
 *
 * @author Vadim Chernetski
 */
public class ServerCreator {

    /**
     * Method creates threads based on role of the user
     *
     * @param socket - socket accepted by server
     * @param logger - instance of logger
     */
    public static void CreateServer(Socket socket, Logger logger, Users users) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            String message = in.readLine();
            String role = getRole(message);
            String name = getName(message);
            switch (role) {
                case "client":
                    ClientServer client = new ClientServer(in, out, socket, name, logger);
                    client.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
                    client.start();
                    users.addUser(client);
                    break;
                case "agent":
                    AgentServer agent = new AgentServer(in, out, socket, name, logger);
                    agent.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
                    agent.start();
                    users.addUser(agent);
                    break;
            }
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }

    /**
     * Method look for user's role
     *
     * @param message - message entered by user during registration
     * @return user's role
     */
    private static String getRole(String message) {
        return message.split(" ")[1];
    }

    /**
     * Method look for user's name
     *
     * @param message - message entered by user during registration
     * @return user's name
     */
    private static String getName(String message) {
        return message.split(" ")[2];
    }
}
