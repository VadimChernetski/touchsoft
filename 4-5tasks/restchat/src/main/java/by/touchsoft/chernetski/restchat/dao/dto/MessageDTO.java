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

import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@EnableTransactionManagement
public class MessageDTO {

    private long id;
    private String creationTime;
    private String context;
    private UserDTO user;
    private ChatDTO chat;

    @Transactional
    public static MessageDTO createMessageDto(Message message){
        MessageDTO messageDTO= new MessageDTO();
        UserDTO userDTO = new UserDTO();
        ChatDTO chatDTO = new ChatDTO();
        User user = message.getUser();
        Chat chat = message.getChat();
        messageDTO.id = message.getId();
        messageDTO.creationTime = message.getCreationTime();
        messageDTO.context = message.getContext();
        userDTO.setId(user.getId());
        userDTO.setRoles(user.getRoles());
        userDTO.setRegistrationTime(user.getRegistrationTime());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        messageDTO.setUser(userDTO);
        chatDTO.setCreationTime(chat.getCreationTime());
        chatDTO.setId(chat.getId());
        chatDTO.setStatus(chat.getStatus());
        return messageDTO;
    }

    @Transactional
    public static Page<MessageDTO> getMessagesPage(Page<Message> messages, Pageable pageable){
        int totalElements = (int) messages.getTotalElements();
        List<MessageDTO> messageDTOs = new LinkedList<>();
        messages.forEach(message -> messageDTOs.add(createMessageDto(message)));
        return new PageImpl<MessageDTO>(messageDTOs, pageable, totalElements);
    }
}
