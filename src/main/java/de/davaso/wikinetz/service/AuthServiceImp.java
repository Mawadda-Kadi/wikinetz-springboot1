package de.davaso.wikinetz.service;
import de.davaso.wikinetz.api.AuthService;
import de.davaso.wikinetz.api.PasswordHasher;
import de.davaso.wikinetz.api.UserRepository;
import de.davaso.wikinetz.model.*;
import java.util.Optional;

public class AuthServiceImp implements AuthService {
    private final UserRepository userStore;
    private final PasswordHasher hasher;
    private User currentUser;

    public AuthServiceImp(UserRepository userStore, PasswordHasher hasher) {
        this.userStore = userStore;
        this.hasher = hasher;
    }

    public Optional<User> getCurrentUser() { return Optional.ofNullable(currentUser); }
    public boolean isLoggedIn() { return currentUser != null; }

    public boolean login(String username, String rawPassword) {
        var userOpt = userStore.findByUsername(username);
        if (userOpt.isEmpty()) return false;
        var user = userOpt.get();
        if (!user.isEnabled()) return false;
        if (!hasher.matches(rawPassword, user.getPasswordHash())) return false;
        this.currentUser = user;
        return true;
    }
    public void logout() { this.currentUser = null; }
    public boolean hasAnyRole(Role... roles) {
        if (!isLoggedIn()) return false;
        var myRole = currentUser.getRole();
        for (Role r : roles) if (myRole == r) return true;
        return false;
    }
    public void requireRole(Role... roles) {
        if (!hasAnyRole(roles)) throw new RuntimeException("Zugriff verweigert: fehlende Berechtigung.");
    }
}
