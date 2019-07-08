package by.touchsoft.chernetski.restchat.entity;

import by.touchsoft.chernetski.restchat.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    private List<User> users;
    @Column(name = "status")
    private int status; //1 - waiting for agent, 2 - waiting for client, 3 - chat started, 4 - closed
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
            if (users == null) {
                users = new ArrayList<>();
            }
            users.add(user);
        }
    }
    public void deleteUser(User user){
        synchronized (monitor){
            users.remove(user);
        }
    }
    public User getCompanion(User user) {
        User companion = null;
        for (User temp : users) {
            if (temp.getRoles().stream().findFirst().get() == user.getRoles().stream().findFirst().get()) {
                continue;
            } else {
                companion = temp;
            }
        }
        return companion;
    }

    public void setStatus(int status){
        synchronized (monitor){
            this.status = status;
        }
    }

    public int getStatus(){
        synchronized (monitor){
            return status;
        }
    }
}
