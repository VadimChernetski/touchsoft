package by.touchsoft.chernetski.restchat.dao.service;

import by.touchsoft.chernetski.restchat.dao.repository.ChatRepository;
import by.touchsoft.chernetski.restchat.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public void delete(Chat chat){
        chatRepository.delete(chat);
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
