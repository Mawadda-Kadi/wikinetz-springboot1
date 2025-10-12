package de.davaso.wikinetz.manager;

import de.davaso.wikinetz.api.VersionService;
import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.model.Version;
import de.davaso.wikinetz.model.MediaSnapshot;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;


@Service
public class VersionManager implements VersionService {

    // articleId -> chronological versions
    private final Map<Integer, List<Version>> versionsByArticle = new HashMap<>();
    // optional, for global audit ordering (monotonic across all articles)
    private final AtomicLong globalSeq = new AtomicLong(0);
    private final ArticleManager articleManager;

    public VersionManager(ArticleManager articleManager) {
        this.articleManager = articleManager;
    }

    private List<Version> bucket(int articleId) {
        return versionsByArticle.computeIfAbsent(articleId, id -> new ArrayList<>());
    }

    /** Ensure v1 exists for an article (call when the article is created/first seen). */
    public Version ensureInitial(Article a, java.util.List<MediaSnapshot> mediaSnaps) {
        List<Version> list = bucket(a.getArticleId());
        if (list.isEmpty()) {
            Integer creatorId = (a.getCreatorId() < 0) ? null : a.getCreatorId();
            String creatorName = (a.getCreatorUsername() != null) ? a.getCreatorUsername() : "SYSTEM";

            Version v = new Version(
                    1,
                    a.getArticleId(),
                    globalSeq.incrementAndGet(),
                    a.getTitle(),
                    a.getContent(),
                    a.getCategory(),
                    "Initiale Erstellung",
                    creatorId,
                    creatorName,
                    a.getCreatedAt(),
                    mediaSnaps == null ? java.util.List.of() : mediaSnaps
            );
            list.add(v);
            return v;
        }
        return list.get(0);
    }

    /** Create a new snapshot from the current article state (call after any edit). */
    public Version snapshot(Article a, User editor, String note, java.util.List<MediaSnapshot> mediaSnaps) {
        List<Version> list = bucket(a.getArticleId());
        int nextNo = list.size() + 1;
        Version v = new Version(
                nextNo,                        // versionNumber
                a.getArticleId(),              // articleId
                globalSeq.incrementAndGet(),   // global sequence
                a.getTitle(),
                a.getContent(),
                a.getCategory(),
                (note == null || note.isBlank()) ? "Änderung" : note,
                (editor != null) ? editor.getUserId()   : null,
                (editor != null) ? editor.getUsername() : "SYSTEM",
                LocalDateTime.now(),
                mediaSnaps == null ? java.util.List.of() : mediaSnaps
        );
        list.add(v);
        return v;
    }

    public Version snapshot(Article a, User editor, String note) {
        return snapshot(a, editor, note, java.util.List.of());
    }

    /** Read-only list of versions for UI/history. */
    public List<Version> listVersions(int articleId) {
        return Collections.unmodifiableList(bucket(articleId));
    }

    public Optional<Version> getVersion(int articleId, int versionNumber) {
        List<Version> list = bucket(articleId);
        if (versionNumber < 1 || versionNumber > list.size()) return Optional.empty();
        return Optional.of(list.get(versionNumber - 1));
    }

    public Optional<Version> latest(int articleId) {
        List<Version> list = bucket(articleId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(list.size() - 1));
    }

    /** Restore article content to an older version and append a new snapshot documenting it. */
    public boolean restore(Article a, int versionNumber, User editor, String note) {
        Optional<Version> opt = getVersion(a.getArticleId(), versionNumber);
        if (opt.isEmpty()) return false;

        Version v = opt.get();
        // Apply state back to the article (setters refresh updatedAt)
        a.setTitle(v.getTitle());
        a.setContent(v.getContent());
        a.setCategory(v.getCategory());

        // Record that we restored
        snapshot(a, editor, (note == null || note.isBlank())
                ? ("Wiederhergestellt aus v" + versionNumber) : note);
        return true;
    }

    public boolean restoreVersion(int articleId, int versionNumber) {
        Article article = articleManager.findArticleById(articleId);
        if (article == null) {
            return false;
        }
        return restore(article, versionNumber, null, null);
    }

    public boolean clearForArticle(int articleId) {
        List<Version> list = versionsByArticle.get(articleId);
        if (list == null || list.isEmpty()) return false;
        versionsByArticle.remove(articleId);
        return true;
    }

    public int count(int articleId) { return bucket(articleId).size(); }
}
