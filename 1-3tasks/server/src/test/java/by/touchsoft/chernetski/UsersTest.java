package by.touchsoft.chernetski;

import by.touchsoft.chernetski.connection.Users;
import by.touchsoft.chernetski.servers.AgentServer;
import by.touchsoft.chernetski.servers.ClientServer;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class UsersTest {

    static Users users;

    @BeforeAll
    public static void createUsers(){
        users = Users.getInstance(Logger.getLogger("test"));
    }

    @BeforeEach
    public void initUsers() {
        users.setFreeAgents(new LinkedList<>());
        users.setFreeClients(new LinkedList<>());
    }

    @Test
    void addUserShouldPutAgentToQueue() {
        try (BufferedWriter out = new BufferedWriter(new StringWriter())) {
            AgentServer agent = new AgentServer(null, out, null, "Cooper", Logger.getLogger("test"));
            Queue<AgentServer> expected = new LinkedList<>();
            expected.offer(agent);
            users.addUser(agent);
            Queue<AgentServer> actual = users.getFreeAgents();
            assertEquals(expected, actual);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void addUserShouldPutClientToQueue() {
        try (BufferedWriter out = new BufferedWriter(new StringWriter())) {
            ClientServer client = new ClientServer(null, out, null, "Alice", Logger.getLogger("test"));
            Deque<ClientServer> expected = new LinkedList<>();
            expected.addLast(client);
            users.addUser(client);
            Deque<ClientServer> actual = users.getFreeClients();
            assertEquals(expected, actual);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void addUserShouldThrowIllegalArgumentExceptionIfAgentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> users.addUser((AgentServer) null));
    }

    @Test
    void addUserShouldThrowIllegalArgumentExceptionIfClientIsNull() {
        assertThrows(IllegalArgumentException.class, () -> users.addUser((ClientServer) null));
    }

    @Test
    void exitUserShouldPutClientToQueue() {
        BufferedWriter out = new BufferedWriter(new StringWriter());
        ClientServer client = new ClientServer(null, out, null, "Cooper", null);
        AgentServer agent = new AgentServer(null, out, null, "Alice", null);
        agent.setClient(Optional.of(client));
        Deque<ClientServer> expected = new LinkedList<>();
        expected.addFirst(client);
        users.userExit(agent);
        Deque<ClientServer> actual = users.getFreeClients();
        assertAll(() -> assertEquals(expected, actual),
                () -> assertEquals(Optional.empty(), client.getAgent()));
    }

    @Test
    void exitUserShouldPutAgentToDeque() {
        BufferedWriter out = new BufferedWriter(new StringWriter());
        ClientServer client = new ClientServer(null, out, null, "Cooper", null);
        AgentServer agent = new AgentServer(null, out, null, "Alice", null);
        client.setAgent(Optional.of(agent));
        Queue<AgentServer> expected = new LinkedList<>();
        expected.offer(agent);
        users.userExit(client);
        Queue<AgentServer> actual = users.getFreeAgents();
        assertAll(() -> assertEquals(expected, actual),
                () -> assertEquals(Optional.empty(), agent.getClient()));
    }

    @Test
    void exitUserShouldThrowIllegalArgumentExceptionIfAgentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> users.userExit((AgentServer) null));
    }

    @Test
    void exitUserShouldThrowIllegalArgumentExceptionIClientIsNull() {
        assertThrows(IllegalArgumentException.class, () -> users.userExit((ClientServer) null));
    }

    @Test
    void exitUserShouldNotPutAgentToDequeIfItIsNull() {
        BufferedWriter out = new BufferedWriter(new StringWriter());
        ClientServer client = new ClientServer(null, out, null, "Cooper", null);
        Queue<AgentServer> expected = new LinkedList<>();
        users.userExit(client);
        Queue<AgentServer> actual = users.getFreeAgents();
        assertEquals(expected, actual);
    }

    @Test
    void exitUserShouldNotPutClientToQueueIfItIsNull() {
        BufferedWriter out = new BufferedWriter(new StringWriter());
        AgentServer agent = new AgentServer(null, out, null, "Alice", null);
        Deque<ClientServer> expected = new LinkedList<>();
        users.userExit(agent);
        Deque<ClientServer> actual = users.getFreeClients();
        assertEquals(expected, actual);
    }

    @Test
    void disconnectClientShouldDissconectClientFromAgent() {
        BufferedWriter out = new BufferedWriter(new StringWriter());
        ClientServer client = new ClientServer(null, out, null,"Cooper", null);
        AgentServer agent = new AgentServer(null, out, null, "Alice", null);
        client.setAgent(Optional.of(agent));
        agent.setClient(Optional.of(client));
        Queue<AgentServer> expectedQueue = new LinkedList<>();
        expectedQueue.offer(agent);
        users.disconnectClient(client);
        Queue<AgentServer> actualQueue = users.getFreeAgents();
        assertEquals(expectedQueue, actualQueue);
    }

    @Test
    void disconnectClientShouldThrowIllegalArgumentExceptionIfClientNull() {
        assertThrows(IllegalArgumentException.class, () -> users.disconnectClient(null));
    }

    @Test
    void tryToConnectShouldConnectAgentAndClientIfTheyFree() {
        BufferedWriter out = new BufferedWriter(new StringWriter());
        ClientServer client = new ClientServer(null, out, null, "Cooper", null);
        AgentServer agent = new AgentServer(null, out, null, "Alice", null);
        users.addUser(agent);
        users.addUser(client);
        users.tryToConnect();
        assertEquals(client.getAgent().get(), agent);
        assertEquals(agent.getClient().get(), client);
    }
}
