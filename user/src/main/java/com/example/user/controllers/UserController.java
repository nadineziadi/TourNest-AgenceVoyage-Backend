package com.example.user.controllers;

import com.example.user.entities.AuditLog;
import com.example.user.entities.Role;
import com.example.user.entities.User;
import com.example.user.repositories.UserRepository;
import com.example.user.services.AuditService;
import com.example.user.services.JwtService;
import com.example.user.services.UserService;
import com.example.user.services.UserServicesImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@EnableDiscoveryClient
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor

//@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserServicesImpl userServices;


    @Operation(summary = "Lister tous les users")
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllProjets() {  //
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @Operation(summary = "Ajouter un utilisateur")
    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User createdUser = userService.addUser(user);
        return ResponseEntity.ok(createdUser);
    }
/*
    @PutMapping("/{userId}/assign/{hebergementId}")
    public void assignToHebergement(@PathVariable int userId,
                                    @PathVariable Long hebergementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setHebergementId(hebergementId);
        userRepository.save(user);
    }
*/

    @Operation(summary = "Assignation de l'utilisateur à un hébergement")
    @PostMapping("/{userId}/assign/{hebergementId}")
    public String assignUserToHebergement(@PathVariable Long userId, @PathVariable Long hebergementId) {
        return userService.assignUserToHebergement(userId, hebergementId);
    }
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/byHebergement/{hebergementId}")
    public List<User> getUsersByHebergement(@PathVariable Long hebergementId) {
        return userService.getUsersByHebergement(hebergementId);
    }
    @PutMapping("/{userId}/unassign")
    public void unassignFromHebergement(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setHebergementId(null);
        userRepository.save(user);
    }
    @Autowired
    private AuditService auditService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        if (userServices.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        // Générer un token de confirmation
        String confirmationToken = UUID.randomUUID().toString(); // Génère un token unique
        user.setConfirmationToken(confirmationToken);

        // Définir une date d'expiration (par exemple, 24 heures)
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // Ajoute 24 heures au temps actuel
        user.setExpiryDate(expiryDate);

        // Sauvegarder l'utilisateur
        User savedUser = userServices.saveUser(user);

        // Envoyer un e-mail de confirmation
        sendConfirmationEmail(savedUser.getEmail(), savedUser.getConfirmationToken());

        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<?> confirmAccount(@RequestParam("token") String token) {
        try {
            // Trouver l'utilisateur par le token de confirmation
            User user = userServices.findByConfirmationToken(token);

            if (user == null) {
                return ResponseEntity.badRequest().body("\"Your account has been deleted due to inactivity. \" +\n" +
                        "                                \"The confirmation token has expired, and you did not confirm your account within the required time. \" +\n" +
                        "                                \"Please register again to create a new account.\"");
            }

            // Vérifier si le token a expiré
            if (user.getExpiryDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(
                        "Your account has been deleted due to inactivity. " +
                                "The confirmation token has expired, and you did not confirm your account within the required time. " +
                                "Please register again to create a new account."
                );
            }

            // Activer le compte
            user.setStatus("Activated");
            user.setConfirmationToken(null); // Supprimer le token après confirmation
            user.setExpiryDate(null); // Supprimer la date d'expiration

            // Sauvegarder les modifications
            userServices.saveUser2(user);

            return ResponseEntity.ok("Account confirmed successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    private void sendConfirmationEmail(String toEmail, String confirmationToken) {
        String confirmationUrl = "http://localhost:8086/users/confirm-account?token=" + confirmationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Activate Your Constructify Account");
        message.setText(
                "Hello,\n\n" +
                        "Welcome to Constructify! Please confirm your email address by clicking the link below:\n\n" +
                        confirmationUrl + "\n\n" +
                        "This link will expire in 24 hours. If you did not create an account, you can safely ignore this email.\n\n" +
                        "Thank you,\n" +
                        "Constructify Support"
        );

        mailSender.send(message);
    }


    @GetMapping("/all")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<User> users = userServices.getUsers(page, size);
        return ResponseEntity.ok(users);
    }


    @GetMapping("/getUserByRole/{role}")
    public List<User> getUsersByRole(@PathVariable Role role) {
        return userService.getUsersByRole(role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("id") Long id) {
        try {
            userServices.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur supprimé avec succès"); // Renvoie un objet JSON
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utilisateur non trouvé"); // Renvoie un objet JSON en cas d'erreur
            return ResponseEntity.status(404).body(response);
        }
    }

    @PutMapping("/UpdateUserRole/{id}")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {

        // Extraire le rôle depuis la requête JSON
        String roleString = requestBody.get("role");

        // Vérifier si la valeur est correcte
        Role newRole;
        try {
            newRole = Role.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Vérifier si l'utilisateur existe
        Optional<User> optionalUser = userServices.findUserById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Mettre à jour le rôle de l'utilisateur
        User user = optionalUser.get();
        user.setRole(newRole);
        User updatedUser = userServices.saveUser2(user);

        return ResponseEntity.ok(updatedUser);
    }







    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Invalid email."));

            if (!"Activated".equals(user.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Your account is not activated. Please confirm your email."));
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid password."));
            }

            // Générer un JWT
            String token = jwtService.generateToken(user);

            // Retourner le JWT et les détails de l'utilisateur
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "redirectUrl", determineRedirectUrl(user.getRole()),
                    "role", user.getRole().name(),
                    "user", user
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }




    @GetMapping("/session")
    public ResponseEntity<?> getSessionInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("user", user);
        response.put("creationTime", session.getCreationTime());
        response.put("lastAccessedTime", session.getLastAccessedTime());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Supprimer la session
        return ResponseEntity.ok("Logout successful.");
    }


    private String determineRedirectUrl(Role role) {
        switch (role) {
            case Admin:
                return "http://localhost:4200/admin"; // Redirection pour les administrateurs
            case Client:
                return "http://localhost:4200/offresList";  // Redirection pour les utilisateurs normaux
            default:
                return "/"; // Redirection par défaut
        }
    }


    @RequestMapping("/home")
    public String home(HttpSession session) {
        // Ajouter des informations dans la session si nécessaire
        session.setAttribute("username", "JohnDoe");

        // Ou récupérer une valeur de la session
        String username = (String) session.getAttribute("username");

        return "home";
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<AuditLog> auditLogs = auditService.getAuditLogs(PageRequest.of(page, size));
        return ResponseEntity.ok(auditLogs);
    }


    // New endpoints for password reset
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            userServices.initiatePasswordReset(request.getEmail());
            sendPasswordResetEmail(request.getEmail(), userServices.findByEmail(request.getEmail()).getResetToken());
            return ResponseEntity.ok("Password reset email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error initiating password reset: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userServices.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error resetting password: " + e.getMessage());
        }
    }

    private void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetUrl = "http://localhost:8089/Constructify/user/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset Your Constructify Password");
        message.setText(
                "Hello,\n\n" +
                        "You have requested to reset your password for Constructify. Please click the link below to reset your password:\n\n" +
                        resetUrl + "\n\n" +
                        "This link will expire in 1 hour. If you did not request a password reset, you can safely ignore this email.\n\n" +
                        "Thank you,\n" +
                        "Constructify Support"
        );

        mailSender.send(message);
    }
}

// DTOs for request bodies
class ForgotPasswordRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class ResetPasswordRequest {
    private String token;
    private String newPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}

