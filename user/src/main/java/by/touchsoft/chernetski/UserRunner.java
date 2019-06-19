package by.touchsoft.chernetski;

import by.touchsoft.chernetski.agent.Agent;
import by.touchsoft.chernetski.client.Client;
import by.touchsoft.chernetski.registration.UserData;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * Class which starts console chat application
 * @author Vadim Chernetski
 */
public class UserRunner {

    /**
     * main method of application. It creates user (agent or client)
     */
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("user");
        logger.info("start application");
        String role;
        Scanner scanner = new Scanner(System.in, "UTF-8");
        UserData userData = new UserData(scanner);
        role = userData.register();
        switch (role){
            case "agent":
                new Agent(userData.getName(), userData.getRegistrationMessage(), scanner, logger);
                break;
            case "client":
                new Client(userData.getName(), userData.getRegistrationMessage(), scanner, logger);
        }
    }
}
