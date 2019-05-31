package by.touchsoft.chernetski;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class AgentServer extends Thread {

    @Setter
    private boolean connectionStatus;
    private BufferedReader in;
    private BufferedWriter out;
    private Logger logger;
    @Setter @Getter
    private Optional<ClientServer> client;
    @Getter
    private String agentName;
    private Socket socket;
    private Users users;

    public AgentServer(BufferedReader in, BufferedWriter out, Socket socket, Users users, String name, Logger logger) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.users = users;
        this.agentName = name;
        this.logger = logger;
        client = Optional.empty();
        connectionStatus = false;
    }

    @Override
    public void run() {
        users.addAgent(this);
        String message;
        while (true) {
            try {
                message = in.readLine();
                if (message.equals("/exit")) {
                    exit();
                    break;
                }
                if (!connectionStatus) {
                    continue;
                }
                if (client.isPresent()) {
                    client.get().sendMessage(message);
                }
            } catch (IOException exception) {
                users.agentExit(this);
                logger.error("incorrect exit " + exception.getMessage());
                break;
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
            logger.error(exception.getMessage());
        }
    }

    private void exit() {
        try {
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
            logger.info(agentName + " exit");
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
