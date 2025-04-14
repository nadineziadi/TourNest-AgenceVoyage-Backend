package com.example.user.repositories;

import com.example.user.entities.Role;
import com.example.user.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {
 //   List<User> findByHebergementId(Long hebergementId);

    @Query("SELECT p.userId FROM User p")
    List<Integer> findAllUserIds();

    List<User> findByHebergementId(Long hebergementId);
    List<User> findByRole(Role role);
    Optional<User> findByEmail(String email);
    Page<User> findByRole(String role, Pageable pageable);// Assure-toi que ton champ email existe bien dans l'entité User
    List<User> findByTeamId(Long teamId);
    Optional<User> findByConfirmationToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.expiryDate < :now")
    void deleteByExpiryDateBefore(LocalDateTime now);
    Optional<User> findByResetToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.expiryDate < :now")
    void deleteByExpiryDateresetBefore(LocalDateTime now);

}
