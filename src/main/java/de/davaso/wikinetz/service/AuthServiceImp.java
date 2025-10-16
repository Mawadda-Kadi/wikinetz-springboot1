package de.davaso.wikinetz.service;

import de.davaso.wikinetz.api.AuthService;
import de.davaso.wikinetz.api.PasswordHasher;
import de.davaso.wikinetz.api.UserRepository;
import de.davaso.wikinetz.model.*;
import de.davaso.wikinetz.exception.AuthenticationException;
import de.davaso.wikinetz.exception.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthServiceImp implements AuthService {
    //Importiere SLF4J und erstelle ein Logger-Objekt:
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImp.class);

    private final UserRepository userStore;
    private final PasswordHasher hasher;
    private User currentUser;

    public AuthServiceImp(UserRepository userStore, PasswordHasher hasher) {
        this.userStore = userStore;
        this.hasher = hasher;
        logger.info("AuthServiceImp initialisiert");
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean login(String username, String rawPassword) {
        logger.info("Login-Versuch für Benutzer '{}'", username);

        var userOpt = userStore.findByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("Login fehlgeschlagen: Benutzer '{}' nicht gefunden", username);
            throw new AuthenticationException("Benutzer '" + username + "' nicht gefunden");
        }
        var user = userOpt.get();
        if (!user.isEnabled()) {
            logger.warn("Login fehlgeschlagen: Benutzer '{}' ist deaktiviert", username);
            throw new AuthenticationException("Benutzer '" + username + "' ist deaktiviert");
        }
        if (!hasher.matches(rawPassword, user.getPasswordHash())) {
            logger.warn("Login fehlgeschlagen: Falsches Passwort für Benutzer '{}'", username);
            throw new AuthenticationException("Falsches Passwort für Benutzer '" + username + "'");
        }
        this.currentUser = user;
        logger.info("Benutzer '{}' erfolgreich eingeloggt", username);
        return true;
    }

    public void logout() {
        if (currentUser != null) {
            logger.info("Benutzer '{}' wurde ausgeloggt", currentUser.getUsername());
        } else {
            logger.debug("Logout aufgerufen, aber kein Benutzer angemeldet");
        }
        this.currentUser = null;
    }

    public boolean hasAnyRole(Role... roles) {
        if (!isLoggedIn()) return false;
        var myRole = currentUser.getRole();
        for (Role r : roles) if (myRole == r) return true;
        return false;
    }

    public void requireRole(Role... roles) {
        if (!hasAnyRole(roles)) {
            String username = currentUser != null ? currentUser.getUsername() : "ANONYMOUS";
            logger.error("Zugriff verweigert für Benutzer '{}', erforderliche Rollen: {}", username, roles);
            throw new AuthorizationException("Zugriff verweigert: fehlende Berechtigung für Benutzer '" + username + "'");
        } else {
            logger.debug("Berechtigungsprüfung erfolgreich für Benutzer '{}'", currentUser.getUsername());
        }
    }
}