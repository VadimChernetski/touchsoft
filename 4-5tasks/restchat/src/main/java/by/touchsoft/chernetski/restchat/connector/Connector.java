package by.touchsoft.chernetski.restchat.connector;

import by.touchsoft.chernetski.restchat.Constants;
import by.touchsoft.chernetski.restchat.dao.service.ChatService;
import by.touchsoft.chernetski.restchat.dao.service.MessageService;
import by.touchsoft.chernetski.restchat.dao.service.UserService;
import by.touchsoft.chernetski.restchat.entity.Chat;
import by.touchsoft.chernetski.restchat.entity.Message;
import by.touchsoft.chernetski.restchat.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Connector extends Thread{

    private ChatService chatService;
    private UserService userService;
    private MessageService messageService;
    private BufferedWriter out;
    private BufferedReader in;
    @Getter
    @Setter
    private Chat chat;
    @Getter
    private List<Message> messages;
    private Socket socket;
    @Getter
    @Setter
    private User user;
    @Getter
    @Setter
    private User companion;
    @Getter
    private Object monitor = new Object();
    private Logger logger;

    public Connector(User user, MessageService messageService, UserService userService, ChatService chatService, Logger logger){
        this.logger = logger;
        this.chatService = chatService;
        this.userService = userService;
        this.messageService = messageService;
        this.user = user;
        user.setConnector(this);
        messages = new LinkedList<>();
        try {
            this.socket = new Socket(Constants.ID, Constants.PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            this.sendMessage("/register " + user.getRoles().stream().findFirst().get().name().toLowerCase() + " " + user.getName());
        } catch (IOException exception){
            logger.error(exception.getMessage());
        }
    }

    @Override
    public void run() {
        Message message;
        String input;
        while (true){
            try{
                chat = user.getOpenChat();
                input = in.readLine();
                if(input.equals("/exit")){
                    exit();
                    break;
                }
                if (input.equalsIgnoreCase("Agent disconnected")
                        || input.equalsIgnoreCase("Client disconnected")
                        || input.equalsIgnoreCase("Agent disconnected, type a message to join another agent")){
                    if(chat != null){
                        chat = chatService.findChatById(chat.getId());
                        chat.setStatus(2);
                        chatService.save(chat);
                        companion = null;
                        user = userService.findById(user.getId());
                        chat = null;
                    }
                }
                message = InputHandler.handle(input, this, chat, messageService, userService, chatService);
                addMessage(message);
            } catch (IOException exception){
                logger.error(exception.getMessage());
            }
        }
    }

    private void addMessage(Message message){
        synchronized (monitor) {
            messages.add(message);
            monitor.notify();
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }

    public List<Message> getMessages(){
        List<Message> forSend;
        synchronized (monitor) {
            forSend = messages;
            messages = new LinkedList<>();
        }
        return forSend;
    }

    private void exit(){
        if(socket != null){
            try {
                socket.close();
            } catch (IOException exception) {
                logger.error(exception.getMessage());
            }
        }
    }
}
