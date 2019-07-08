package by.touchsoft.chernetski.restchat.entity;


import by.touchsoft.chernetski.restchat.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table (name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "creation_time")
    private String creationTime;
    @Column(name = "context")
    private String context;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @Column(name = "status")
    private int status; //0 - unsent, 1 - sent, -1 - chat was closed before send
    @Transient
    private String name;

    public void generateCreationTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        creationTime = currentTime.format(Constants.TIME_CREATION_PATTERN);
    }

    @Override
    public String toString(){
        return user.getName() + " (" + creationTime + ") " + context + " " + status;
    }
}
