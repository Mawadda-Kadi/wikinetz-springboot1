package de.davaso.wikinetz.web;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Role;
import de.davaso.wikinetz.service.VersionService;
import de.davaso.wikinetz.service.ArticleService;
import de.davaso.wikinetz.web.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/versions")
public class VersionController {

    private final VersionService versionService;
    private final ArticleService articleService;

    public VersionController(VersionService versionService, ArticleService articleService) {
        this.versionService = versionService;
        this.articleService = articleService;
    }

    @PutMapping("/article/{articleId}/restore/{versionNumber}")
    public ResponseEntity<String> restore(
            @PathVariable int articleId,
            @PathVariable int versionNumber,
            @RequestHeader("Authorization") String authHeader
    ) {
        // Extract role from JWT
        String token = authHeader.replace("Bearer ", "");
        Role role = JwtUtil.getRole(token);

        // Only EDITORS or ADMINS can restore versions
        if (role == Role.VIEWER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Editors/Admin only");
        }

        // Fetch the Article entity first
        Article article = articleService.findById(articleId)
                .orElse(null);

        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Article not found");
        }

        boolean restored = versionService.restoreVersion(article, versionNumber);

        if (!restored) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Version not found or restore failed");
        }

        return ResponseEntity.ok("Article restored to version " + versionNumber);
    }
}
