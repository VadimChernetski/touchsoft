package by.touchsoft.chernetski.websocket.decoder;

import by.touchsoft.chernetski.websocket.message.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {

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
