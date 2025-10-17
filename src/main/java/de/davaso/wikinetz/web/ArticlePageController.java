package de.davaso.wikinetz.web;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Category;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.service.ArticleService;
import de.davaso.wikinetz.service.UserService;
import de.davaso.wikinetz.web.form.ArticleForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/articles")
public class ArticlePageController {

    private final ArticleService articleService;
    private final UserService userService;

    public ArticlePageController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping
    public String listArticles(Model model) {
        model.addAttribute("articles", articleService.findAll());
        return "category-list"; // or a new template like article-list.html
    }

    @GetMapping("/{id}")
    public String showArticle(@PathVariable int id, Model model) {
        Article article = articleService.getById(id);
        model.addAttribute("article", article);
        return "article-detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("articleForm", new ArticleForm());
        model.addAttribute("categories", Category.values());
        return "article-form";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("articleForm") ArticleForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            return "article-form";
        }

        var author = userService.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        articleService.createArticle(form.getTitle(), form.getContent(), form.getCategory(), author);

        // Flash Message
        redirectAttributes.addFlashAttribute("successMessage", "Artikel wurde erfolgreich erstellt ✅");

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        Article article = articleService.getById(id);

        ArticleForm form = new ArticleForm();
        form.setTitle(article.getTitle());
        form.setContent(article.getContent());
        form.setCategory(article.getCategory());

        model.addAttribute("articleForm", form);
        model.addAttribute("categories", Category.values());
        model.addAttribute("articleId", id);

        return "article-form";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable int id,
            @Valid @ModelAttribute("articleForm") ArticleForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            model.addAttribute("articleId", id);
            return "article-form";
        }

        Article article = articleService.getById(id);
        article.setTitle(form.getTitle());
        article.setContent(form.getContent());
        article.setCategory(form.getCategory());
        articleService.save(article);

        // Flash Message
        redirectAttributes.addFlashAttribute("successMessage", "Artikel wurde erfolgreich aktualisiert");

        return "redirect:/articles/" + id;
    }
}
