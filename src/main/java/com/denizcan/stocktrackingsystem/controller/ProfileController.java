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
        // Mevcut kullanıcı bilgilerini al
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findByUsername(currentUsername).orElse(null);

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser, RedirectAttributes redirectAttributes) {
        // Mevcut kullanıcıyı al
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);

        if (currentUser != null) {
            // Güncellenemeyecek alanlar
            updatedUser.setId(currentUser.getId());
            updatedUser.setUsername(currentUser.getUsername());
            updatedUser.setPassword(currentUser.getPassword());
            updatedUser.setRole(currentUser.getRole());
            updatedUser.setActive(currentUser.isActive());
            
            // Güncellenebilir alanlar
            currentUser.setFullName(updatedUser.getFullName());
            currentUser.setEmail(updatedUser.getEmail());
            
            userRepository.save(currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profil bilgileriniz başarıyla güncellendi.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Kullanıcı bulunamadı.");
        }
        
        return "redirect:/profile";
    }
    
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findByUsername(currentUsername).orElse(null);
        
        if (user != null) {
            // Mevcut şifreyi kontrol et
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("passwordError", "Mevcut şifre doğru değil.");
                return "redirect:/profile";
            }
            
            // Yeni şifre ve onay şifresini kontrol et
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("passwordError", "Yeni şifre ve onay şifresi eşleşmiyor.");
                return "redirect:/profile";
            }
            
            // Şifreyi güncelle
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("passwordSuccess", "Şifreniz başarıyla değiştirildi.");
        } else {
            redirectAttributes.addFlashAttribute("passwordError", "Kullanıcı bulunamadı.");
        }
        
        return "redirect:/profile";
    }
} 