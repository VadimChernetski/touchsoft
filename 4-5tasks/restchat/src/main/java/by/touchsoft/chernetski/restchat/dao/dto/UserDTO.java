package by.touchsoft.chernetski.restchat.dao.dto;

import by.touchsoft.chernetski.restchat.entity.Chat;
import by.touchsoft.chernetski.restchat.entity.Message;
import by.touchsoft.chernetski.restchat.entity.Role;
import by.touchsoft.chernetski.restchat.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@EnableTransactionManagement
public class UserDTO {

    private long id;
    private Set<Role> roles;
    private String registrationTime;
    private String email;
    private String name;
    private List<MessageDTO> messages;
    private Set<ChatDTO> chats = new HashSet<>();
    private int status;

    @Transactional
    public static UserDTO createUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.id = user.getId();
        userDTO.roles = user.getRoles();
        userDTO.registrationTime = user.getRegistrationTime();
        userDTO.email = user.getEmail();
        userDTO.name = user.getName();
        userDTO.messages = getMessages(user);
        userDTO.chats = getChats(user);
        return userDTO;
    }

    @Transactional
    public static Page<UserDTO> getUsersPage(Page<User> users, Pageable pageable){
        int totalElements = (int) users.getTotalElements();
        List<UserDTO> userDTOs = new ArrayList<>();
        users.forEach(user -> userDTOs.add(createUserDTO(user)));
        return new PageImpl<UserDTO>(userDTOs, pageable, totalElements);
    }

    private static List<MessageDTO> getMessages(User user){
        List<Message> messages = user.getMessages();
        List<MessageDTO> messageDTOs = new ArrayList<>();
        messages.forEach(message -> {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(message.getId());
            messageDTO.setCreationTime(message.getCreationTime());
            messageDTO.setContext(message.getContext());
            messageDTOs.add(messageDTO);
        });
        return messageDTOs;
    }

    private static Set<ChatDTO> getChats(User user){
        Set<Chat> chats = user.getChats();
        Set<ChatDTO> chatDTOs = new HashSet<>();
        chats.forEach(chat -> {
            ChatDTO chatDTO = new ChatDTO();
            chatDTO.setId(chat.getId());
            chatDTO.setStatus(chat.getStatus());
            chatDTO.setCreationTime(chat.getCreationTime());
            chatDTOs.add(chatDTO);
        });
        return chatDTOs;
    }
}
