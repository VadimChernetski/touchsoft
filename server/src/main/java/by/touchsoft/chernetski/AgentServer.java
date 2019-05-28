package by.touchsoft.chernetski;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class AgentServer extends Thread {

    private boolean connectionWithClient;
    private boolean workingStatus;
    private BufferedReader in;
    private BufferedWriter out;
    private Optional<ClientServer> client;
    private Socket socket;
    private Users users;

    public AgentServer(BufferedReader in, BufferedWriter out, Socket socket, Users users) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.users = users;
        client = Optional.empty();
        connectionWithClient = false;
        workingStatus = true;
    }

    @Override
    public void run() {
        users.addAgent(this);
        String message;
        while (workingStatus) {
            try {
                message = in.readLine();
                if (message.equals("/exit")) {
                    exit();
                    break;
                }
                if (!connectionWithClient) {
                    continue;
                }
                if (client.isPresent()) {
                    client.get().sendMessage(message);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void exit() {
        try {
            workingStatus = false;
            out.write("/exit\n");
            out.flush();
            users.agentExit(this);
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void setClient(Optional<ClientServer> client) {
        this.client = client;
    }

    public void setConnectionWithClient(boolean connectionWithClient) {
        this.connectionWithClient = connectionWithClient;
    }

    public Optional<ClientServer> getClient() {
        return client;
    }
}
