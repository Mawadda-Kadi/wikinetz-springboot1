package de.davaso.wikinetz.model;

import java.util.Objects;

public class User {
    private final int USER_ID;
    private String username;
    private String passwordHash;
    private String email;
    private Role role;
    private boolean enabled = true;

    public User(int USER_ID, String username, String passwordHash, String email, Role role) {
        this.USER_ID = USER_ID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
    }

    // Getters

    public int getUSER_ID() {
        return USER_ID;
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
                "USER_ID= " + USER_ID +
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
        if (!(o instanceof User user)) return false;
        return USER_ID == user.USER_ID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(USER_ID);
    }
}
