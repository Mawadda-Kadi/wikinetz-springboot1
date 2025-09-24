package de.davaso.wikinetz.api;

public interface PasswordHasher {
    String hash(String raw);
    boolean matches(String raw, String hash);
}

