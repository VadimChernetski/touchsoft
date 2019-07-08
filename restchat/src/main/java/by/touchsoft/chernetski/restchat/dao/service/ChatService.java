package by.touchsoft.chernetski.restchat.dao.service;

import by.touchsoft.chernetski.restchat.dao.repository.ChatRepository;
import by.touchsoft.chernetski.restchat.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final String monitor = "Chat service monitor";
    @Autowired
    private ChatRepository chatRepository;

    public Chat findWaitingChatForAgent(){
        Chat chat = null;
        synchronized (monitor){
            List<Chat> chats = chatRepository.findByStatus(1);
            if(!chats.isEmpty()){
                int chatNumber = (int) (Math.random()* (chats.size()));
                chat = chats.remove(chatNumber);
                chat.setStatus(3);
                chatRepository.save(chat);
            }
        }
        return chat;
    }

    public void delete(Chat chat){
        chatRepository.delete(chat);
    }

    public Chat findFirstWaitingChatForClient(){
        Chat chat = null;
        synchronized (monitor){
            List<Chat> chats = chatRepository.findByStatus(2);
            if(!chats.isEmpty()){
                chat = chats.remove(0);
                chat.setStatus(3);
                chatRepository.save(chat);
            }
        }
        return chat;
    }

    public Chat findChatById(long id){
        return chatRepository.findById(id).get();
    }

    public Page findAll(Pageable pageable){
        return chatRepository.findAll(pageable);
    }

    public Chat save(Chat chat){
        return chatRepository.save(chat);
    }
}
