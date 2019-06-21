package by.touchsoft.chernetski.servers;

import by.touchsoft.chernetski.connection.Users;
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

/**
 * Class that works with client's messages
 * @author Vadim Chernetski
 */
public class ClientServer extends Thread implements UserServer {

    /** Field that displays connection with agent */
    @Setter
    private boolean connectionStatus;
    /** Stream receiving messages */
    private BufferedReader in;
    /** Stream sending messages */
    private BufferedWriter out;
    /** All messages that wasn't sent until client didn't connect to agent */
    @Setter
    private List<String> messagesBeforeAgentConnect;
    /** Log4j logger */
    private Logger logger;
    /** Instance of agent */
    @Getter @Setter
    private Optional<AgentServer> agent;
    /** Name of current client */
    @Getter
    private String clientName;
    /** Socket for interaction with chat application */
    private Socket socket;
    /** Instance of Users class */
    private Users users;

    public ClientServer(BufferedReader in, BufferedWriter out, Socket socket, Users users, String name, Logger loger) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.users = users;
        this.clientName = name;
        this.logger = loger;
        agent = Optional.empty();                       //Для читаемости кода лучше инициализировать
        connectionStatus = false;                       //эти 3 поля в полях класса,
        messagesBeforeAgentConnect = new LinkedList<>();// а не в конструкторе
    }

    /**
     * Method for starting thread
     */
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
                    exit();
                    break;
                }
                if (connectionStatus) {
                    sendMessages();
                    agent.get().sendMessage(message);
                } else {
                    users.addUser(this);
                    messagesBeforeAgentConnect.add(message);
                }
            } catch (IOException exception) {
                users.userExit(this);
                logger.error("incorrect exit " + exception.getMessage());
                break;
            }
        }
    }

    /**
     * Method sends all missed messages
     */
    public void sendMessages() {    //зачем сохранять сообщения в ArrayList что бы потом сделать из него StringBuilder?
        StringBuilder history = new StringBuilder();    //если сразу сохранять в StringBuilder, можно избежать
        if (!messagesBeforeAgentConnect.isEmpty()) {    //лишних строк кода и ненужных операций
            int messagesCount = messagesBeforeAgentConnect.size();
            for (int i = 0; i < messagesCount; i++) {
                if(i == messagesCount - 1){
                    history.append(messagesBeforeAgentConnect.get(i));
                } else {
                    history.append(messagesBeforeAgentConnect.get(i)).append("\n");
                }
            }
            messagesBeforeAgentConnect.clear();
            history.subSequence(history.length()-2, history.length()-1); // эта строчка вообще неопнятно зачем
            agent.get().sendMessage(history.toString());
        }
    }

    /**
     * Method sends message to companion
     * @param message - context of message
     */
    @Override
    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }

    /**
     * Method that stops client
     */
    private void exit() {
        try {
            out.write("/exit\n");
            out.flush();
            users.userExit(this);
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (!socket.isClosed()) {
                socket.close(); //закрытие сокета влечет за собой закрытие IO ресурсов.
            }           // JavaDoc: Closing this socket will also close the socket's InputStream and OutputStream
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
