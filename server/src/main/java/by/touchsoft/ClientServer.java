package by.touchsoft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ClientServer  extends Thread{

    private BufferedReader in;
    private BufferedWriter out;
    private Optional<AgentServer> agent;
    private Socket socket;
    private Users users;

    public ClientServer(BufferedReader in, BufferedWriter out, Socket socket, Users users) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.users = users;
        agent = Optional.empty();
    }

    @Override
    public void run() {
        users.addClient(this);
        String message;
        List<String> messagesBeforeAgentConnect = new LinkedList<>();
        agent = users.connectClientToAgent(this);
        while(true){
            try{
                message = in.readLine();
                if(message.equals("/leave")){
                    disconnectFromAgent();
                    continue;
                }
                if(message.equals("/exit")){
                    exit();
                    break;
                }
                if(agent.isPresent()) {
                    sendMessages(messagesBeforeAgentConnect);
                    agent.get().sendMessage(message);
                } else {
                    messagesBeforeAgentConnect.add(message);
                    agent = users.connectClientToAgent(this);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void sendMessages (List<String> messagesBeforeAgentConnect) {
        StringBuilder history = new StringBuilder();
        if (!messagesBeforeAgentConnect.isEmpty()) {
            for (String message : messagesBeforeAgentConnect) {
                history.append(message).append("\n");
            }
            messagesBeforeAgentConnect.clear();
            agent.get().sendMessage(history.toString());
        }
    }

    private void disconnectFromAgent(){
        users.disconnectClient(this);
        agent = Optional.empty();
    }

    private void exit(){
        try{
            out.write("/exit\n");
            out.flush();
            users.clientExit(this);
            if(out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }

    public void sendMessage(String message){
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
}
