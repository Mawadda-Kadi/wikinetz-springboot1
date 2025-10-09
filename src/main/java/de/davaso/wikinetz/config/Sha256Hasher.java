package de.davaso.wikinetz.config;

import de.davaso.wikinetz.api.PasswordHasher;

class Sha256Hasher implements PasswordHasher {

    @Override
    public String hash(String raw) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean matches(String raw, String hashed) {
        // Simply re-hash and compare
        return hash(raw).equals(hashed);
    }
}

