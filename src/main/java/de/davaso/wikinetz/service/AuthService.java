package de.davaso.wikinetz.service;

import de.davaso.wikinetz.model.Role;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.manager.UserStore;

import java.util.Optional;

/**
 * Einfache Anmelde-/Abmelde-Logik für die Konsolen-App.
 * Arbeitet mit dem UserStore (Map<Integer, User>) zusammen.
 *
 * Aufgaben:
 * - Login (prüft Benutzername + Passwort über BCrypt)
 * - Logout
 * - Aktuellen Benutzer halten
 * - Status prüfen (eingeloggt? Rolle?)
 */
public class AuthService {

    // Referenz auf die Benutzerverwaltung (in-memory Map)
    private final UserStore userStore;

    // Hält den aktuell eingeloggten Benutzer (null = niemand eingeloggt)
    private User currentUser;

    public AuthService(UserStore userStore) {
        this.userStore = userStore;
    }

    // Gibt den aktuell eingeloggten Benutzer zurück (Optional, um Null zu vermeiden)
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    // Prüft, ob jemand eingeloggt ist
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Versucht, einen Benutzer einzuloggen.
     * Ablauf:
     * 1) User per Benutzername aus dem UserStore holen
     * 2) prüfen, ob enabled == true
     * 3) Passwort mit BCrypt gegen gespeicherten Hash prüfen (PasswordUtil.matches)
     * 4) bei Erfolg: currentUser setzen
     *
     * @return true = Login erfolgreich, false = fehlgeschlagen
     */
    public boolean login(String username, String rawPassword) {
        Optional<User> userOpt = userStore.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false; // Benutzername unbekannt
        }

        User user = userOpt.get();

        if (!user.isEnabled()) {
            return false; // Konto deaktiviert
        }

        // BCrypt-Prüfung gegen den gespeicherten Hash
        boolean ok = PasswordUtil.matches(rawPassword, user.getPasswordHash());
        if (!ok) {
            return false; // Passwort falsch
        }

        // Erfolg: als aktueller Benutzer merken
        this.currentUser = user;
        return true;
    }

    // Meldet den aktuellen Benutzer ab
    public void logout() {
        this.currentUser = null;
    }

    // Hilfsfunktion: Prüft, ob der eingeloggte Benutzer eine der angegebenen Rollen hat
    public boolean hasAnyRole(Role... roles) {
        if (!isLoggedIn()) return false;
        Role myRole = currentUser.getRole();
        for (Role r : roles) {
            if (myRole == r) return true;
        }
        return false;
    }

    // Erzwingt eine Rolle; wirft eine RuntimeException, wenn nicht erfüllt
    public void requireRole(Role... roles) {
        if (!hasAnyRole(roles)) {
            throw new RuntimeException("Zugriff verweigert: fehlende Berechtigung.");
        }
    }
}

