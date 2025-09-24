package de.davaso.wikinetz.api;

import de.davaso.wikinetz.model.*;
import java.util.*;

public interface UserRepository {
    User ensureAdmin(String username, String password, String email);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User register(String username, String rawPassword, String email, Role role);
    java.util.List<User> listAll();
    void updatePassword(User user, String newRawPassword);
    void deleteByUsername(String username);
}

