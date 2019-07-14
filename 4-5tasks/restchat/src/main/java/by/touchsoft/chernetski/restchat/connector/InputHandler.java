package by.touchsoft.chernetski.restchat.connector;

import by.touchsoft.chernetski.restchat.dao.service.ChatService;
import by.touchsoft.chernetski.restchat.dao.service.MessageService;
import by.touchsoft.chernetski.restchat.dao.service.UserService;
import by.touchsoft.chernetski.restchat.entity.Chat;
import by.touchsoft.chernetski.restchat.entity.Message;
import by.touchsoft.chernetski.restchat.entity.User;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InputHandler {

    private static final Pattern ID_PATTERN = Pattern.compile("\\([0-9]+\\)");
    private static final Pattern TIME_PATTERN = Pattern.compile("\\([0-9: ]+\\)[:]");

    public static Message handle(String input, Connector connector, Chat chat, MessageService messageService, UserService userService, ChatService chatService) {
        Message message = new Message();
        String id;
        String time;
        Matcher matcher = TIME_PATTERN.matcher(input);
        User companion;
        if (matcher.find()) {
            time = matcher.group();
            input = input.replace(time, "").trim();
            time = time.replaceAll("[\\(\\)]", "");
            message.setCreationTime(time.substring(0, time.length() - 1));
        } else {
            message.generateCreationTime();
            message.setName("server");
            message.setContext(input);
            return message;
        }
        matcher = ID_PATTERN.matcher(input);
        if (matcher.find()) {
            id = matcher.group();
            input = input.replace(id, "");
            companion = userService.findById(Long.valueOf(id.replaceAll("[\\(\\)]", "")));
            message.setUser(companion);
            if (chat == null) {
                chat = createChat(connector.getUser(), companion, chatService, userService, connector);
                connector.setChat(chat);
            }
        } else {
            companion = new User();
            companion.setId(-1);
            connector.setCompanion(companion);
            if (chat == null) {
                chat = createChat(connector.getUser(), companion, chatService, userService, connector);
                connector.setChat(chat);
            }
        }
        input = input.replace("():", "");
        String[] parts = input.split(" ");
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if(i != 0){
                context.append(parts[i]);
            }
        }
        message.setName(parts[0]);
        message.setContext(context.toString());
        message.setChat(chat);
        messageService.save(message);
        return message;
    }

    private static Chat createChat(User user, User companion, ChatService chatService, UserService userService, Connector connector) {
        Chat chat;
        if (companion.getOpenChat() != null && user.getOpenChat() == null) {
            chat = companion.getOpenChat();
            user.addChat(chat);
            user.setStatus(2);
            user = userService.save(user);
            connector.setUser(user);
        } else if (user.getOpenChat() != null && companion.getOpenChat() == null) {
            chat = user.getOpenChat();
            if (companion.getId() != -1) {
                companion.addChat(chat);
                companion = userService.save(companion);
                connector.setCompanion(companion);
            }
        } else {
            chat = new Chat();
            chat.setStatus(1);
            chat.addUser(user);
            chat.generateCreationTime();
            if (companion != null && companion.getOpenChat() != null) {
                chat.addUser(companion);
                connector.setCompanion(companion);
            }
            chat = chatService.save(chat);
        }
        return chat;
    }
}
