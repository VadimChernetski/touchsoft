package by.touchsoft.chernetski.client;

import by.touchsoft.chernetski.UserConstants;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Class for user with role Client
 *
 * @author Vadim Chernetski
 */
public class Client {

    /**
     * Constructor. In this constructor starts threads for reading and writing messages
     *
     * @param name                - name of User
     * @param registrationMessage - user input during registration
     * @param scanner             - instance of Scanner class
     * @param logger              - instance of Log4j class
     */
    public Client(String name, String registrationMessage, Scanner scanner, Logger logger) {
        try (Socket socket = new Socket(InetAddress.getByName(UserConstants.ID), UserConstants.PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))) {
            out.write(registrationMessage + "\n");
            out.flush();
            logger.info("client " + name + " connected");
            MessageSender messageSender = new MessageSender(scanner, out, name, logger);
            MessageReader messageReader = new MessageReader(in, logger);
            messageReader.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
            messageReader.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
            messageSender.start();
            messageReader.start();
            messageSender.join();
            messageReader.join();
        } catch (UnknownHostException exception) {
            logger.error(exception.getMessage());
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        } catch (InterruptedException exception) {
            logger.error(exception.getMessage());
        }
        logger.info("client " + name + " disconnected");
    }
}
