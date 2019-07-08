package by.touchsoft.chernetski.restchat.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    AGENT, CLIENT;

    @Override
    public String getAuthority() {
        return name();
    }
}
