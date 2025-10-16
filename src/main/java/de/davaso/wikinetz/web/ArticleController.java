package de.davaso.wikinetz.web;

import de.davaso.wikinetz.dto.ArticleDtos.CreateArticleRequest;
import de.davaso.wikinetz.dto.ArticleDtos.UpdateArticleRequest;
import de.davaso.wikinetz.exception.InvalidArticleException;
import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.service.ArticleService;
import de.davaso.wikinetz.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;

    public ArticleController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    /**
     * List all articles.
     */
    @GetMapping
    public List<Article> list() {
        return articleService.findAll();
    }

    /**
     * Get a single article by ID.
     */
    @GetMapping("/{id}")
    public Article get(@PathVariable int id) {
        return articleService.getById(id);
    }

    /**
     * Create a new article.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Article create(@Valid @RequestBody CreateArticleRequest req) {
        User author = userService.findByUsername(req.authorUsername())
                .orElseThrow(() -> new InvalidArticleException("Unknown author: " + req.authorUsername()));

        return articleService.createArticle(
                req.title(),
                req.content(),
                req.category(),
                author
        );
    }

    /**
     * Update an existing article.
     */
    @PutMapping("/{id}")
    public Article update(@PathVariable int id, @RequestBody UpdateArticleRequest req) {
        Article article = articleService.getById(id);

        if (req.title() != null && !req.title().isBlank()) {
            article.setTitle(req.title());
        }
        if (req.content() != null) {
            article.setContent(req.content());
        }
        if (req.category() != null) {
            article.setCategory(req.category());
        }

        return articleService.save(article);
    }

    /**
     * Delete an article by ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        articleService.deleteById(id);
    }
}
