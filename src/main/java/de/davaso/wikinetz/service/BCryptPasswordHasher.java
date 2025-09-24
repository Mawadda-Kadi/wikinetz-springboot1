package de.davaso.wikinetz.service;

import de.davaso.wikinetz.api.PasswordHasher;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordHasher implements PasswordHasher {
    private final int cost;
    public BCryptPasswordHasher(int cost) { this.cost = cost; }
    public String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt(cost)); }
    public boolean matches(String raw, String hash) { return BCrypt.checkpw(raw, hash); }
}

