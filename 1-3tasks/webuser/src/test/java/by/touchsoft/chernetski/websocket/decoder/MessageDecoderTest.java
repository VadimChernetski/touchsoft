package by.touchsoft.chernetski.websocket.decoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import by.touchsoft.chernetski.websocket.message.Message;
import org.junit.jupiter.api.Test;

public class MessageDecoderTest {

    MessageDecoder decoder = new MessageDecoder();

    @Test
    void youShouldGetMessageFromJsonString(){
        String json = "{\"context\":\"test context\",\"name\":\"Test\"}";
        Message expected = new Message();
        expected.setContext("test context");
        expected.setName("Test");
        Message actual = decoder.decode(json);
        assertEquals(expected, actual);
    }
}
