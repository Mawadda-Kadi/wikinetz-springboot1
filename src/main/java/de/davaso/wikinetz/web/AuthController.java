package de.davaso.wikinetz.web;

import de.davaso.wikinetz.exception.AuthenticationException;
import de.davaso.wikinetz.manager.UserStore;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.web.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserStore userStore;

    public AuthController(UserStore userStore) {
        this.userStore = userStore;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        User user = userStore.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!userStore.matchesPassword(user, password)) {
            throw new AuthenticationException("Invalid username or password");
        }

        String token = JwtUtil.generateToken(username, user.getRole());
        return token; // Return as plain string or JSON
    }
}

