package by.touchsoft.chernetski.registration;

import by.touchsoft.chernetski.UserConstants;

import java.util.Scanner;
import java.util.regex.Matcher;

public class Registration {

    private String name;
    private String role;
    private String registerMessage;
    private Scanner scanner;

    public Registration(Scanner scanner) {
        this.scanner = scanner;
    }

    public String register() {
        System.out.print("Register please\n");
        while (true) {
            registerMessage = scanner.nextLine();
            String[] clientData = registerMessage.split(" ");
            if (checkRegistrationCommand(registerMessage)) {
                role = clientData[1];
                name = clientData[2];
                break;
            } else {
                System.out.print("Wrong command, try again\n");
            }
        }
        return role;
    }

    private boolean checkRegistrationCommand(String command) {
        return command.matches(UserConstants.PATTERN_REGISTRATION);
    }

    public String getName() {
        return name;
    }

    public String getRegisterMessage() {
        return  registerMessage;
    }
}
