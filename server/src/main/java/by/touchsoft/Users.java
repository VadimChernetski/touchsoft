package by.touchsoft;

import java.util.*;
import java.util.Map.Entry;

public class Users {

    private static final String monitor = "monitor";
    private Map<Server, Server> clients = new HashMap<>();
    private List<Server> freeAgents = new ArrayList<>();

    public int addClient(Server server) {
        synchronized (monitor) {
            if (freeAgents.isEmpty()) {
                clients.put(server, null);
                return -1;
            } else {
                clients.put(server, freeAgents.remove(0));
                return 1;
            }
        }
    }

    public void addAgent(Server server) {
        synchronized (monitor) {
            freeAgents.add(server);
        }
    }

    public boolean addFreeAgents(Server server) {
        synchronized (monitor) {
            Server agent = clients.get(server);
            if (agent == null && !freeAgents.isEmpty()) {
                clients.put(server, freeAgents.remove(0));
                return true;
            }
            return false;
        }
    }

    public Map<Server, Server> getClients() {
        return clients;
    }

    public Server getClient(Server server){
        Server client = null;
        synchronized (monitor){
            for(Entry<Server, Server> entry : clients.entrySet()){
                Server tempClient = entry.getValue();
                if(tempClient !=null && tempClient.equals(server)){
                    client = entry.getKey();
                }
            }
        }
        return client;
    }
}
