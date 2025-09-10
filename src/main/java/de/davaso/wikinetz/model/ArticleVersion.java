package de.davaso.wikinetz.model;

import java.time.LocalDateTime;

public class ArticleVersion {
    private static int counter = 1;

    private final int versionId;
    private final int articleId;
    private final String content;
    private final int editorId;
    //private final LocalDateTime timestamp;
    private java.time.LocalDateTime updated_at;

    public ArticleVersion(int articleId, String content, int editorId) {
        this.versionId = counter++;
        this.articleId = articleId;
        this.content = content;
        this.editorId = editorId;
        this.updated_at = java.time.LocalDateTime.now();
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

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    // touch() refreshes the “last modified” timestamp.
    public void touch() {
        this.updated_at = java.time.LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Version " + versionId + " | Artikel-ID: " + articleId +
                " | Bearbeiter-ID: " + editorId + " | Datum: " + updated_at +
                "\nInhalt: " + content;
    }
}
