package by.touchsoft.chernetski.websocket.endpoint;

import by.touchsoft.chernetski.connection.connecter.Connector;
import by.touchsoft.chernetski.websocket.decoder.MessageDecoder;
import by.touchsoft.chernetski.websocket.encoder.MessageEncoder;
import by.touchsoft.chernetski.websocket.message.Message;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/chat",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class)
public class ChatEndPoint {

    private boolean connectionStatus = false;
    private Connector connector;
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnClose
    public void onClose(Session session) {

    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        if (connectionStatus) {
            connector.sendMessage(handleMessage(message));
            session.getBasicRemote().sendObject(message);
        } else {
            registration(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        e.printStackTrace();
    }

    public void sendMessage(Message message) {
        try {
            this.session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException exception) {
            exception.printStackTrace();
        }
    }

    private void registration(Message message) {
        connector = new Connector(this);
        connector.start();
        connector.sendMessage(message.toString());
        connectionStatus = true;
    }

    private String handleMessage(Message message) {
        String result;
        String context = message.getContext();
        switch (context) {
            case "/exit":
                result = "/exit";
                break;
            case "/leave":
                result = "/leave";
                break;
            default:
                message.writeInTime();
                result = message.toString();
                break;
        }
        return result;
    }
}
