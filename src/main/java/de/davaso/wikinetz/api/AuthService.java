package de.davaso.wikinetz.api;

import de.davaso.wikinetz.model.*;
import java.util.Optional;

public interface AuthService {
    Optional<User> getCurrentUser();
    boolean isLoggedIn();
    boolean login(String username, String rawPassword);
    void logout();
    boolean hasAnyRole(Role... roles);
    void requireRole(Role... roles);
}
