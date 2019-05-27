package by.touchsoft.chernetski;

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
        while (true) {
            System.out.print("Register please\n");
            registerMessage = scanner.nextLine();
            String[] clientData = registerMessage.split(" ");
            if (checkRegistrationCommand(registerMessage)) {
                role = clientData[1];
                name = clientData[2];
                break;
            } else {
                System.out.println("Wrong command, try again");
            }
        }
        return role;
    }

    private boolean checkRegistrationCommand(String command) {
        Matcher matcher = UserConstants.PATTERN_REGISTRATION.matcher(command);
        String registrationCommand = "";
        if (matcher.find()) {
            registrationCommand = matcher.group();
        }
        return command.equals(registrationCommand);
    }

    public String getName() {
        return name;
    }

    public String getRegisterMessage() {
        return  registerMessage;
    }
}
