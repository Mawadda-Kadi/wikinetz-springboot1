package de.davaso.wikinetz.web;

import de.davaso.wikinetz.manager.VersionManager;
import de.davaso.wikinetz.model.Role;
import de.davaso.wikinetz.web.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/versions")
public class VersionController {

    private final VersionManager versionManager;

    public VersionController(VersionManager versionManager) {
        this.versionManager = versionManager;
    }

    @PutMapping("/article/{articleId}/restore/{versionNumber}")
    public ResponseEntity<String> restore(
            @PathVariable int articleId,
            @PathVariable int versionNumber,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Role role = JwtUtil.getRole(token);

        if (role == Role.VIEWER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Editors/Admin only");
        }

        boolean ok = versionManager.restoreVersion(articleId, versionNumber);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Version not found or restore failed");
        }

        return ResponseEntity.ok("Article restored to version " + versionNumber);
    }
}


