package de.davaso.wikinetz.model;


import java.util.Optional;

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

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public static Optional<Category> getCategoryById(int id){
        for (Category c : values()) {
            if (c.categoryId == id) return java.util.Optional.of(c);
        }
        return Optional.empty();
    }
}
