package com.example.user.configuration;

import com.example.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TokenCleanupTask {

    @Autowired
    private UserRepository userRepository;

    // Exécute la tâche toutes les minutes (ajustez selon vos besoins)
    @Scheduled(cron = "0 * * * * ?") // Toutes les minutes
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        userRepository.deleteByExpiryDateBefore(now); // Supprime les utilisateurs expirés
        System.out.println("Expired users cleanup executed at: " + now);
    }
}
