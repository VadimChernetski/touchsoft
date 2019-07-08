package by.touchsoft.chernetski.restchat.dao.repository;

import by.touchsoft.chernetski.restchat.entity.Chat;
import by.touchsoft.chernetski.restchat.entity.Message;
import by.touchsoft.chernetski.restchat.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findMessageByUser(User user);
    Page<Message> findAll(Pageable page);
    List<Message> findByUserAndStatus(User user, int status);
    List<Message> findByChat(Chat chat);
}
