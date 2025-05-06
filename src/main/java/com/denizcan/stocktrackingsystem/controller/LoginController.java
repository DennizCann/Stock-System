package com.denizcan.stocktrackingsystem.controller;

import com.denizcan.stocktrackingsystem.model.User;
import com.denizcan.stocktrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam("role") String role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        userRepository.save(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/register-auth")
    public String registerAuthPage(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("error", "Yeni hesap oluşturmak için admin olarak giriş yapmalısınız.");
            return "redirect:/login";
        }
        return "register-auth";
    }

    @PostMapping("/register-auth")
    public String registerAuth(@RequestParam String adminUsername,
                               @RequestParam String adminPassword,
                               RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(adminUsername, adminPassword)
            );
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/register";
            } else {
                redirectAttributes.addFlashAttribute("error", "Sadece admin kullanıcı yeni hesap oluşturabilir.");
                return "redirect:/register-auth";
            }
        } catch (AuthenticationException e) {
            redirectAttributes.addFlashAttribute("error", "Admin doğrulama başarısız!");
            return "redirect:/register-auth";
        }
    }
} 