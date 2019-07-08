package by.touchsoft.chernetski.restchat.controller;

import by.touchsoft.chernetski.restchat.dao.service.ChatService;
import by.touchsoft.chernetski.restchat.dao.service.MessageService;
import by.touchsoft.chernetski.restchat.dao.service.UserService;
import by.touchsoft.chernetski.restchat.entity.Chat;
import by.touchsoft.chernetski.restchat.entity.Message;
import by.touchsoft.chernetski.restchat.entity.Role;
import by.touchsoft.chernetski.restchat.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Controller
@PreAuthorize("hasAuthority('CLIENT')")
public class ChatClientController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @GetMapping("/client")
    public String client(){
        return "client.html";
    }

    @GetMapping("/client/chat")
    public String chat(@AuthenticationPrincipal User user){
        Chat chat = chatService.findFirstWaitingChatForClient();
        user.setStatus(2);
        if(chat == null){
            chat = new Chat();
            chat.generateCreationTime();
            chat.setStatus(1);
            chat.addUser(user);
            chat.setId(chatService.save(chat).getId());
            user.setChat(chat);
        } else {
            user.setChat(chat);
            User companion = chat.getCompanion(user);
            if(companion != null) {
                companion.setStatus(2);
                userService.save(companion);
            } else {
                chat.setStatus(1);
                chatService.save(chat);
                user.setChat(chat);
            }
        }
        userService.save(user);
        return "/chatClient.html";
    }

    @PostMapping ("/client/chat/post")
    @ResponseStatus(value = HttpStatus.OK)
    public void post(@RequestBody Message message, @AuthenticationPrincipal User user){
        message.generateCreationTime();
        message.setUser(user);
        message.setStatus(0);
        user.addMessage(message);
        message.setId(messageService.save(message).getId());
    }

    @GetMapping("/client/chat/send")
    @ResponseBody
    public List send(@AuthenticationPrincipal User user){
        Chat chat = user.getChat();
        chat = chatService.findChatById(chat.getId());
        user.setChat(chat);
        if(chat.getStatus() != 3){
            user.setChat(chatService.findChatById(user.getChat().getId()));
            return Collections.emptyList();
        } else {
            return findUnsent(user, chat);
        }
    }

    @GetMapping("/client/chat/exit")
    public String exit(@AuthenticationPrincipal User user){
        user.setStatus(1);
        Chat chat = user.getChat();
        if(chat != null) {
            if (chat.getStatus() == 3 && user.getRoles().stream().findFirst().get() == Role.CLIENT) {
                chat.setStatus(2);
                chat.deleteUser(user);
                chatService.save(chat);
            } else {
                chat.setStatus(4);
                chatService.save(chat);
            }
        }
        user.setChat(null);
        userService.save(user);
        return "redirect:/client";
    }

    private List<Message> findUnsent(User user, Chat chat){
        chat = chatService.findChatById(chat.getId());
        user.setChat(chat);
        User companion = chat.getCompanion(user);
        companion = userService.findById(companion.getId());
        List<Message> messages = companion.getMessages();
        List<Message> unsent = new LinkedList<>();
        synchronized (companion.getMonitor()){
            for (Message message: messages){
                if(message.getStatus() == 0){
                    message.setStatus(1);
                    message.setChat(chat);
                    message = messageService.save(message);
                    messageService.save(message);
                    message.setName(companion.getName());
                    unsent.add(message);
                }
            }
        }
        return unsent;
    }
}
