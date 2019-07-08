package by.touchsoft.chernetski.registration;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDataTest {

    @Test
    void registerShouldReturnRoleIfInputCorrect() {
        ByteArrayInputStream in = new ByteArrayInputStream("/register client Java\n".getBytes());
        Scanner scanner = new Scanner(in);
        UserData userData = new UserData(scanner);
        String expected = "client";
        String actual = userData.register();
        assertEquals(expected, actual);
    }

    @Test
    void registerShouldAskRepeatInputRegistrationDataIfItWasIncorrect() {
        ByteArrayInputStream in = new ByteArrayInputStream("/incorrect message\n/register client Java\n".getBytes());
        Scanner scanner = new Scanner(in);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(byteOut);
        PrintStream oldOut = System.out;
        System.setOut(newOut);
        UserData userData = new UserData(scanner);
        String expected = "Register please\n" +
                "input: register client|agent name\n" +
                "Wrong command, try again\n";
        userData.register();
        System.setOut(oldOut);
        String actual = new String(byteOut.toByteArray());
        assertEquals(expected, actual);
    }
}
