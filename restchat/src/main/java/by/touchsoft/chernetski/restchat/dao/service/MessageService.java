package by.touchsoft.chernetski.restchat.dao.service;

import by.touchsoft.chernetski.restchat.dao.repository.MessageRepository;
import by.touchsoft.chernetski.restchat.entity.Chat;
import by.touchsoft.chernetski.restchat.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message save(Message message){
        return messageRepository.save(message);
    }

    public Page findAll(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }

    public List<Message> findByChat(Chat chat){
        return messageRepository.findByChat(chat);
    }
}
