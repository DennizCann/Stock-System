package com.denizcan.stocktrackingsystem.config;

import com.denizcan.stocktrackingsystem.model.User;
import com.denizcan.stocktrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Admin kullanıcısının varlığını kontrol et
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFullName("Sistem Yöneticisi");
            adminUser.setEmail("admin@example.com");
            adminUser.setRole("ROLE_ADMIN");
            adminUser.setActive(true);
            
            userRepository.save(adminUser);
            
            System.out.println("Admin kullanıcısı oluşturuldu.");
        }
    }
} 