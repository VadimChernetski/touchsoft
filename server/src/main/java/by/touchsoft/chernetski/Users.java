package by.touchsoft.chernetski;

import java.util.*;
import java.util.Map.Entry;

public class Users {

    private static final String monitor = "monitor";
    private Map<ClientServer, AgentServer> connections = new HashMap<>();
    private List<AgentServer> freeAgents = new ArrayList<>();
    private Queue<ClientServer> clients = new LinkedList<>();

    public void addClient(ClientServer client) {
        synchronized (monitor) {
            clients.offer(client);
        }
    }

    public Optional<AgentServer> connectClientToAgent(ClientServer client) {
        Optional<AgentServer> agent = Optional.empty();
        synchronized (monitor) {
            AgentServer tempAgent = connections.get(client);
            if(tempAgent != null){
                return Optional.of(tempAgent);
            }
            if (!freeAgents.isEmpty()) {
                agent = Optional.of(freeAgents.get(0));
                connections.put(client, agent.get());
                clients.remove(client);
            }
        }
        if(agent.isPresent()){
            agent.get().setClient(Optional.of(client));
        }
        return agent;
    }

    public void disconnectClient(ClientServer client) {
        synchronized (monitor) {
            clients.offer(client);
            AgentServer agent = connections.get(client);
            freeAgents.add(agent);
            connections.remove(client);
        }
    }

    public void clientExit(ClientServer client) {
        AgentServer agent;
        synchronized (monitor) {
            agent = connections.get(client);
            connections.remove(client);
            freeAgents.add(agent);
        }
        if(agent != null) {
            agent.setClient(Optional.empty());
        }
    }

    public void addAgent(AgentServer agent) {
        synchronized (monitor) {
            freeAgents.add(agent);
        }
    }

    public Optional<ClientServer> connectAgentToClient(AgentServer agent) {
        Optional<ClientServer> client;
        synchronized (monitor) {
            client = checkClientInConnections(agent);
            if (!clients.isEmpty() && !client.isPresent()) {
                client = Optional.of(clients.poll());
                connections.put(client.get(), agent);
                freeAgents.remove(agent);
            }
        }
        if(client.isPresent()){
            client.get().setAgent(Optional.of(agent));
        }
        return client;
    }

    public void agentExit(ClientServer client) {
        synchronized (monitor) {
                connections.remove(client);
                clients.offer(client);
        }
        if(client != null) {
            client.setAgent(Optional.empty());
        }
    }

    public void agentExit(AgentServer agent) {
        synchronized (monitor) {
                freeAgents.remove(agent);
        }
    }

    private Optional<ClientServer> checkClientInConnections (AgentServer agent){
        Optional<ClientServer> client = Optional.empty();
        synchronized (monitor){
            for(Entry<ClientServer, AgentServer> entry : connections.entrySet()){
                if(entry.getValue().equals(agent)){
                    client = Optional.of(entry.getKey());
                }
            }
        }
        return client;
    }
}
