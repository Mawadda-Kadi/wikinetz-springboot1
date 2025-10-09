package de.davaso.wikinetz.web;

import de.davaso.wikinetz.api.ArticleService;
import de.davaso.wikinetz.api.dto.ArticleDtos.CreateArticleRequest;
import de.davaso.wikinetz.api.dto.ArticleDtos.UpdateArticleRequest;
import de.davaso.wikinetz.exception.InvalidArticleException;
import de.davaso.wikinetz.manager.UserStore;
import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final UserStore userStore; // optional for author lookup

    public ArticleController(ArticleService articleService, UserStore userStore) {
        this.articleService = articleService;
        this.userStore = userStore;
    }

    @GetMapping
    public List<Article> list() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{id}")
    public Article get(@PathVariable int id) {
        return articleService.getArticleById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Article create(@Valid @RequestBody CreateArticleRequest req) {
        User author = userStore.findByUsername(req.authorUsername())
                .orElseThrow(() -> new InvalidArticleException("Unknown author: " + req.authorUsername()));

        return articleService.addArticle(
                req.title(),
                req.content(),
                req.category(),
                author
        );
    }

    @PutMapping("/{id}")
    public Article update(@PathVariable int id, @RequestBody UpdateArticleRequest req) {
        Article article = articleService.getArticleById(id);

        if (req.title() != null && !req.title().isBlank()) {
            article.setTitle(req.title());
        }
        if (req.content() != null) {
            article.setContent(req.content());
        }
        if (req.category() != null) {
            article.setCategory(req.category());
        }

        // You could also snapshot a version here if using VersionManager
        return article;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        articleService.deleteArticleById(id);
    }
}