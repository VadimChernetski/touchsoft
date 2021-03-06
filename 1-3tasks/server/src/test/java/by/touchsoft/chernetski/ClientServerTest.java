package by.touchsoft.chernetski;


import by.touchsoft.chernetski.servers.AgentServer;
import by.touchsoft.chernetski.servers.ClientServer;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientServerTest {

    @Test
    void sendMessageShouldWriteOutInputMessageWithNewLineSymbol() {
        String expected = "test message";
        String actual = "";
        File testFile = new File("test.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
             BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
            ClientServer client = new ClientServer(null, writer, null, null, null);
            client.getUsers().setLogger(Logger.getLogger("test"));
            client.sendMessage("test message");
            actual = reader.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (testFile.exists()) {
            testFile.delete();
        }
        assertEquals(expected, actual);
    }

    @Test
    void sendMessagesShouldWriteOutInputMessages() {
        StringBuilder message = new StringBuilder();
        message.append("first message\n");
        message.append("second message\n");
        message.append("third message\n");
        File testFile = new File("test.txt");
        String expected = "first message " +
                "second message " +
                "third message";
        String actual = "";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
             BufferedWriter out = new BufferedWriter(new StringWriter());
             BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
            ClientServer client = new ClientServer(null, out, null, null, null);
            AgentServer agent = new AgentServer(null, writer, null, null, null);
            client.setAgent(Optional.of(agent));
            client.setMessagesBeforeAgentConnect(message);
            client.sendMessages();
            actual = reader.readLine() + " " + reader.readLine() + " " + reader.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (testFile.exists()) {
            testFile.delete();
        }
        assertEquals(expected, actual);
    }

    @Test
    void checkCorrectExit(){
        File testFile = new File("test.txt");
        Logger logger = Logger.getLogger("test");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("/exit\n".getBytes())));
            ClientServer client = new ClientServer(reader, writer, new Socket(), "Test", logger);
            client.start();
            assertTrue(client.isAlive());
            Thread.currentThread().sleep(250);
            assertTrue(!client.isAlive());
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (InterruptedException exception){
            exception.printStackTrace();
        }
        if (testFile.exists()) {
            testFile.delete();
        }
    }
}
