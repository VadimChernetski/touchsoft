package by.touchsoft;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.regex.Matcher;

public class Server extends Thread{

    private BufferedWriter out;
    private BufferedReader in;
    private String role;
    private Socket socket;

    public Server (Socket socket){
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            message = in.readLine();
            role = readRole(message);
            registerUser();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try{
            while(true){
                message = in.readLine();
                if (role.equals("client")){
                    ServerRunner.users.getClients().get(this).sendMessage(message);
                } else {
                    ServerRunner.users.getClient(this).sendMessage(message);
                }
            }
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }

    private void registerUser() {
        if(role.equals("client")){
            int result = ServerRunner.users.addClient(this);
            if (result == -1){
                this.sendMessage("waiting for agent");
                while (true){
                    boolean addingAgent = ServerRunner.users.addFreeAgents(this);
                    if(addingAgent){
                        break;
                    }
                }
            }
            sendMessage("agent connected\n");
        } else {
            ServerRunner.users.addAgent(this);
        }
    }

    private String readRole(String message){
        Matcher matcher = ServerConstants.PATTERN_ROLE.matcher(message);
        matcher.find();
        return matcher.group();
    }

    private void sendMessage(String message){
        try{
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return Objects.equals(out, server.out) &&
                Objects.equals(in, server.in) &&
                Objects.equals(role, server.role) &&
                Objects.equals(socket, server.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(out, in, role, socket);
    }
}
