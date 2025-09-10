package de.davaso.wikinetz.model;

import java.util.ArrayList;
import java.util.List;

public class MediaManager {
    private final List<Media> mediaList = new ArrayList<>();
    private int nextId = 1;

    public Media addMedia(int articleId, String filename, String filepath, MediaType type) {
        Media media = new Media(nextId++, articleId, filename, filepath, type);
        mediaList.add(media);
        return media;
    }

    public List<Media> getMediaByArticleId(int articleId) {
        return mediaList.stream()
                .filter(m -> m.getArticleId() == articleId)
                .toList();
    }

    public boolean deleteMediaById(int mediaId) {
        return mediaList.removeIf(m -> m.getMediaId() == mediaId);
    }
}

