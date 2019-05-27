package by.touchsoft.agent;

import by.touchsoft.UserConstants;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Agent {

    private boolean connectionStatus;
    private List<String> responseTemplates;


    {
        responseTemplates = new ArrayList<>();
        responseTemplates.add("How can I help you?\n");
    }

    public Agent(String name, String registerMessage, Scanner scanner) {
        connectionStatus = false;
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
            ReadMessageAgent readMessage = new ReadMessageAgent(in);
            SendMessageAgent sendMessage = new SendMessageAgent(scanner, out, name, responseTemplates);
            readMessage.start();
            sendMessage.start();
            readMessage.join();
            sendMessage.join();
        } catch (UnknownHostException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (InterruptedException exception){
            exception.printStackTrace();
        }
    }

    public boolean getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
}
