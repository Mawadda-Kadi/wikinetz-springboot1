package de.davaso.wikinetz.service;

import de.davaso.wikinetz.exception.ArticleNotFoundException;
import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Category;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article addArticle(String title, String content, Category category, User author) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setCategory(category);
        article.setCreator(author);
        return articleRepository.save(article);
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article getById(int id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
    }

    public Article updateArticle(int id, String title, String content, Category category) {
        Article article = getById(id);
        if (title != null && !title.isBlank()) article.setTitle(title);
        if (content != null) article.setContent(content);
        if (category != null) article.setCategory(category);
        return articleRepository.save(article);
    }

    public void deleteArticle(int id) {
        articleRepository.deleteById(id);
    }
}

