package by.touchsoft.chernetski.restchat.controller;

import by.touchsoft.chernetski.restchat.connector.Connector;
import by.touchsoft.chernetski.restchat.dao.service.ChatService;
import by.touchsoft.chernetski.restchat.dao.service.MessageService;
import by.touchsoft.chernetski.restchat.dao.service.UserService;
import by.touchsoft.chernetski.restchat.entity.Message;
import by.touchsoft.chernetski.restchat.entity.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("hasAuthority('CLIENT')")
public class ChatClientController {

    @Autowired
    private Logger logger;

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
        User temp = userService.findById(user.getId());
        temp.setStatus(2);
        temp = userService.save(temp);
        Connector connector = new Connector(temp, messageService, userService, chatService, logger);
        connector.start();
        user.setConnector(connector);
        logger.info(user.getName() + " connected to chat");
        return "/chatClient.html";
    }

    @PostMapping ("/client/chat/post")
    @ResponseStatus(value = HttpStatus.OK)
    public void sendMessage(@RequestBody Message message, @AuthenticationPrincipal User user){
        message.setUser(user);
        message.generateCreationTime();
        message.setName(user.getName());
        user.getConnector().sendMessage(message.toString());
    }

    @GetMapping("/client/chat/send")
    @ResponseBody
    public List recieveMessage(@AuthenticationPrincipal User user){
        List<Message> messages = user.getConnector().getMessages();
        if (messages.size() == 0){
            synchronized (user.getConnector().getMonitor()) {
                try {
                    user.getConnector().getMonitor().wait(20000);
                    messages = user.getConnector().getMessages();
                } catch (InterruptedException exception) {
                    logger.error(exception.getMessage());
                }
            }
        }
        return messages;
    }

    @GetMapping("/client/chat/exit")
    public String exit(@AuthenticationPrincipal User user){
        user.getConnector().sendMessage("/exit");
        user = userService.findById(user.getId());
        user.setStatus(1);
        userService.save(user);
        return "redirect:/client";
    }

    @GetMapping("/client/chat/leave")
    public String leave(@AuthenticationPrincipal User user){
        user.getConnector().sendMessage("/leave");
        user = userService.findById(user.getId());
        user.setStatus(1);
        userService.save(user);
        logger.info(user.getName() + " left chat");
        return "redirect:/client/chat";
    }
}
