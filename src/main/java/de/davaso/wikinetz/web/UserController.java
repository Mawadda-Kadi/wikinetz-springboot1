package de.davaso.wikinetz.web;

import de.davaso.wikinetz.api.dto.UserDto;
import de.davaso.wikinetz.exception.AuthorizationException;
import de.davaso.wikinetz.manager.UserStore;
import de.davaso.wikinetz.model.Role;
import de.davaso.wikinetz.web.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserStore userStore;

    public UserController(UserStore userStore) {
        this.userStore = userStore;
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> delete(@PathVariable String username,
                                         @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Role role = JwtUtil.getRole(token);
        if (role != Role.ADMIN) {
            throw new AuthorizationException("Access denied: ADMIN only");
        }

        userStore.deleteByUsername(username);
        return ResponseEntity.ok("User " + username + " deleted");
    }

    @GetMapping
    public List<UserDto> list() {
        return userStore.listAll().stream()
                .map(u -> new UserDto(u.getUserId(), u.getUsername(), u.getEmail(), u.getRole(), u.isEnabled()))
                .toList();
    }
}
