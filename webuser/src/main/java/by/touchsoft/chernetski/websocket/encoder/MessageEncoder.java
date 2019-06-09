package by.touchsoft.chernetski.websocket.encoder;

import by.touchsoft.chernetski.websocket.message.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

    private Gson json = new GsonBuilder().create();

    @Override
    public String encode(Message message) {
        return json.toJson(message);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
