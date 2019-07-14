package by.touchsoft.chernetski.restchat.dao.dto;

import by.touchsoft.chernetski.restchat.entity.Chat;
import by.touchsoft.chernetski.restchat.entity.Message;
import by.touchsoft.chernetski.restchat.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Setter
@Getter
@EnableTransactionManagement
public class ChatDTO {

    private long id;
    private List<MessageDTO> messages;
    private String creationTime;
    private Set<UserDTO> users = new HashSet<>();
    private int status;

    @Transactional
    public static ChatDTO createChatDTO(Chat chat){
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.id = chat.getId();
        chatDTO.creationTime = chat.getCreationTime();
        chatDTO.status = chat.getStatus();
        chatDTO.messages = getMessages(chat);
        chatDTO.users = getUsers(chat);
        return chatDTO;
    }

    @Transactional
    public static Page<ChatDTO> getChatPage(Page<Chat> chats, Pageable pageable){
        int totalElements = (int) chats.getTotalElements();
        List<ChatDTO> chatDTOs = new ArrayList<>();
        chats.forEach(chat -> chatDTOs.add(createChatDTO(chat)));
        return new PageImpl<ChatDTO>(chatDTOs, pageable, totalElements);
    }

    private static List<MessageDTO> getMessages (Chat chat){
        List<Message> messages = chat.getMessages();
        List<MessageDTO> messageDTOs = new LinkedList<>();
        messages.forEach(message -> {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(message.getId());
            messageDTO.setCreationTime(message.getCreationTime());
            messageDTO.setContext(message.getContext());
            messageDTOs.add(messageDTO);
        });
        return messageDTOs;
    }

    private static Set<UserDTO> getUsers(Chat chat){
        Set<User> users = chat.getUsers();
        Set<UserDTO> userDTOs = new HashSet<>();
        users.forEach(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setRegistrationTime(user.getRegistrationTime());
            userDTO.setStatus(user.getStatus());
            userDTOs.add(userDTO);
        });
        return userDTOs;
    }
}
