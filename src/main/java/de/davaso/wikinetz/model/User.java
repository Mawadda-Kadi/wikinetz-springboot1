package de.davaso.wikinetz.model;

import java.util.Objects;

public class User {
    private final int userId;
    private String username;
    private String passwordHash;
    private String email;
    private Role role;
    private boolean enabled = true;

    public User(int userId, String username, String passwordHash, String email, Role role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
    }

    // Getters

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    // Setters
    // Bisher keine Setters für Benutzername und E-Mail hinzufügt, um die Daten vor unsicherer Änderung zu schützen
    // und eine korrekte Verarbeitung zu erzwingen
    public void setPassword(String rawPassword) {
        this.passwordHash = PasswordUtil.hash(rawPassword);
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId= " + userId +
                ", username= '" + username + '\'' +
                ", email= '" + email + '\'' +
                ", role= " + role +
                ", enabled= " + enabled +
                '}';
    }

    // equals() und hashCode()
    // Ein neuer Benutzer erhält normalerweise immer eine neue eindeutige id, aber equals nutzt die id, damit Java
    // denselben Benutzer auch dann als gleich erkennt, wenn er zweimal geladen wird
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }
}