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

/**
 * Endpoint for WebSocket
 *
 * @author Vadim Chernetski
 */
@ServerEndpoint(value = "/chat",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class)
public class ChatEndPoint {

    /**
     * status of connection to server
     */
    private boolean connectionStatus = false;

    /**
     * Instance of Connector class
     */
    private Connector connector;

    /**
     * Instance of Log4j class
     */
    private static Logger logger = Logger.getLogger("webApp");

    /**
     * Instance of Session class
     */
    @Getter
    @Setter
    private Session session;

    /**
     * Open ChatEndPoint
     *
     * @param session - Session that created when ChatEndPoint opens
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    /**
     * Close ChatEndPoint
     */
    @OnClose
    public void onClose() {
        this.session = null;
    }

    /**
     * Send message to the browser
     *
     * @param message - message that will send to the web
     */
    @OnMessage
    public void onMessage(Message message) throws IOException, EncodeException {
        if (connectionStatus) {
            connector.sendMessage(handleMessage(message));
            session.getBasicRemote().sendObject(message);
            if (message.getContext().equals("/exit")) {
                connectionStatus = false;
                connector = null;
            }
        } else {
            registration(message);
        }
    }

    /**
     * Handling error
     *
     * @param e - throwable
     */
    @OnError
    public void onError(Throwable e) {
        logger.error(e.getMessage());
    }

    /**
     * Using for send message from connector
     *
     * @param message - message
     */
    public void sendMessage(Message message) {
        try {
            session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException exception) {
            logger.error(exception.getMessage());
        }
    }

    /**
     * Method create connector
     *
     * @param message - registration message
     */
    private void registration(Message message) {
        if (!message.getName().isEmpty() && !message.getContext().isEmpty()) {
            connector = new Connector(this, logger, message.getContext());
            connector.start();
            connector.sendMessage(message.toString());
            connectionStatus = true;
        }
    }

    /**
     * looking for commands in massage
     *
     * @param message - message
     * @return handled message
     */
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
