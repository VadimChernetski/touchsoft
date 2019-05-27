package by.touchsoft.chernetski.client;

import by.touchsoft.chernetski.UserConstants;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public Client(String name, String registerMessage, Scanner scanner) {
        try (Socket socket = new Socket(InetAddress.getByName(UserConstants.ID), UserConstants.PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))) {
            out.write(registerMessage + "\n");
            out.flush();
            SendMessageClient sendMessage = new SendMessageClient(scanner, out, name);
            ReadMessageClient readMessage = new ReadMessageClient(in);
            sendMessage.start();
            readMessage.start();
            sendMessage.join();
            readMessage.join();
        } catch (UnknownHostException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (InterruptedException exception){
            exception.printStackTrace();
        }
    }
}
