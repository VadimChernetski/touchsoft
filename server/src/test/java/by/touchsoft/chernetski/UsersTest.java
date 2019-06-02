package by.touchsoft.chernetski;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class UsersTest {

    Users users;

    @BeforeEach
    public void initUsers() {
        users = new Users(Logger.getLogger("server"));
        users.setFreeAgents(new LinkedList<>());
        users.setFreeClients(new LinkedList<>());
    }

    @Test
    void addUserShouldPutAgentToQueue() {
        AgentServer agent = new AgentServer(null, null, null, null, "Cooper", null);
        Queue<AgentServer> expected = new LinkedList<>();
        expected.offer(agent);
        users.addUser(agent);
        Queue<AgentServer> actual = users.getFreeAgents();
        assertEquals(expected, actual);
    }

    @Test
    void addUserShouldPutClientToQueue() {
        ClientServer client = new ClientServer(null, null, null, null, "Alice", null);
        Deque<ClientServer> expected = new LinkedList<>();
        expected.addLast(client);
        users.addUser(client);
        Deque<ClientServer> actual = users.getFreeClients();
        assertEquals(expected, actual);
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
        ClientServer client = new ClientServer(null, out, null, null, "Cooper", null);
        AgentServer agent = new AgentServer(null, out, null, null, "Alice", null);
        agent.setClient(Optional.of(client));
        Deque<ClientServer> expected = new LinkedList<>();
        expected.addFirst(client);
        users.userExit(agent);
        Deque<ClientServer> actual = users.getFreeClients();
        assertAll(() -> assertEquals(expected, actual),
                () -> assertEquals(Optional.empty(), client.getAgent()));
    }

    @Test
    void exitUserShouldPutAgentToDeque(){
        BufferedWriter out = new BufferedWriter(new StringWriter());
        ClientServer client = new ClientServer(null, out, null, null, "Cooper", null);
        AgentServer agent = new AgentServer(null, out, null, null, "Alice", null);
        client.setAgent(Optional.of(agent));
        Queue<AgentServer> expected = new LinkedList<>();
        expected.offer(agent);
        users.userExit(client);
        Queue<AgentServer> actual = users.getFreeAgents();
        assertAll(() -> assertEquals(expected, actual),
                () -> assertEquals(Optional.empty(), agent.getClient()));
    }

    @Test
    void exitUserShouldThrowIllegalArgumentExceptionIfAgentIsNull(){
        assertThrows(IllegalArgumentException.class, () -> users.userExit((AgentServer)null));
    }

    @Test
    void exitUserShouldThrowIllegalArgumentExceptionIClientIsNull(){
        assertThrows(IllegalArgumentException.class, () -> users.userExit((ClientServer) null));
    }

    @Test
    void exitUserShouldNotPutAgentToDequeIfItIsNull(){
        BufferedWriter out = new BufferedWriter(new StringWriter());
        ClientServer client = new ClientServer(null, out, null, null, "Cooper", null);
        Queue<AgentServer> expected = new LinkedList<>();
        users.userExit(client);
        Queue<AgentServer> actual = users.getFreeAgents();
        assertEquals(expected, actual);
    }

    @Test
    void exitUserShouldNotPutClientToQueueIfItIsNull() {
        BufferedWriter out = new BufferedWriter(new StringWriter());
        AgentServer agent = new AgentServer(null, out, null, null, "Alice", null);
        Deque<ClientServer> expected = new LinkedList<>();
        users.userExit(agent);
        Deque<ClientServer> actual = users.getFreeClients();
        assertEquals(expected, actual);
    }

    @Test
    void disconnectClientShouldPutClientAndAgentToCollections(){
        BufferedWriter out = new BufferedWriter(new StringWriter());
        ClientServer client = new ClientServer(null, out, null, null, "Cooper", null);
        AgentServer agent = new AgentServer(null, out, null, null, "Alice", null);
        client.setAgent(Optional.of(agent));
        agent.setClient(Optional.of(client));
        Queue<AgentServer> expectedQueue = new LinkedList<>();
        Deque<ClientServer> expectedDeque = new LinkedList<>();
        expectedDeque.addLast(client);
        expectedQueue.offer(agent);
        users.disconnectClient(client);
        Queue<AgentServer> actualQueue = users.getFreeAgents();
        Deque<ClientServer> actualDeque = users.getFreeClients();
        assertAll(() -> assertEquals(expectedDeque, actualDeque),
                  () -> assertEquals(expectedQueue, actualQueue));
    }

    @Test
    void disconnectClientShouldThrowIllegalArgumentExceptionIfClientNull(){
        assertThrows(IllegalArgumentException.class, () -> users.disconnectClient(null));
    }

    @Test
    void tryToConnectShouldConnectAgentAndClientIfTheyFree(){
        BufferedWriter out = new BufferedWriter(new StringWriter());
        ClientServer client = new ClientServer(null, out, null, null, "Cooper", null);
        AgentServer agent = new AgentServer(null, out, null, null, "Alice", null);
        users.addUser(agent);
        users.addUser(client);
        users.tryToConnect();
        assertEquals(client.getAgent().get(), agent);
        assertEquals(agent.getClient().get(), client);
    }
}
