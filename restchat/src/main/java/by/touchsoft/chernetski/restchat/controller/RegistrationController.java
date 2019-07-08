package by.touchsoft.chernetski.restchat.controller;

import by.touchsoft.chernetski.restchat.dao.repository.UserRepository;
import by.touchsoft.chernetski.restchat.entity.Role;
import by.touchsoft.chernetski.restchat.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registration/register")
    public String registration(
            @RequestParam(name = "role") String role,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "email") String email){
        User user = new User();
        if(role.equals("agent")){
            user.addRole(Role.AGENT);
        } else {
            user.addRole(Role.CLIENT);
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setEmail(email);
        user.generateRegistrationTime();
        userRepository.save(user);
        return "redirect:/login";
    }
}
