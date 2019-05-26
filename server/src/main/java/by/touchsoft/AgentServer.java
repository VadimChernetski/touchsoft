package by.touchsoft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class AgentServer extends Thread {

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
    }

    @Override
    public void run() {
        users.addAgent(this);
        String message;
        client = users.connectAgentToClient(this);
        while (true) {
            try {
                if (!client.isPresent()) {
                    client = users.connectAgentToClient(this);
                }
                message = in.readLine();
                if (message.equals("/exit")) {
                    exit();
                    break;
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
            out.write("/exit\n");
            out.flush();
            if (client.isPresent()) {
                users.agentExit(client.get());
            } else {
                users.agentExit(this);
            }
            out.close();
            in.close();
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void setClient(Optional<ClientServer> client) {
        this.client = client;
    }
}
