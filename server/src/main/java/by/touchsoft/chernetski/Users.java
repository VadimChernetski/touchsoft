package by.touchsoft.chernetski;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.util.*;

public class Users {

    private static final String monitor = "monitor";
    @Getter @Setter
    private Queue<AgentServer> freeAgents = new LinkedList<>();
    @Getter @Setter
    private Deque<ClientServer> freeClients = new LinkedList<>();
    private Logger logger;

    public Users(Logger logger) {
        this.logger = logger;
    }

    public void addAgent(AgentServer agent) {
        logger.info(agent.getAgentName() + " connected");
        synchronized (monitor) {
            freeAgents.offer(agent);
            monitor.notify();
        }
    }

    public void agentExit(AgentServer agent) {
        logger.info("agent " + agent.getAgentName() + " disconnected");
        Optional<ClientServer> client = agent.getClient();
        if (client.isPresent()) {
            synchronized (monitor) {
                freeClients.addFirst(client.get());
                client.get().setConnectionStatus(false);
                client.get().setAgent(Optional.empty());
                monitor.notify();
            }
            client.get().sendMessage("Agent disconnected\nType a message to join another agent\n");
        }
    }

    public void addClient(ClientServer client) {
        logger.info("client " + client.getClientName() + " connected");
        synchronized (monitor) {
            freeClients.addLast(client);
            monitor.notify();
        }
    }

    public void disconnectClient(ClientServer client) {
        logger.info("client " + client.getClientName() + " disconnected from agent " +
                client.getAgent().get().getAgentName());
        client.setConnectionStatus(false);
        Optional<AgentServer> agent = client.getAgent();
        if (agent.isPresent()) {
            agent.get().sendMessage("Client disconnected\n");
            agent.get().setConnectionStatus(false);
            agent.get().setClient(Optional.empty());
            synchronized (monitor) {
                freeAgents.offer(agent.get());
                freeClients.addLast(client);
                monitor.notify();
            }
        }
    }

    public void clientExit(ClientServer client) {
        logger.info(client.getClientName() + " disconnected");
        Optional<AgentServer> agent = client.getAgent();
        if (agent.isPresent()) {
            agent.get().sendMessage("Client disconnected\n");
            agent.get().setConnectionStatus(false);
            agent.get().setClient(Optional.empty());
            logger.info("agent " + agent.get().getAgentName() + " disconnected from client");
            synchronized (monitor) {
                freeAgents.offer(agent.get());
                monitor.notify();
            }
        }
    }

    public void tryToConnect() {
        AgentServer agent = null;
        ClientServer client = null;
        synchronized (monitor) {
            if (!freeClients.isEmpty() && !freeAgents.isEmpty()) {
                agent = freeAgents.poll();
                client = freeClients.pollFirst();
            } else {
                try {
                    monitor.wait();
                } catch (InterruptedException exception) {
                    logger.error(exception.getMessage());
                }
            }
        }
        if (agent != null && client != null){
            logger.info("client " + client.getClientName() + " connected to agent " + agent.getAgentName());
            agent.setClient(Optional.of(client));
            client.setAgent(Optional.of(agent));
            client.sendMessage("Agent connected\n");
            agent.sendMessage("Client connected\n");
            client.sendMessages();
            agent.setConnectionStatus(true);
            client.setConnectionStatus(true);
        }
    }
}
