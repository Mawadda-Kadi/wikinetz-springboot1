package de.davaso.wikinetz.web;

import de.davaso.wikinetz.api.dto.UserDto;
import de.davaso.wikinetz.manager.UserStore;
import de.davaso.wikinetz.model.Role;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.web.security.JwtUtil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String username,
                       @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Role role = JwtUtil.getRole(token);
        if (role != Role.ADMIN) {
            throw new RuntimeException("Access denied: ADMIN only");
        }
        userStore.deleteByUsername(username);
    }

    @GetMapping
    public List<UserDto> list() {
        return userStore.listAll().stream()
                .map(u -> new UserDto(u.getUserId(), u.getUsername(), u.getEmail(), u.getRole(), u.isEnabled()))
                .toList();
    }
}
