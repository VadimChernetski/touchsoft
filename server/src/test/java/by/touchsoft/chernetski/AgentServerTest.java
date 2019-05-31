package by.touchsoft.chernetski;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgentServerTest {

    @Test
    void sendMessageShouldWriteOutInputMessageWithNewLineSymbol() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        AgentServer agent = new AgentServer(null, out,null, null, null, null);
        agent.sendMessage("test String");
        String expected = "test String\n";
        String actual = stringWriter.toString();
        assertEquals(expected,actual);
    }
}
