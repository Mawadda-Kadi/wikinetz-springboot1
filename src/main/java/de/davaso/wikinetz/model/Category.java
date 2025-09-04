package de.davaso.wikinetz.model;


public enum Category {
    BACKEND(1, "Backend-Entwicklung"),
    FRONTED(2, "Frontedend-Entwicklung"),
    IT_SISCHERHEIT(3, "IT-Sicherheit"),
    WARTUNG_INSTALLATION(4, "Wartung & Installation"),
    CLOUD_VIRTUALISIRUNG(5, "Cloud & Vertualisirung");

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


    @Override
    public String toString() {
        return "Category{ " +
                "Category_id= " + CATEGORY_ID +
                ", Category name= '" + CATEGORY_NAME + '\'';

    }
}
