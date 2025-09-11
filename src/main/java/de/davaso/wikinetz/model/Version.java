package de.davaso.wikinetz.model;

import java.time.LocalDateTime;

public class Version {
    private final int versionNumber;
    private final String title;
    private final String content;
    private final Category category;
    private final String note;
    private final Integer editorId;
    private final String editorUsername;
    private java.time.LocalDateTime createdAt;

    public Version(int versionNumber, String title, String content, Category category, String note, Integer editorId, String editorUsername, java.time.LocalDateTime createdAt) {
        this.versionNumber = versionNumber;
        this.title = title;
        this.content = content;
        this.category = category;
        this.note = note;
        this.editorId = editorId;
        this.editorUsername = editorUsername;
        this.createdAt = java.time.LocalDateTime.now();
    }

    // Getters
    public int getVersionNumber() {
        return versionNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Category getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public Integer getEditorId() {
        return editorId;
    }

    public String getEditorUsername() {
        return editorUsername;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public java.lang.String toString() {
        return "Version{" +
                "versionNumber=" + versionNumber +
                ", note='" + note + '\'' +
                ", editorUsername='" + editorUsername + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // equals() und hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version v)) return false;
        return versionNumber == v.versionNumber
                && articleId == v.articleId;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(articleId, versionNumber);
    }
}




