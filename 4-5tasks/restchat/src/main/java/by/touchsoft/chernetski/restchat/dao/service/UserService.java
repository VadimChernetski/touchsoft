package by.touchsoft.chernetski.restchat.dao.service;

import by.touchsoft.chernetski.restchat.dao.repository.UserRepository;
import by.touchsoft.chernetski.restchat.entity.Role;
import by.touchsoft.chernetski.restchat.entity.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private Logger logger;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findByName(name);
        if(user != null) {
            user.setStatus(1);
            userRepository.save(user);
            logger.info(user.getName() + " login");
        }
        return user;
    }

    public Page<User> findByStatus(int status, Pageable page){
        return userRepository.findByStatus(status, page);
    }

    public Page<User> findByRolesAndStatus(Role role, int status, Pageable page){
        return userRepository.findByRolesAndStatus(role, status, page);
    }

    public Page<User> findByRoles(Role role, Pageable page){
        return userRepository.findByRoles(role, page);
    }

    public Page<User> findAll(Pageable page){
        return userRepository.findAll(page);
    }

    public User findByName(String name){
        return userRepository.findByName(name);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User findById(Long id){
        return userRepository.findById(id).get();
    }

    public void delete(User user){
        userRepository.delete(user);
    }
}
