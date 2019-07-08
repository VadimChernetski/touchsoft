package by.touchsoft.chernetski.restchat.connector;

import by.touchsoft.chernetski.restchat.dao.service.UserService;
import by.touchsoft.chernetski.restchat.entity.Chat;
import by.touchsoft.chernetski.restchat.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

public class Connector extends Thread{

    @Autowired
    private UserService userService;

    private User agent;
    private User client;
    private Chat chat;

    public void setAgent(User agent){
        this.agent = agent;
        if(chat == null){
            chat = agent.getChat();
        }
    }
    public void setClient(User client){
        this.client = client;
        if(chat == null){
            chat = client.getChat();
        }
    }
}
