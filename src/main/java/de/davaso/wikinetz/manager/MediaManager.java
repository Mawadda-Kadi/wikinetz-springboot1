package de.davaso.wikinetz.manager;

import de.davaso.wikinetz.api.IdGenerator;
import de.davaso.wikinetz.api.MediaService;
import de.davaso.wikinetz.model.Media;
import de.davaso.wikinetz.model.MediaType;
import de.davaso.wikinetz.model.MediaSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MediaManager implements MediaService {
    private final List<Media> mediaList = new ArrayList<>();
    private final IdGenerator idGen;

    public MediaManager(IdGenerator idGen) {
        this.idGen = idGen;
    }

    @Override
    public Media addMedia(int articleId, String filename, String filepath, MediaType type) {
        Media media = new Media(idGen.nextId(), articleId, filename, filepath, type);
        mediaList.add(media);
        return media;
    }

    public Media findById(int mediaId) {
        for (Media m : mediaList) {
            if (m.getMediaId() == mediaId) return m;
        }
        return null;
    }

    // Gib alle zu Artikel X gehörenden Medien zurück.
    public List<Media> getMediaByArticleId(int articleId) {
        // erzeugt einen Datenstrom.
        return mediaList.stream()
                //filter(...) prüft jedes Element mit if-Logik
                .filter(m -> m.getArticleId() == articleId)
                //toList() sammelt alle passenden Medien in eine neue Liste.
                .toList();
    }

    public boolean updateMedia(int mediaId, String newFilename, String newFilepath, MediaType newType) {
        Media m = findById(mediaId);
        if (m == null) return false;

        if (newFilename != null) m.setFilename(newFilename);
        if (newFilepath != null) m.setFilepath(newFilepath);
        if (newType != null) m.setType(newType);
        return true;
    }

    public void replaceAllForArticle(int articleId, java.util.List<MediaSnapshot> snaps) {
        // remove all current media for the article
        mediaList.removeIf(m -> m.getArticleId() == articleId);
        // re-add from snapshots (new ids)
        if (snaps != null) {
            for (MediaSnapshot s : snaps) {
                addMedia(articleId, s.getFilename(), s.getFilepath(), s.getType());
            }
        }
    }

    public boolean deleteMediaById(int mediaId) {
        //removeIf(...) geht durch die Liste.Für jedes Element wird geprüft
        return mediaList.removeIf(m -> m.getMediaId() == mediaId);
    }
}

