package by.touchsoft.chernetski.restchat.entity;

import by.touchsoft.chernetski.restchat.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    private List<Message> messages;
    @Column(name = "creation_time")
    private String creationTime;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_chats",
            joinColumns = {@JoinColumn(name = "chat_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> users = new HashSet<>();
    @Column(name = "status")
    private int status; //1 - started, 2 - closed
    @Transient
    private final String monitor = "chat monitor";

    public void addMessage(Message message) {
        synchronized (monitor) {
            if (messages == null) {
                messages = new LinkedList<>();
            }
            messages.add(message);
        }
    }

    public void generateCreationTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        creationTime = currentTime.format(Constants.TIME_CREATION_PATTERN);
    }

    public void addUser(User user) {
        synchronized (monitor) {
            users.add(user);
            user.getChats().add(this);
        }
    }

    public void setStatus(int status) {
        synchronized (monitor) {
            this.status = status;
        }
    }

    public int getStatus() {
        synchronized (monitor) {
            return status;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return id == chat.id &&
                status == chat.status &&
                creationTime.equals(chat.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationTime, status);
    }
}
