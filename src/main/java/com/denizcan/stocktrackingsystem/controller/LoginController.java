package com.denizcan.stocktrackingsystem.controller;

import com.denizcan.stocktrackingsystem.model.User;
import com.denizcan.stocktrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        // Şifreyi hashleme
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Varsayılan olarak kullanıcı rolü atama
        user.setRole("ROLE_USER");
        
        // Kullanıcıyı kaydetme
        userRepository.save(user);
        
        return "redirect:/login?registered";
    }
} 