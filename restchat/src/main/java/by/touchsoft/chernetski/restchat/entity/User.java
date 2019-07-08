package by.touchsoft.chernetski.restchat.entity;

import by.touchsoft.chernetski.restchat.Constants;
import by.touchsoft.chernetski.restchat.connector.Connector;
import by.touchsoft.chernetski.restchat.dao.service.MessageService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@EqualsAndHashCode
@Entity
@Table(name = "users")
public class User  implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
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
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Message> messages;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
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

    public void addMessage(Message message){
        synchronized (monitor) {
            if (messages == null) {
                messages = new LinkedList<>();
            }
            messages.add(message);
        }
    }


    public void addRole(Role role){
        if(roles == null){
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public void setStatus(int status){
        synchronized (monitor){
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
}
