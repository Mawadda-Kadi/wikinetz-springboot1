package de.davaso.wikinetz.model;


// MediaSnapshot (unveränderliche Kopie eines Mediums zum Zeitpunkt dieser Version)
public class MediaSnapshot {
    private final String filename;
    private final String filepath;
    private final MediaType type;

    public MediaSnapshot(String filename, String filepath, MediaType type) {
        this.filename = filename;
        this.filepath = filepath;
        this.type = type;
    }

    public String getFilename() { return filename; }
    public String getFilepath() { return filepath; }
    public MediaType getType() { return type; }
}

