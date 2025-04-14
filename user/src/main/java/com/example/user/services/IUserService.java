package com.example.user.services;

import com.example.user.entities.Role;
import com.example.user.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface IUserService {
    List<Integer> getAllUserIds();
    List<User> getAllUsers();
    public String assignUserToHebergement(Long userId, Long hebergementId);
    User addUser(User user);
    User saveUser(User user);
    List<User> getUsersByRole(Role role);
    void deleteUser(Long userId);

    User findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<User> findUserById(Long id);
    User getUserById(Long id);

    Page<User> getUsers(int page, int size);

    //List<User> getUsersByTeam(Long teamId);
    String generateConfirmationToken();
    User findByConfirmationToken(String token);

    User saveUser2(User user);

    void initiatePasswordReset(String email);
    User findByResetToken(String token);
    void resetPassword(String token, String newPassword);

}
