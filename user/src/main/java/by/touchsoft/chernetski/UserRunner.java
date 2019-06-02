package by.touchsoft.chernetski;

import by.touchsoft.chernetski.agent.Agent;
import by.touchsoft.chernetski.client.Client;
import by.touchsoft.chernetski.registration.Registration;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class UserRunner {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("user");
        logger.info("start application");
        String role;
        Scanner scanner = new Scanner(System.in, "UTF-8");
        Registration registration = new Registration(scanner);
        role = registration.register();
        switch (role){
            case "agent":
                new Agent(registration.getName(), registration.getRegisterMessage(), scanner, logger);
                break;
            case "client":
                new Client(registration.getName(), registration.getRegisterMessage(), scanner, logger);
        }
    }
}
