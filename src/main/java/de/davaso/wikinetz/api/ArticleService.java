package de.davaso.wikinetz.api;

import de.davaso.wikinetz.model.*;
import java.util.List;

public interface ArticleService {
    Article addArticle(String title, String content, Category category, User author);
    List<Article> getAllArticles();
    Article findArticleById(int id);
    boolean deleteArticleById(int id);
    Article getArticleById(int articleId);
}

