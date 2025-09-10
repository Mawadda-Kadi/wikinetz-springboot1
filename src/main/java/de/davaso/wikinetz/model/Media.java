package de.davaso.wikinetz.model;
//Die Klasse Media ist ein Datenmodell für ein Medienobjekt (z.B. Bild, Video, Link), das zu einem Artikel gehört.
public class Media {
    private int mediaId;
    private int articleId;
    private String filename;
    private String filepath;   //Speicherort oder URL
    private MediaType type;

    public Media(int mediaId, int articleId, String filename, String filepath, MediaType type) {
        this.mediaId = mediaId;
        this.articleId = articleId;
        this.filename = filename;
        this.filepath = filepath;
        this.type = type;
    }

    // Getters
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

    // Setters
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
    public String toString() { // Gibt eine lesbare Darstellung des Objekts zurück (z.B. für Debugging)
        return "Media{" +
                "mediaId=" + mediaId +
                ", articleId=" + articleId +
                ", filename='" + filename + '\'' +
                ", filepath='" + filepath + '\'' +
                ", type=" + type +
                '}';
    }
}
