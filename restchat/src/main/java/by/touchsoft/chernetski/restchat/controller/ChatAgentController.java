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
@RequestMapping("/agent")
@PreAuthorize("hasAuthority('AGENT')")
public class ChatAgentController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @GetMapping("/chat")
    public String chat(@AuthenticationPrincipal User user){
        Chat chat = chatService.findWaitingChatForAgent();
        user.setStatus(2);
        if(chat == null){
            chat = new Chat();
            chat.generateCreationTime();
            chat.addUser(user);
            chat.setStatus(2);
            chat.setId(chatService.save(chat).getId());
            user.setChat(chat);
        } else {
            user.setChat(chat);
            User companion = chat.getCompanion(user);
            companion.setStatus(2);
            userService.save(companion);
        }
        userService.save(user);
        return "/chatAgent.html";
    }

    @PostMapping ("/chat/post")
    @ResponseStatus(value = HttpStatus.OK)
    public void takeMessage(@RequestBody Message message, @AuthenticationPrincipal User user){
        Chat chat = user.getChat();
        if(chat == null){

        }
        chat = chatService.findChatById(chat.getId());
        user.setChat(chat);
        if(chat.getStatus() == 2){
            message.setStatus(-1);
        }
        message.generateCreationTime();
        message.setUser(user);
        user.addMessage(message);
        message.setId(messageService.save(message).getId());
    }

    @GetMapping("/chat/send")
    @ResponseBody
    public List send(@AuthenticationPrincipal User user){
        Chat chat = user.getChat();
        if(chat == null){
            chat = chatService.findWaitingChatForAgent();
        } else {
            chat = chatService.findChatById(chat.getId());
        }
        user.setChat(chat);
        if(chat.getStatus() != 3){
            Chat temp = chatService.findWaitingChatForAgent();
            if(temp != null){
                chatService.delete(chat);
                chat = temp;
            }
            user.setChat(chat);
            userService.save(user);
            return Collections.emptyList();
        } else {
            return findUnsent(user, chat);
        }
    }

    @GetMapping("/chat/exit")
    public String exit(@AuthenticationPrincipal User user){
        user.setStatus(1);
        Chat chat = user.getChat();
        if(chat != null) {
            if (chat.getStatus() == 3 && user.getRoles().stream().findFirst().get() == Role.CLIENT) {
                chat.setStatus(2);
                chat.deleteUser(user);
                chatService.save(chat);
            } else if (chat.getStatus() == 3 && user.getRoles().stream().findFirst().get() == Role.AGENT) {
                chat.setStatus(1);
                chat.deleteUser(user);
                chatService.save(chat);
            } else {
                chat.setStatus(4);
                chatService.save(chat);
            }
        }
        user.setChat(null);
        userService.save(user);
        return "redirect:/agent";
    }

    private List findUnsent(User user, Chat chat){
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
                    messageService.save(message);
                    message.setName(companion.getName());
                    unsent.add(message);
                }
            }
        }
        return unsent;
    }
}
