package de.davaso.wikinetz.api;

import de.davaso.wikinetz.model.*;
import java.util.*;

public interface VersionService {
    Version ensureInitial(Article a, java.util.List<MediaSnapshot> mediaSnaps);
    Version snapshot(Article a, User editor, String note, java.util.List<MediaSnapshot> mediaSnaps);
    java.util.List<Version> listVersions(int articleId);
    Optional<Version> getVersion(int articleId, int versionNumber);
    Optional<Version> latest(int articleId);
    boolean restore(Article a, int versionNumber, User editor, String note);
    boolean clearForArticle(int articleId);
    int count(int articleId);
}
