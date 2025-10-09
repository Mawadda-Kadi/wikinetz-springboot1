package de.davaso.wikinetz.api.dto;

import de.davaso.wikinetz.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArticleDtos {

    public record CreateArticleRequest(
            @NotBlank String title,
            String content,
            @NotNull Category category,
            @NotBlank String authorUsername
    ) {}

    public record UpdateArticleRequest(
            String title,
            String content,
            Category category,
            String note
    ) {}
}