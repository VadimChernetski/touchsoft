package by.touchsoft;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client{

    private BufferedWriter out;
    private BufferedReader in;
    private Registration registration;
    private Scanner scanner;
    private Socket socket;
    private String name;
    private String role;

    public Client() {
        try {
            socket = new Socket(InetAddress.getByName(ClientConstants.ID), ClientConstants.PORT);
            scanner = new Scanner(System.in, "UTF-8");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
            registration = new Registration(out, scanner);
            registration.registration();
            name =registration.getName();
            role = registration.getRole();
            new SendMessage(scanner, out, name, role).start();
            new ReadMessage(in).start();
        } catch (UnknownHostException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


}
