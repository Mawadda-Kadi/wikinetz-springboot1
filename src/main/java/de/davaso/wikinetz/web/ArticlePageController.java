package de.davaso.wikinetz.web;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Category;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.service.ArticleService;
import de.davaso.wikinetz.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/articles")
public class ArticlePageController {

    private final ArticleService articleService;
    private final UserService userService;

    public ArticlePageController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String showArticle(@PathVariable int id, Model model) {
        Article article = articleService.getById(id);
        model.addAttribute("article", article);
        return "article-detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("categories", Category.values());
        return "article-form";
    }

    @PostMapping("/create")
    public String create(@RequestParam String title,
                         @RequestParam String content,
                         @RequestParam Category category) {

        // Beispiel: System- oder Admin-Benutzer als Autor verwenden
        User author = userService.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        articleService.createArticle(title, content, category, author);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        Article article = articleService.getById(id);
        model.addAttribute("article", article);
        model.addAttribute("categories", Category.values());
        return "article-form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable int id,
                         @RequestParam String title,
                         @RequestParam String content,
                         @RequestParam Category category) {
        Article article = articleService.getById(id);
        article.setTitle(title);
        article.setContent(content);
        article.setCategory(category);
        articleService.save(article);
        return "redirect:/articles/" + id;
    }
}
