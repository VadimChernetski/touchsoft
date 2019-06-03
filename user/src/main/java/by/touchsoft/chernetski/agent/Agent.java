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

public class Agent {

    private List<String> responseTemplates;

    {
        responseTemplates = new ArrayList<>();
        responseTemplates.add("How can I help you?");
        responseTemplates.add("Hello");
    }

    public Agent(String name, String registerMessage, Scanner scanner, Logger logger) {
        int i = 0;
        System.out.println("To use a pattern, type its number");
        for (String template : responseTemplates) {
            System.out.println(++i + ". " + template);
        }
        try (Socket socket = new Socket(InetAddress.getByName(UserConstants.ID), UserConstants.PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))) {
            out.write(registerMessage + "\n");
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
