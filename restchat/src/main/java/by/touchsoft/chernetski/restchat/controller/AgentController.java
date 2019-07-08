package by.touchsoft.chernetski.restchat.controller;

import by.touchsoft.chernetski.restchat.dao.repository.UserRepository;
import by.touchsoft.chernetski.restchat.dao.service.ChatService;
import by.touchsoft.chernetski.restchat.dao.service.MessageService;
import by.touchsoft.chernetski.restchat.entity.Chat;
import by.touchsoft.chernetski.restchat.entity.Message;
import by.touchsoft.chernetski.restchat.entity.Role;
import by.touchsoft.chernetski.restchat.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/agent")
@PreAuthorize("hasAuthority('AGENT')")
public class AgentController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String mainAgent(){
        return "agent.html";
    }

    @GetMapping("/all")
    public String gentAll(){
        return "/all.html";
    }

    @GetMapping("/agents")
    public String agents(){
        return "/agents.html";
    }

    @GetMapping("/chats")
    public String chats(){
        return "/chats.html";
    }

    @GetMapping("/clients")
    public String agentCLients(){
        return "/clients.html";
    }

    @GetMapping("/all/show")
    @ResponseBody
    public Page<User> allUsers(@RequestParam(name = "status") String status,
                               @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<User> users;
        int userStatus = Integer.valueOf(status);
        if(userStatus == -1){
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByStatus(userStatus, pageable);
        }
        return users;
    }
    @GetMapping("/agents/show")
    @ResponseBody
    public Page<User> allAgents(@RequestParam(name = "status") String status,
                                @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<User> agents;
        int agentStatus = Integer.valueOf(status);
        if(agentStatus == -1){
            agents = userRepository.findByRoles(Role.AGENT, pageable);
        } else {
            agents = userRepository.findByRolesAndStatus(Role.AGENT, Integer.parseInt(status), pageable);
        }
        return agents;
    }
    @GetMapping("/clients/show")
    @ResponseBody
    public Page<User> allClients(@RequestParam(name = "status") String status,
                                @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<User> clients;
        int agentStatus = Integer.valueOf(status);
        if(agentStatus == -1){
            clients = userRepository.findByRoles(Role.CLIENT, pageable);
        } else {
            clients = userRepository.findByRolesAndStatus(Role.CLIENT, Integer.parseInt(status), pageable);
        }
        return clients;
    }

    @GetMapping("/clients/free")
    @ResponseBody
    public int freeClients(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<User> freeClients = userRepository.findByRolesAndStatus(Role.CLIENT, 1, pageable);
        return freeClients.getTotalPages();
    }

    @GetMapping("/agents/free")
    @ResponseBody
    public int freeAgents(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<User> freeClients = userRepository.findByRolesAndStatus(Role.AGENT, 1, pageable);
        return freeClients.getTotalPages();
    }

    @GetMapping("/all/{id}")
    @ResponseBody
    public User details(@PathVariable(name = "id") String id){
        return userRepository.findById(Long.valueOf(id)).get();
    }

    @GetMapping("/chats/show")
    @ResponseBody
    public Page findAllChats(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return chatService.findAll(pageable);
    }

    @GetMapping("/chats/{id}")
    @ResponseBody
    public Chat findChat(@PathVariable(name = "id") String id){
        Chat chat = chatService.findChatById(Long.valueOf(id));
        List<Message> messages = messageService.findByChat(chat);
        List<User> users = userRepository.findByChat(chat);
        chat.setMessages(messages);
        chat.setUsers(users);
        return chat;
    }
}
