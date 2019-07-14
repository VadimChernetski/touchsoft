package by.touchsoft.chernetski.connection.connector;

import by.touchsoft.chernetski.connection.connecter.Connector;
import by.touchsoft.chernetski.websocket.message.Message;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConnectorTest {

    @Test
    void sendMessageShouldWriteOutInputMessageWithNewLineSymbol(){
        Connector connector = new Connector();
        Message message = new Message("Some context", "Test");
        message.writeInTime();
        String expected = message.toString();
        String actual;
        File testFile = new File("test.txt");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
            BufferedReader reader = new BufferedReader(new FileReader(testFile))){
            connector.setOut(writer);
            connector.sendMessage(message.toString());
            actual = reader.readLine();
            assertEquals(expected, actual);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (testFile.exists()) {
            testFile.delete();
        }
    }
}
