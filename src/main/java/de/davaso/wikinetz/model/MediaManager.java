package de.davaso.wikinetz.model;

import java.util.ArrayList;
import java.util.List;

public class MediaManager {
    private final List<Media> mediaList = new ArrayList<>();
    private int nextId = 1;

    public Media addMedia(int articleId, String filename, String filepath, MediaType type) {
        Media media = new Media(nextId++, articleId, filename, filepath, type); //nextId++ erzeugt eine eindeutige ID.
        mediaList.add(media);
        return media;
    }

    public List<Media> getMediaByArticleId(int articleId) {
        return mediaList.stream() // erzeugt einen Datenstrom.
                .filter(m -> m.getArticleId() == articleId) //filter(...) prüft jedes Element mit if-Logik
                .toList(); //toList() sammelt alle passenden Medien in eine neue Liste.
    }

    public boolean deleteMediaById(int mediaId) {
        return mediaList.removeIf(m -> m.getMediaId() == mediaId);//removeIf(...) geht durch die Liste.Für jedes Element wird geprüft
    }
}

