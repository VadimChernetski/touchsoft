package by.touchsoft;

import java.util.Scanner;

public class UserRunner {

    public static void main(String[] args) {
        String role;
        Scanner scanner = new Scanner(System.in, "UTF-8");
        Registration registration = new Registration(scanner);
        role = registration.register();
        switch (role){
            case "agent":
                new Agent(registration.getName(), registration.getRegisterMessage(), scanner);
                break;
            case "client":
                new Client(registration.getName(), registration.getRegisterMessage(), scanner);
        }
    }
}
