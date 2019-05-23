package by.touchsoft;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;

public class Registration {

    private BufferedWriter out;
    private Scanner scanner;
    private String name;
    private String role;

    public Registration(BufferedWriter out, Scanner scanner) {
        this.out = out;
        this.scanner = scanner;
    }

    public void registration() {
        while (true) {
            System.out.print("Register please\n");
            String command = scanner.nextLine();
            String[] clientData = command.split(" ");
            if (checkRegistrationCommand(command)) {
                role = clientData[1];
                name = clientData[2];
                SendData(command);
                break;
            } else {
                System.out.println("Wrong command, try again");
            }
        }
    }

    private boolean checkRegistrationCommand(String command) {
        Matcher matcher = ClientConstants.PATTERN_REGISTRATION.matcher(command);
        String registrationCommand = "";
        if (matcher.find()) {
            registrationCommand = matcher.group();
        }
        return command.equals(registrationCommand);
    }

    private boolean SendData(String command) {
        try {
            out.write(command + "\n");
            out.flush();
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
