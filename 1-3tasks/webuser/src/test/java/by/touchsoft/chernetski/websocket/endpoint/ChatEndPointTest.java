package by.touchsoft.chernetski.websocket.endpoint;

import by.touchsoft.chernetski.websocket.message.Message;
import org.junit.jupiter.api.Test;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ChatEndPointTest {

    @Test
    void sendMessageTest() throws IOException, EncodeException {
        ChatEndPoint endPoint = new ChatEndPoint();
        Session session = mock(Session.class);
        Basic basic = mock(Basic.class);
        endPoint.setSession(session);
        when(session.getBasicRemote()).thenReturn(basic);
        endPoint.sendMessage(null);
        verify(session, times(1)).getBasicRemote();
        verify(session, never()).addMessageHandler(Message.class, Message::writeInTime);
        verify(basic, times(1)).sendObject(null);
    }
}
