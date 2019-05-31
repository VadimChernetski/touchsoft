package by.touchsoft.chernetski;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientServerTest {

    @Test
    void sendMessageShouldWriteOutInputMessageWithNewLineSymbol(){
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        ClientServer client = new ClientServer(null, out, null, null, null, null);
        client.sendMessage("test String");
        String expected = "test String\n";
        String actual = stringWriter.toString();
        assertEquals(expected,actual);
    }
}
