package by.touchsoft.chernetski.restchat.dao.repository;

import by.touchsoft.chernetski.restchat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    void delete(Chat chat);
    Optional<Chat> findById(Long id);
    List<Chat> findByStatus(int status);
    Page<Chat> findAll(Pageable page);

}
