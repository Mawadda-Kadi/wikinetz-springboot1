package de.davaso.wikinetz.api;

import de.davaso.wikinetz.model.*;
import java.util.List;

public interface MediaService {
    Media addMedia(int articleId, String filename, String filepath, MediaType type);
    Media findById(int mediaId);
    List<Media> getMediaByArticleId(int articleId);
    boolean updateMedia(int mediaId, String newFilename, String newFilepath, MediaType newType);
    boolean deleteMediaById(int mediaId);
    void replaceAllForArticle(int articleId, java.util.List<MediaSnapshot> snaps);
}
