package by.touchsoft.chernetski.restchat.controller;

import by.touchsoft.chernetski.restchat.dao.dto.ChatDTO;
import by.touchsoft.chernetski.restchat.dao.dto.UserDTO;
import by.touchsoft.chernetski.restchat.dao.service.ChatService;
import by.touchsoft.chernetski.restchat.dao.service.UserService;
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

@Controller
@RequestMapping("/agent")
@PreAuthorize("hasAuthority('AGENT')")
public class AgentController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

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
    public Page<UserDTO> allUsers(@RequestParam(name = "status") String status,
                                  @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<UserDTO> users;
        int userStatus = Integer.valueOf(status);
        if(userStatus == -1){
            users = UserDTO.getUsersPage(userService.findAll(pageable), pageable);
        } else {
            users = UserDTO.getUsersPage(userService.findByStatus(userStatus, pageable), pageable);
        }
        return users;
    }
    @GetMapping("/agents/show")
    @ResponseBody
    public Page<UserDTO> allAgents(@RequestParam(name = "status") String status,
                                @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<UserDTO> agents;
        int agentStatus = Integer.valueOf(status);
        if(agentStatus == -1){
            agents = UserDTO.getUsersPage(userService.findByRoles(Role.AGENT, pageable), pageable);
        } else {
            agents = UserDTO.getUsersPage(userService.findByRolesAndStatus(Role.AGENT, Integer.parseInt(status), pageable), pageable);
        }
        return agents;
    }
    @GetMapping("/clients/show")
    @ResponseBody
    public Page<UserDTO> allClients(@RequestParam(name = "status") String status,
                                @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<UserDTO> clients;
        int agentStatus = Integer.valueOf(status);
        if(agentStatus == -1){
            clients = UserDTO.getUsersPage(userService.findByRoles(Role.CLIENT, pageable), pageable);
        } else {
            clients = UserDTO.getUsersPage(userService.findByRolesAndStatus(Role.CLIENT, Integer.parseInt(status), pageable), pageable);
        }
        return clients;
    }

    @GetMapping("/clients/free")
    @ResponseBody
    public long freeClients(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<User> freeClients = userService.findByRolesAndStatus(Role.CLIENT, 1, pageable);
        return freeClients.getTotalElements();
    }

    @GetMapping("/agents/free")
    @ResponseBody
    public long freeAgents(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<User> freeClients = userService.findByRolesAndStatus(Role.AGENT, 1, pageable);
        return freeClients.getTotalElements();
    }

    @GetMapping("/all/{id}")
    @ResponseBody
    public UserDTO details(@PathVariable(name = "id") String id){
        return UserDTO.createUserDTO(userService.findById(Long.valueOf(id)));
    }

    @GetMapping("/chats/show")
    @ResponseBody
    public Page findAllChats(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return ChatDTO.getChatPage(chatService.findAll(pageable), pageable);
    }

    @GetMapping("/chats/{id}")
    @ResponseBody
    public ChatDTO findChat(@PathVariable(name = "id") String id){
        return ChatDTO.createChatDTO(chatService.findChatById(Long.valueOf(id)));
    }
}
