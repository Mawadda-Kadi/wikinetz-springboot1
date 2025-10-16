package de.davaso.wikinetz.model;

import lombok.Value;

/*
@Value automatically makes:
- All fields private final
- Generates constructor, getters, equals(), hashCode(), and toString()
- Makes the class final
Perfect for this kind of immutable DTO
 */
// MediaSnapshot (unveränderliche Kopie eines Mediums zum Zeitpunkt dieser Version)
@Value
public class MediaSnapshot {
    String filename;
    String filepath;
    MediaType type;
}

