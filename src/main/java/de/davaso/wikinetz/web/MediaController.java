package de.davaso.wikinetz.web;

import de.davaso.wikinetz.api.MediaService;
import de.davaso.wikinetz.api.dto.MediaDto;
import de.davaso.wikinetz.model.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/article/{articleId}")
    public List<MediaDto> listByArticle(@PathVariable int articleId) {
        return mediaService.getMediaByArticleId(articleId).stream()
                .map(m -> new MediaDto(m.getMediaId(), m.getArticleId(), m.getFilename(), m.getFilepath(), m.getType()))
                .toList();
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaDto upload(@RequestParam int articleId,
                           @RequestParam("file") MultipartFile file) throws IOException {
        // Save file somewhere (e.g., /uploads)
        String uploadDir = "uploads/";
        java.nio.file.Path path = java.nio.file.Paths.get(uploadDir, file.getOriginalFilename());
        java.nio.file.Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());

        var media = mediaService.addMedia(articleId, file.getOriginalFilename(), path.toString(), MediaType.IMAGE);

        return new MediaDto(media.getMediaId(), media.getArticleId(), media.getFilename(), media.getFilepath(), media.getType());
    }


}
