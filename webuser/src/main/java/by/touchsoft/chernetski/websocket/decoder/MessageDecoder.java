package by.touchsoft.chernetski.websocket.decoder;

import by.touchsoft.chernetski.websocket.message.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * Implementation of javax.websocket.Decoder
 * @author Vadim Chernetski
 */
public class MessageDecoder implements Decoder.Text<Message> {

    /** Gson instance */
    private Gson json = new GsonBuilder().create();

    @Override
    public Message decode(String s){
        return json.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s) {
        return s != null && !s.isEmpty();
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
