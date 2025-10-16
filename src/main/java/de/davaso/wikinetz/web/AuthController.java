package de.davaso.wikinetz.web;

import de.davaso.wikinetz.exception.AuthenticationException;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.service.UserService;
import de.davaso.wikinetz.web.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Authenticates the user and returns a JWT token if credentials are valid.
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!userService.checkPassword(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }

        String token = JwtUtil.generateToken(user.getUsername(), user.getRole());
        return Map.of("token", token);
    }
}

