package de.davaso.wikinetz.model;

import java.time.LocalDateTime;


public class Article {
    private final int articleId;
    private String title;
    private String content;
    private Category category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // Article Constructor
    public Article(int articleId, String title, String content, Category category) {

        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // Getters
    public int getArticleId() {
        return articleId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    //Setters
    public void setTitle(String title) {
        this.title = title;
        touch();
    }

    public void setContent(String content) {
        this.content = content;
        touch();
    }

    public void setCategory(Category category) {
        this.category = category;
        touch();
    }

    // touch() refreshes the “last modified” timestamp.
    private void touch() {
        this.updatedAt = java.time.LocalDateTime.now();
    }

    // toString() runs whenever Java needs a string for my object
    // I don’t have to call it by name most of the time. it’s used implicitly
    @Override
    public String toString() {
        return "Article{" +
                "id= " +articleId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", category=" + category +
                '}';
    }
}