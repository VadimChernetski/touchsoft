package by.touchsoft.chernetski.registration;

import by.touchsoft.chernetski.UserConstants;
import lombok.Getter;

import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Class for user's registration
 *
 * @author Vadim Chernetski
 */
public class UserData {

    /**
     * Name of user
     */
    @Getter
    private String name;
    /**
     * Role of user
     */
    private String role;
    /**
     * user input
     */
    @Getter
    private String registrationMessage;
    /**
     * instance of Scanner class
     */
    private Scanner scanner;

    /**
     * Constructor
     *
     * @param scanner - instance of Scanner class
     */
    public UserData(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * User information gathering method
     *
     * @return role of user
     */
    public String register() {
        System.out.print("Register please\n");
        while (true) {
            registrationMessage = scanner.nextLine();
            String[] clientData = registrationMessage.split(" ");
            if (checkRegistrationCommand(registrationMessage)) {
                role = clientData[1];
                name = clientData[2];
                break;
            } else {
                System.out.print("Wrong command, try again\n");
            }
        }
        return role;
    }

    /**
     * Input validation
     *
     * @param registrationMessage - input
     * @return true if check passed, else - false
     */
    private boolean checkRegistrationCommand(String registrationMessage) {
        return registrationMessage.matches(UserConstants.PATTERN_REGISTRATION);
    }
}
