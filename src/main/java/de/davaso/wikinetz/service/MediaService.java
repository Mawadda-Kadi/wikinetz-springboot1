package de.davaso.wikinetz.service;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Media;
import de.davaso.wikinetz.model.MediaType;
import de.davaso.wikinetz.repository.MediaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media addMedia(Article article, String filename, String filepath, MediaType type) {
        Media media = new Media();
        media.setArticle(article);
        media.setFilename(filename);
        media.setFilepath(filepath);
        media.setType(type);
        return mediaRepository.save(media);
    }

    public List<Media> getMediaForArticle(Article article) {
        return mediaRepository.findByArticle(article);
    }

    public void deleteMedia(int id) {
        mediaRepository.deleteById(id);
    }
}
