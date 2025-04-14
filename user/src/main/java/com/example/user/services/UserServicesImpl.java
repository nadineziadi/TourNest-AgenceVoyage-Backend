package com.example.user.services;

import com.example.user.entities.Role;
import com.example.user.entities.User;
import com.example.user.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServicesImpl implements IUserService {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public LocalDateTime calculateExpiryDate(int expiryTimeInHours) {
        return LocalDateTime.now().plusHours(expiryTimeInHours);
    }

        @Autowired
    public void UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User saveUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword()); // Hacher le mot de passe
        user.setPassword(hashedPassword); // Remplacer le mot de passe en clair par le mot de passe haché
        System.out.println("Mot de passe en clair : " + user.getPassword());
        System.out.println("Mot de passe haché : " + hashedPassword);
        return userRepository.save(user); // Sauvegarder l'utilisateur dans la base de données
    }



    @Transactional
    public User saveUser2(User user) {
        System.out.println("Mot de passe en clair : " + user.getPassword());
        return userRepository.save(user); // Sauvegarder l'utilisateur dans la base de données
    }


    @Override
    public List<Integer> getAllUserIds() {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public String assignUserToHebergement(Long userId, Long hebergementId) {
        return null;
    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId);
        }
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

     @Override
    public String generateConfirmationToken() {
        return UUID.randomUUID().toString();
    }
    @Override
    public User findByConfirmationToken(String token) {
        return userRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Your account has been deleted due to inactivity. " +
                        "The confirmation token has expired, and you did not confirm your account within the required time. " +
                        "Please register again to create a new account."));
    }


    public Page<User> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }
    public Page<User> getUsersByRole(String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByRole(role, pageable);
    }

    // New methods for password reset
    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        User user = findByEmail(email);
        if (user == null) {
            throw new RuntimeException("No user found with email: " + email);
        }

        // Generate reset token and set expiry (e.g., 1 hour)
        String resetToken = generateResetToken();
        user.setResetToken(resetToken);
        user.setResetExpiryDate(calculateExpiryDate(1)); // Token expires in 1 hour

        // Save the user with the reset token
        saveUser(user);
    }

    public User findByResetToken(String token) {
        return userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token. Please request a new password reset."));
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = findByResetToken(token);

        // Check if the token has expired
        if (user.getResetExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("The reset token has expired. Please request a new password reset.");
        }

        // Update the password
        user.setPassword(newPassword);
        user.setResetToken(null);
        user.setResetExpiryDate(null);

        // Save the updated user
        saveUser(user);
    }
}
