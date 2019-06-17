package by.touchsoft.chernetski.websocket.endpoint;

import by.touchsoft.chernetski.connection.connecter.Connector;
import by.touchsoft.chernetski.websocket.decoder.MessageDecoder;
import by.touchsoft.chernetski.websocket.encoder.MessageEncoder;
import by.touchsoft.chernetski.websocket.message.Message;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/chat",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class)
public class ChatEndPoint {

    private boolean connectionStatus = false;
    private Connector connector;
    private Logger logger = Logger.getLogger("webApp");
    @Getter @Setter
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnClose
    public void onClose(Session session) {
        session = null;
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        if (connectionStatus) {
            connector.sendMessage(handleMessage(message));
            session.getBasicRemote().sendObject(message);
            if(message.getContext().equals("/exit")){
                connectionStatus = false;
                connector = null;
            }
        } else {
            registration(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        logger.error(e.getMessage());
    }

    public void sendMessage(Message message) {
        try {
            session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException exception) {
            logger.error(exception.getMessage());
        }
    }

    private void registration(Message message) {
        if(checkRegistrationData(message)) {
            connector = new Connector(this, logger, message.getContext());
            connector.start();
            connector.sendMessage(message.toString());
            connectionStatus = true;
        }
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

    private boolean checkRegistrationData(Message message){
        String input = message.toString();
        return input.matches("/register (agent|client) [A-z0-9]*");
    }
}
