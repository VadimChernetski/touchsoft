package by.touchsoft.chernetski.client;

import by.touchsoft.chernetski.UserConstants;
import by.touchsoft.chernetski.message.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public Client(String name, String registerMessage, Scanner scanner, Logger logger) {
        Message massage = new Message();
        try (Socket socket = new Socket(InetAddress.getByName(UserConstants.ID), UserConstants.PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))) {
            out.write(registerMessage + "\n");
            out.flush();
            logger.info("client" + name + " connected");
            MessageSender messageSender = new MessageSender(scanner, out, name, logger, massage);
            MessageReader messageReader = new MessageReader(in, logger, massage);
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
        } catch (InterruptedException exception){
            logger.error(exception.getMessage());
        }
        logger.info("client" + name + " disconnected");
    }
}