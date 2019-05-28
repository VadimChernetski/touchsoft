package by.touchsoft.chernetski;

import java.util.*;

public class Users {

    private static final String monitor = "monitor";
    private Queue<AgentServer> freeAgents = new LinkedList<>();
    private Deque<ClientServer> freeClients = new LinkedList<>();


    public void addAgent(AgentServer agent) {
        synchronized (monitor) {
            freeAgents.offer(agent);
            monitor.notify();
        }
    }

    public void agentExit(AgentServer agent) {
        Optional<ClientServer> client = agent.getClient();
        if (client.isPresent()) {
            synchronized (monitor) {
                freeClients.addFirst(client.get());
                client.get().setConnectionWithAgent(false);
                client.get().setAgent(Optional.empty());
                monitor.notify();
            }
            client.get().sendMessage("Agent disconnected\nType a message to join another agent\n");
        }
    }

    public void addClient(ClientServer client) {
        synchronized (monitor) {
            freeClients.addLast(client);
            monitor.notify();
        }
    }

    public void disconnectClient(ClientServer client) {
        client.setConnectionWithAgent(false);
        Optional<AgentServer> agent = client.getAgent();
        if (agent.isPresent()) {
            agent.get().sendMessage("Client disconnected\n");
            agent.get().setConnectionWithClient(false);
            agent.get().setClient(Optional.empty());
            synchronized (monitor) {
                freeAgents.offer(agent.get());
                freeClients.addLast(client);
                monitor.notify();
            }
        }
    }

    public void clientExit(ClientServer client) {
        Optional<AgentServer> agent = client.getAgent();
        if (agent.isPresent()) {
            agent.get().sendMessage("Client disconnected\n");
            agent.get().setConnectionWithClient(false);
            agent.get().setClient(Optional.empty());
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

                }
            }
        }
        if (agent != null && client != null){
            agent.setClient(Optional.of(client));
            client.setAgent(Optional.of(agent));
            client.sendMessage("Agent connected\n");
            agent.sendMessage("Client connected\n");
            client.sendMessages();
            agent.setConnectionWithClient(true);
            client.setConnectionWithAgent(true);
        }
    }
}
