package de.davaso.wikinetz.service;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Category;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * Return all articles.
     */
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    /**
     * Find one article by ID or throw an exception.
     */
    public Article getById(int id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID " + id));
    }

    public Optional<Article> findById(int id) {
        return articleRepository.findById(id);
    }

    /**
     * Create a new article.
     */
    public Article createArticle(String title, String content, Category category, User author) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setCategory(category);
        article.setCreator(author);
        return articleRepository.save(article);
    }

    /**
     * Update or save article.
     */
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    /**
     * Delete article by ID.
     */
    public void deleteById(int id) {
        articleRepository.deleteById(id);
    }
}
