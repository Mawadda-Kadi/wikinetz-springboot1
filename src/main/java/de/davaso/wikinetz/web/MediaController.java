package de.davaso.wikinetz.web;

import de.davaso.wikinetz.dto.MediaDto;
import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.MediaType;
import de.davaso.wikinetz.service.ArticleService;
import de.davaso.wikinetz.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;
    private final ArticleService articleService;

    public MediaController(MediaService mediaService, ArticleService articleService) {
        this.mediaService = mediaService;
        this.articleService = articleService;
    }

    /**
     * List all media items belonging to a specific article.
     */
    @GetMapping("/article/{articleId}")
    public List<MediaDto> listByArticle(@PathVariable int articleId) {
        Article article = articleService.getById(articleId);

        return mediaService.getMediaForArticle(article).stream()
                .map(m -> new MediaDto(
                        m.getMediaId(),
                        m.getArticle().getArticleId(),
                        m.getFilename(),
                        m.getFilepath(),
                        m.getType()
                ))
                .toList();
    }

    /**
     * Upload a media file and link it to an article.
     */
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaDto upload(@RequestParam int articleId,
                           @RequestParam("file") MultipartFile file) throws IOException {

        Article article = articleService.getById(articleId);

        // Define upload directory (could be configurable)
        String uploadDir = "uploads/";
        Path path = Paths.get(uploadDir, file.getOriginalFilename());

        // Ensure directory exists
        Files.createDirectories(path.getParent());

        // Save the file locally
        file.transferTo(path.toFile());

        // Create media entry in DB
        var media = mediaService.addMedia(article, file.getOriginalFilename(), path.toString(), MediaType.IMAGE);

        return new MediaDto(
                media.getMediaId(),
                media.getArticle().getArticleId(),
                media.getFilename(),
                media.getFilepath(),
                media.getType()
        );
    }
}
