package de.davaso.wikinetz.manager;


import de.davaso.wikinetz.api.PasswordHasher;
import de.davaso.wikinetz.api.UserRepository;
import de.davaso.wikinetz.model.Role;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.service.PasswordUtil;

import java.util.*;


public class UserStore implements UserRepository{

    private final PasswordHasher hasher;
    // Speichert alle Benutzer im Speicher (Map: ID -> User)
    private final Map<Integer, User> users = new HashMap<>();

    // Gibt jedem Benutzer eine eindeutige, fortlaufende ID
    private int nextUserId = 1;

    public UserStore(PasswordHasher hasher) {
        this.hasher = hasher;
    }

    // Erstellt einen Admin-Benutzer
    public User ensureAdmin(String username, String password, String email) {
        // prüfen, ob der Benutzername schon existiert
        for (User u : users.values()) {
            if (u.getUsername().equals(username)) {
                return u; // existiert schon -> zurückgeben
            }
        }
        // neuen Admin erzeugen
        User admin = new User(nextUserId++, username, PasswordUtil.hash(password), email, Role.ADMIN);
        users.put(admin.getUserId(), admin);
        return admin;
    }

    // Sucht einen Benutzer anhand des Benutzernamens
    public Optional<User> findByUsername(String username) {
        for (User u : users.values()) {
            if (u.getUsername().equals(username)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    // Prüft, ob ein Benutzername bereits vergeben ist
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    // Registriert einen neuen Benutzer
    public User register(String username, String rawPassword,
                         String email, Role role) {
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Benutzername ist bereits vergeben.");
        }
        String hashed = hasher.hash(rawPassword);
        User u = new User(nextUserId++, username, hashed, email, role);
        users.put(u.getUserId(), u);
        return u;
    }

    @Override
    public void updatePassword(User user, String newRawPassword) {
        user.setPasswordHash(hasher.hash(newRawPassword));
    }

    // Gibt eine Liste aller Benutzer zurück
    public List<User> listAll() {
        return new ArrayList<>(users.values());
    }

    // Löscht einen Benutzer anhand des Benutzernamens
    public void deleteByUsername(String username) {
        Integer idToRemove = null;
        for (User u : users.values()) {
           if (u.getUsername().equals(username)) {
                idToRemove = u.getUserId();
                break;
            }
        }
        if (idToRemove != null) {
            users.remove(idToRemove);
        }
    }
}
