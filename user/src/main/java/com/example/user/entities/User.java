package com.example.user.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;


    @NotBlank(message = "First name is required")
    private String firstname;

    @NotBlank(message = "Last name is required")
    private String lastname;

    @Email(message = "Email must be valid")
    @Column(unique = true, nullable = false)
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "The password must contain at least 8 characters, including one uppercase letter, one lowercase letter, one digit, and one special character.")
    private String password;

    @Pattern(regexp = "^[0-9]{8}$", message = "Phone number must contain exactly 8 digits")
    private String phone;



    @Enumerated(EnumType.STRING)
    private Role role;

    private String status = "Not Activated";

    private Long teamId;

    private String confirmationToken;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "reset_token", nullable = true)
    private String resetToken;

    @Column(name = "reset_expiry_date", nullable = true)
    private LocalDateTime resetExpiryDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + this.role); // Préfixe "ROLE_" requis par Spring Security
    }

    @Override
    public String getUsername() {
        return this.email; // L'email de l'utilisateur comme identifiant
    }

    @Override
    public String getPassword() {
        return this.password; // La password de l'utilisateur
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Vous pouvez ajouter une logique spécifique si nécessaire
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Vous pouvez ajouter une logique spécifique si nécessaire
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Vous pouvez ajouter une logique spécifique si nécessaire
    }

    @Override
    public boolean isEnabled() {
        return "Activated".equals(this.status); // L'utilisateur est activé ou non
    }

  private Long hebergementId;


}
