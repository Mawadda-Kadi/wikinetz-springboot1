package de.davaso.wikinetz.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum Category {
    BACKEND(1, "Backend-Entwicklung"),
    FRONTEND(2, "Frontend-Entwicklung"),
    IT_SICHERHEIT(3, "IT-Sicherheit"),
    WARTUNG_INSTALLATION(4, "Wartung & Installation"),
    CLOUD_VIRTUALISIERUNG(5, "Cloud & Virtualisierung");

    private final int categoryId;
    private final String categoryName;

    Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public static Optional<Category> getCategoryById(int id) {
        for (Category c : values()) {
            if (c.categoryId == id) return Optional.of(c);
        }
        return Optional.empty();
    }
}
