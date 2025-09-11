package de.davaso.wikinetz.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VersionManager {
    private final List<ArticleVersion> versions = new ArrayList<>();

    public void saveVersion(int articleId, String content, int editorId) {
        versions.add(new Version(articleId, content, editorId));
    }

    public List<ArticleVersion> getVersionsForArticle(int articleId) {
        return versions.stream()
                .filter(v -> v.getArticleId() == articleId)
                .collect(Collectors.toList());
    }
}
