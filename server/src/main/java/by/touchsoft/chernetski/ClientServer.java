package by.touchsoft.chernetski;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ClientServer extends Thread {

    private boolean connectionWithAgent;
    private boolean workingStatus;
    private BufferedReader in;
    private BufferedWriter out;
    private List<String> messagesBeforeAgentConnect;
    private Optional<AgentServer> agent;
    private Socket socket;
    private Users users;

    public ClientServer(BufferedReader in, BufferedWriter out, Socket socket, Users users) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.users = users;
        agent = Optional.empty();
        connectionWithAgent = false;
        messagesBeforeAgentConnect = new LinkedList<>();
        workingStatus = true;
    }

    @Override
    public void run() {
        users.addClient(this);
        String message;
        while (workingStatus) {
            try {
                message = in.readLine();
                if (message.equals("/leave")) {
                    users.disconnectClient(this);
                    continue;
                }
                if (message.equals("/exit")) {
                    users.clientExit(this);
                    exit();
                    break;
                }
                if (connectionWithAgent) {
                    sendMessages();
                    agent.get().sendMessage(message);
                } else {
                    messagesBeforeAgentConnect.add(message);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void sendMessages() {
        StringBuilder history = new StringBuilder();
        if (!messagesBeforeAgentConnect.isEmpty()) {
            for (String message : messagesBeforeAgentConnect) {
                history.append(message).append("\n");
            }
            messagesBeforeAgentConnect.clear();
            agent.get().sendMessage(history.toString());
        }
    }

    private void exit() {
        try {
            workingStatus = false;
            out.write("/exit\n");
            out.flush();
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

    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void setAgent(Optional<AgentServer> agent) {
        this.agent = agent;
    }

    public void setConnectionWithAgent(boolean connectionWithAgent) {
        this.connectionWithAgent = connectionWithAgent;
    }

    public Optional<AgentServer> getAgent() {
        return agent;
    }
}
