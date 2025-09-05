package de.davaso.wikinetz.model;


public enum Category {
    BACKEND(1, "Backend-Entwicklung"),
    FRONTEND(2, "Frontend-Entwicklung"),
    IT_SICHERHEIT(3, "IT-Sicherheit"),
    WARTUNG_INSTALLATION(4, "Wartung & Installation"),
    CLOUD_VIRTUALISIERUNG(5, "Cloud & Virtualisierung");

    private final int CATEGORY_ID;
    private final String CATEGORY_NAME;



    Category(int CATEGORY_ID, String CATEGORY_NAME) {
        this.CATEGORY_ID = CATEGORY_ID;
        this.CATEGORY_NAME = CATEGORY_NAME;
    }

    public int getCATEGORY_ID() {
        return CATEGORY_ID;
    }

    public String getCATEGORY_NAME() {
        return CATEGORY_NAME;
    }

    public static Category getCategoryById(int id){
        for (Category c : values()) {
            if (c.CATEGORY_ID == id) {
                return c;
            }
        }
        return null;
    }
}
