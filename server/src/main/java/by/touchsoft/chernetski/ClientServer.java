package by.touchsoft.chernetski;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class ClientServer extends Thread {

    @Setter
    private boolean connectionStatus;
    private BufferedReader in;
    private BufferedWriter out;
    private List<String> messagesBeforeAgentConnect;
    private Logger logger;
    @Getter
    @Setter
    private Optional<AgentServer> agent;
    @Getter
    private String clientName;
    private Socket socket;
    private Users users;

    public ClientServer(BufferedReader in, BufferedWriter out, Socket socket, Users users, String name, Logger loger) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.users = users;
        this.clientName = name;
        this.logger = loger;
        agent = Optional.empty();
        connectionStatus = false;
        messagesBeforeAgentConnect = new LinkedList<>();
    }

    @Override
    public void run() {
        users.addUser(this);
        String message;
        while (true) {
            try {
                message = in.readLine();
                if (message.equals("/leave")) {
                    if(connectionStatus) {
                        users.disconnectClient(this);
                        continue;
                    } else {
                        continue;
                    }
                }
                if (message.equals("/exit")) {
                    users.userExit(this);
                    exit();
                    break;
                }
                if (connectionStatus) {
                    sendMessages();
                    agent.get().sendMessage(message);
                } else {
                    messagesBeforeAgentConnect.add(message);
                }
            } catch (IOException exception) {
                users.userExit(this);
                logger.error("incorrect exit " + exception.getMessage());
                break;
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
            logger.error(exception.getMessage());
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
