package de.davaso.wikinetz.model;
import java.time.LocalDateTime;
import java.util.Objects;

public class Version {
    private final int versionNumber;
    private final int articleId;
    private final  long globalSeq;
    private final String title;
    private final String content;
    private final Category category;
    private final String note;
    private final Integer editorId;
    private final String editorUsername;
    private final LocalDateTime createdAt;
    private final java.util.List<MediaSnapshot> media;
    public java.util.List<MediaSnapshot> getMedia() { return media; }

    public Version(
            int versionNumber,
            int articleId,
            long globalSeq,
            String title,
            String content,
            Category category,
            String note,
            Integer editorId,
            String editorUsername,
            LocalDateTime createdAt,
            java.util.List<MediaSnapshot> media
    ) {
        this.versionNumber = versionNumber;
        this.articleId = articleId;
        this.globalSeq = globalSeq;
        this.title = title;
        this.content = content;
        this.category = category;
        this.note = note;
        this.editorId = editorId;
        this.editorUsername = editorUsername;
        this.createdAt = createdAt;
        this.media = java.util.List.copyOf(media == null ? java.util.List.of() : media);
    }

    // Getters
    public int getVersionNumber() {
        return versionNumber;
    }
    public int getArticleId() { return articleId; }
    public String getTitle() {
        return title;
    }
    public long getGlobalSeq() { return globalSeq; }
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
        if (o == null || getClass() != o.getClass()) return false;
        Version v = (Version) o;
        return versionNumber == v.versionNumber && articleId == v.articleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, versionNumber);
    }
}




