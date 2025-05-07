package com.denizcan.stocktrackingsystem.controller;

import com.denizcan.stocktrackingsystem.model.User;
import com.denizcan.stocktrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String profilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findByUsername(currentUsername).orElse(null);

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute User updatedUser,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);

        if (currentUser != null) {
            // Profil bilgilerini güncelle
            currentUser.setFullName(updatedUser.getFullName());
            currentUser.setEmail(updatedUser.getEmail());

            // Şifre değiştirme isteği varsa
            if (newPassword != null && !newPassword.isBlank()) {
                if (currentPassword == null || !passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Mevcut şifreniz yanlış!");
                    return "redirect:/profile";
                }
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Yeni şifreler eşleşmiyor!");
                    return "redirect:/profile";
                }
                currentUser.setPassword(passwordEncoder.encode(newPassword));
            }

            userRepository.save(currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profil bilgileriniz başarıyla güncellendi.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Kullanıcı bulunamadı.");
        }

        return "redirect:/profile";
    }
} 