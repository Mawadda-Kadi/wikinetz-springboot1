package de.davaso.wikinetz.web.form;

import de.davaso.wikinetz.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArticleForm {

    @NotBlank(message = "Titel darf nicht leer sein")
    private String title;

    private String content;

    @NotNull(message = "Bitte eine Kategorie auswählen")
    private Category category;

    // Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
