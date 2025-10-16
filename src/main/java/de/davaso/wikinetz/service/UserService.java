package de.davaso.wikinetz.service;

import de.davaso.wikinetz.model.Role;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.repository.UserRepository;
import de.davaso.wikinetz.api.PasswordHasher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User ensureAdmin(String username, String rawPassword, String email) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User admin = new User();
                    admin.setUsername(username);
                    admin.setPasswordHash(passwordHasher.hash(rawPassword));
                    admin.setEmail(email);
                    admin.setRole(Role.ADMIN);
                    admin.setEnabled(true);
                    return userRepository.save(admin);
                });
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User register(String username, String rawPassword, String email, Role role) {
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordHasher.hash(rawPassword));
        u.setEmail(email);
        u.setRole(role);
        u.setEnabled(true);
        return userRepository.save(u);
    }

    public void updatePassword(User user, String newRawPassword) {
        user.setPasswordHash(passwordHasher.hash(newRawPassword));
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
