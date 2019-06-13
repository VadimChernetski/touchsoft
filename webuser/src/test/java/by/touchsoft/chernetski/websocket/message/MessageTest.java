package by.touchsoft.chernetski.websocket.message;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageTest {

    @Test
    void writeInTimeShouldAddCurrentTimeToContext(){
        Message message = new Message("Some Context", "Name");
        LocalTime time = LocalTime.now();
        String context = message.getContext();
        String expected = "(" + time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "): " + context;
        message.writeInTime();
        String actual = message.getContext();
        assertEquals(expected, actual);
    }
}
