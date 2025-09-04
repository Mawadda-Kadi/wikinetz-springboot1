package de.davaso.wikinetz.model;

import java.time.LocalDateTime;

public class Article {
    private final int ARTICLE_ID;
    private String title;
    private String content;
    private java.time.LocalDateTime created_at;
    private java.time.LocalDateTime updated_at;

    // Article Constructor
    public Article (int article_id, String title, String content) {

        this.ARTICLE_ID = article_id;
        this.title = title;
        this.content = content;
        this.created_at = java.time.LocalDateTime.now();
        this.updated_at = this.created_at;
    }

    // Getters and Setters
    public int getArticleId() {
        return ARTICLE_ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    // touch() refreshes the “last modified” timestamp.
    private void touch() {
        this.updated_at = java.time.LocalDateTime.now();
    }

    // toString() runs whenever Java needs a string for my object
    // I don’t have to call it by name most of the time. it’s used implicitly
    @Override
    public String toString() {
        return "[" + ARTICLE_ID + "] " + title + " - " + content;
    }
}
