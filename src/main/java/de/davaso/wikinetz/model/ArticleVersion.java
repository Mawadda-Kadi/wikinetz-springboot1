package de.davaso.wikinetz.model;

import java.time.LocalDateTime;

public class ArticleVersion {
    private static int counter = 1;

    private final int versionId;
    private final int articleId;
    private final String content;
    private final int editorId;
    private final LocalDateTime timestamp;

    public ArticleVersion(int articleId, String content, int editorId) {
        this.versionId = counter++;
        this.articleId = articleId;
        this.content = content;
        this.editorId = editorId;
        this.timestamp = LocalDateTime.now();
    }

    public int getVersionId() {
        return versionId;
    }

    public int getArticleId() {
        return articleId;
    }

    public String getContent() {
        return content;
    }

    public int getEditorId() {
        return editorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Version " + versionId + " | Artikel-ID: " + articleId +
                " | Bearbeiter-ID: " + editorId + " | Datum: " + timestamp +
                "\nInhalt: " + content;
    }
}
