package by.touchsoft.chernetski.restchat.entity;

import by.touchsoft.chernetski.restchat.Constants;
import by.touchsoft.chernetski.restchat.connector.Connector;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    @Column(name = "reg_time")
    private String registrationTime;
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Message> messages;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_chats",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "chat_id")})
    private Set<Chat> chats = new HashSet<>();
    @Column(name = "status")
    private int status; //0 - user offline, 1 - user online, 2 - user in chat
    @Transient
    private Connector connector;
    @Transient
    private final String monitor = "monitor";

    public void generateRegistrationTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        registrationTime = currentTime.format(Constants.TIME_CREATION_PATTERN);
    }

    public void addMessage(Message message) {
        synchronized (monitor) {
            if (messages == null) {
                messages = new LinkedList<>();
            }
            messages.add(message);
        }
    }

    public Chat getOpenChat() {
        Chat openChat = null;
        if (chats != null) {
            openChat = chats.stream().filter(chat -> chat.getStatus() == 1).findFirst().orElse(null);
        }
        return openChat;
    }

    public void addChat(Chat chat) {
        synchronized (monitor) {
            chats.add(chat);
            chat.getUsers().add(this);
        }
    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public void setStatus(int status) {
        synchronized (monitor) {
            this.status = status;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                status == user.status &&
                registrationTime.equals(user.registrationTime) &&
                email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registrationTime, email, status);
    }
}
