package by.touchsoft.chernetski.websocket.encoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import by.touchsoft.chernetski.websocket.message.Message;
import org.junit.jupiter.api.Test;

public class MessageEncoderTest {

    MessageEncoder encoder = new MessageEncoder();

    @Test
    void youShouldGetStringJson(){
        Message message = new Message("test context", "Test");
        String expected = "{\"context\":\"test context\",\"name\":\"Test\"}";
        String actual = encoder.encode(message);
        assertEquals(expected, actual);
    }
}
