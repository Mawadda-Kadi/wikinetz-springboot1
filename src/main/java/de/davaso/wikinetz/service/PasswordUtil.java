package de.davaso.wikinetz.service;

import org.mindrot.jbcrypt.BCrypt;


// Utility class (Hilfsklasse) zum Hashing und Verifizieren (Prüfen) von Passwörten
public final class PasswordUtil {
    private PasswordUtil() {}

    // Höherer cost factor = sicherer, aber langsamer
    private static final int cost = 12;

    // salt: ein zufälliges Datenelement, das dem Passwort vor dem Hashing hinzufügt wird. Es stellt sicher, dass
    //       dasselbe Passwort bei verschiedenen Benutzen unterschiedliche Hashes erzeugt
    // gensalt: erzeugt ein zufälliges Salt und codiert den Cost Factor in den final Hash String
    public static String hash(String rawPassword) {
        String salt = BCrypt.gensalt(cost);
        return BCrypt.hashpw(rawPassword, salt);
    }

    // Validiert das eingegebene Passwort gegen den gespeicherten BCrypt-Hash
    public static boolean matches(String rawPassword, String storedHash) {
        return BCrypt.checkpw(rawPassword, storedHash);
    }
}
