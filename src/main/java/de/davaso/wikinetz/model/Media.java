package de.davaso.wikinetz.model;

public class Media {
    private int mediaId;
    private int articleId;
    private String filename;
    private String filepath;
    private MediaType type;

    public Media(int mediaId, int articleId, String filename, String filepath, MediaType type) {
        this.mediaId = mediaId;
        this.articleId = articleId;
        this.filename = filename;
        this.filepath = filepath;
        this.type = type;
    }

    public int getMediaId() {
        return mediaId;
    }

    public int getArticleId() {
        return articleId;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public MediaType getType() {
        return type;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Media{" +
                "mediaId=" + mediaId +
                ", articleId=" + articleId +
                ", filename='" + filename + '\'' +
                ", filepath='" + filepath + '\'' +
                ", type=" + type +
                '}';
    }
}
