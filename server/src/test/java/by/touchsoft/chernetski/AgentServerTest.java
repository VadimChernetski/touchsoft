package by.touchsoft.chernetski;

import by.touchsoft.chernetski.connection.Users;
import by.touchsoft.chernetski.servers.AgentServer;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentServerTest {

    @Test
    void sendMessageShouldWriteOutInputMessageWithNewLineSymbol() {
        String expected = "test message";
        String actual = "";
        File testFile = new File("test.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
             BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
            AgentServer agent = new AgentServer(null, writer, null, null, null, null);
            agent.sendMessage("test message");
            actual = reader.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (testFile.exists()) {
            testFile.delete();
        }
        assertEquals(expected, actual);
    }
}
