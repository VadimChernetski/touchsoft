package by.touchsoft.chernetski.connection;

import by.touchsoft.chernetski.servers.AgentServer;
import by.touchsoft.chernetski.servers.ClientServer;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Class contains queue of agents and users and connects/disconnects them
 * @author Vadim Chernetski
 */
public class Users {

    /** Instance of Users class */
    private static volatile Users instance;

    /** Monitor that used for thread safe operations */
    private static final String monitor = "monitor";

    /** Queue of free agents */
    @Getter @Setter
    private Queue<AgentServer> freeAgents = new LinkedList<>();

    /** Queue of clients */
    @Getter @Setter
    private Deque<ClientServer> freeClients = new LinkedList<>();

    /** Log4j instance */
    @Setter
    private Logger logger;

    {
        Thread connector = new Thread(() -> {
            while(true) {
                this.tryToConnect();
            }
        });
        connector.setUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage()));
        connector.start();
    }

    /**
     * Constructor
     * @param logger - Log4j instance
     */
    private Users(Logger logger) {
        this.logger = logger;
    }

    public static Users getInstance(Logger logger){
        if(instance == null){
            synchronized (Users.class){
                if(instance == null){
                    instance = new Users(logger);
                }
            }
        }
        return instance;
    }

    /**
     * Method adds agent to the queue
     * @param agent - AgentServer instance
     * @throws IllegalArgumentException if agent is null
     */
    public void addUser(AgentServer agent) {
        if (agent == null) {
            throw new IllegalArgumentException();
        }
        logger.info(agent.getAgentName() + " connected");
        synchronized (monitor) {
            if(!freeAgents.contains(agent)) {
                freeAgents.offer(agent);
                monitor.notify();
            }
        }
    }

    /**
     * Method puts agent's companion to the queue if it's present
     * @param agent - AgentServer that stops working
     * @throws IllegalArgumentException if agent is null
     */
    public void userExit(AgentServer agent) {
        if (agent == null) {
            throw new IllegalArgumentException();
        }
        logger.info("agent " + agent.getAgentName() + " disconnected");
        Optional<ClientServer> client = agent.getClient();
        synchronized (monitor) {
            if (client.isPresent()) {
                freeClients.addFirst(client.get());
                client.get().setConnectionStatus(false);
                client.get().setAgent(Optional.empty());
                client.get().sendMessage("Agent disconnected\nType a message to join another agent");
            } else {
                freeAgents.remove(agent);
            }
            monitor.notify();
        }
    }

    /**
     * Method adds client to the queue
     * @param client - ClientServer instance
     * @throws IllegalArgumentException if client is null
     */
    public void addUser(ClientServer client) {
        if (client == null) {
            throw new IllegalArgumentException();
        }
        logger.info("client " + client.getClientName() + " connected");
        synchronized (monitor) {
            if (!freeClients.contains(client)) {
                freeClients.addLast(client);
                monitor.notify();
            }
        }
    }

    /**
     * Method disconnects client form agent
     * @param client - client that will be disconnected from agent (if agent is)
     * @throws IllegalArgumentException if client is null
     */
    public void disconnectClient(ClientServer client) {
        if (client == null) {
            throw new IllegalArgumentException();
        }
        logger.info("client " + client.getClientName() + " disconnected from agent " +
                client.getAgent().get().getAgentName());
        client.setConnectionStatus(false);
        Optional<AgentServer> agent = client.getAgent();
        if (agent.isPresent()) {
            client.setAgent(Optional.empty());
            agent.get().sendMessage("Client disconnected");
            agent.get().setConnectionStatus(false);
            agent.get().setClient(Optional.empty());
            client.setInQueue(false);
            synchronized (monitor) {
                freeAgents.offer(agent.get());
                monitor.notify();
            }
        }
    }

    /**
     * Method puts client's companion to the queue if it's present
     * @param client - ClientServer that stops working
     * @throws IllegalArgumentException if client is null
     */
    public void userExit(ClientServer client) {
        if (client == null) {
            throw new IllegalArgumentException();
        }
        logger.info(client.getClientName() + " disconnected");
        Optional<AgentServer> agent = client.getAgent();
        synchronized (monitor) {
            if (agent.isPresent()) {
                agent.get().sendMessage("Client disconnected");
                agent.get().setConnectionStatus(false);
                agent.get().setClient(Optional.empty());
                logger.info("agent " + agent.get().getAgentName() + " disconnected from client");
                freeAgents.offer(agent.get());
            } else {
                freeClients.remove(client);
            }
            monitor.notify();
        }
    }

    /**
     * Method checks queues. If in queues there are client(s) and agent(s) connects them.
     */
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
        if (agent != null && client != null) {
            logger.info("client " + client.getClientName() + " connected to agent " + agent.getAgentName());
            agent.setClient(Optional.of(client));
            client.setAgent(Optional.of(agent));
            client.sendMessage("Agent connected");
            agent.sendMessage("Client connected");
            client.sendMessages();
            agent.setConnectionStatus(true);
            client.setConnectionStatus(true);
        }
    }
}
