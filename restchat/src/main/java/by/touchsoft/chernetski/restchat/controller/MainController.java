package by.touchsoft.chernetski.restchat.controller;

import by.touchsoft.chernetski.restchat.dao.repository.UserRepository;
import by.touchsoft.chernetski.restchat.entity.Role;
import by.touchsoft.chernetski.restchat.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal User user){
        if (user == null){
            return "redirect:/login";
        } else {
            if( user.getRoles().contains(Role.AGENT)){
                return "redirect:/agent";
            } else {
                return "redirect:/client";
            }
        }
    }

    @GetMapping("/login")
    public String login (@AuthenticationPrincipal User user){
        if(user != null) {
            return "redirect:/home";
        } else {
            return "login.html";
        }
    }
}
