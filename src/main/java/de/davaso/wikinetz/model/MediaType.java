package de.davaso.wikinetz.model;
//MediaType ist ein Enum (Aufzählungstyp), der festlegt, welche Medientypen erlaubt sind.
//Enums sind typsicher: Nur erlaubte Werte sind möglich
//Verhindert Fehler durch falsche Strings wie "img" statt "IMAGE"

public enum MediaType {
    IMAGE,
    VIDEO,
    LINK
}
