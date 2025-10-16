package de.davaso.wikinetz.service;

import de.davaso.wikinetz.model.Role;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Ensures that an admin user with the given username exists.
     * If not, creates one with the provided password and email.
     */
    public User ensureAdmin(String username, String rawPassword, String email) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User admin = new User();
                    admin.setUsername(username);
                    admin.setPasswordHash(passwordEncoder.encode(rawPassword));
                    admin.setEmail(email);
                    admin.setRole(Role.ADMIN);
                    admin.setEnabled(true);
                    return userRepository.save(admin);
                });
    }

    /**
     * Verifies a raw password against the stored hash.
     */
    public boolean checkPassword(String rawPassword, String storedHash) {
        return passwordEncoder.matches(rawPassword, storedHash);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Registers a new user with an encoded password.
     */
    public User register(String username, String rawPassword, String email, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setRole(role);
        u.setEnabled(true);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        return userRepository.save(u);
    }

    /**
     * Updates a user's password with a new encoded hash.
     */
    public void updatePassword(User user, String newRawPassword) {
        user.setPasswordHash(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public void deleteByUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(userRepository::delete);
    }
}
