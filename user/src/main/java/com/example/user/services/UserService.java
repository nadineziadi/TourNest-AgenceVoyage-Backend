package com.example.user.services;

import com.example.user.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.user.entities.User;
import com.example.user.repositories.UserRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String assignUserToHebergement(Long userId, Long hebergementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User non trouvé avec id: " + userId));

        // Assigner simplement l'ID de l'hébergement
        user.setHebergementId(hebergementId);

        userRepository.save(user);
        return "Utilisateur " + userId + " affecté à l'hébergement " + hebergementId;
    }

    @Override
    public List<Integer> getAllUserIds() {
        return userRepository.findAllUserIds();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));
    }
    public List<User> getUsersByHebergement(Long hebergementId) {
        return userRepository.findByHebergementId(hebergementId);
    }
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User saveUser(User user) {
        return null;
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return null;
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.empty();
    }



    @Override
    public Page<User> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Override
    public String generateConfirmationToken() {
        return null;
    }

    @Override
    public User findByConfirmationToken(String token) {
        return null;
    }

    @Override
    public User saveUser2(User user) {
        return null;
    }

    @Override
    public void initiatePasswordReset(String email) {

    }

    @Override
    public User findByResetToken(String token) {
        return null;
    }

    @Override
    public void resetPassword(String token, String newPassword) {

    }
}
