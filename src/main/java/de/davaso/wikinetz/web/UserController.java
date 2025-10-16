package de.davaso.wikinetz.web;

import de.davaso.wikinetz.dto.UserDto;
import de.davaso.wikinetz.exception.AuthorizationException;
import de.davaso.wikinetz.service.UserService;
import de.davaso.wikinetz.model.Role;
import de.davaso.wikinetz.web.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> delete(@PathVariable String username,
                                         @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Role role = JwtUtil.getRole(token);
        if (role != Role.ADMIN) {
            throw new AuthorizationException("Access denied: ADMIN only");
        }

        userService.deleteByUsername(username);
        return ResponseEntity.ok("User " + username + " deleted");
    }

    @GetMapping
    public List<UserDto> list() {
        return userService.listAll().stream()
                .map(u -> new UserDto(u.getUserId(), u.getUsername(), u.getEmail(), u.getRole(), u.isEnabled()))
                .toList();
    }
}
