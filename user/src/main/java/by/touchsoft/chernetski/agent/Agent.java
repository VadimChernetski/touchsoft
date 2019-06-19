package by.touchsoft.chernetski.agent;

import by.touchsoft.chernetski.UserConstants;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class for user with role Agent
 * @author Vadim Chernetski
 */
public class Agent {

    /** templates of nessages */
    private List<String> responseTemplates;

    {
        responseTemplates = new ArrayList<>();
        responseTemplates.add("How can I help you?");
        responseTemplates.add("Hello");
    }

    /**
     * Constructor. In this constructor starts threads for reading and writing messages
     * @param name - name of User
     * @param registrationMessage - user input during registration
     * @param scanner - instance of Scanner class
     * @param logger - instance of Log4j class
     */
    public Agent(String name, String registrationMessage, Scanner scanner, Logger logger) {
        int i = 0;
        System.out.println("To use a pattern, type its number");
        for (String template : responseTemplates) {
            System.out.println(++i + ". " + template);
        }
        try (Socket socket = new Socket(InetAddress.getByName(UserConstants.ID), UserConstants.PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))) {
            out.write(registrationMessage + "\n");
            out.flush();
            logger.info("agent " + name + " connected");
            MessageReader messageReader = new MessageReader(in, logger);
            MessageSender messageSender = new MessageSender(scanner, out, name, responseTemplates, logger);
            messageReader.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
            messageReader.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
            messageReader.start();
            messageSender.start();
            messageReader.join();
            messageSender.join();
        } catch (UnknownHostException exception) {
            logger.error(exception.getMessage());
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        } catch (InterruptedException exception) {
            logger.error(exception.getMessage());
        }
        logger.info("agent " + name + " disconnected");
    }
}
