package de.davaso.wikinetz.web;

import de.davaso.wikinetz.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryPageController {

    private final ArticleService articleService;

    public CategoryPageController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{category}")
    public String showCategory(@PathVariable String category, Model model) {
        var articles = articleService.findAll().stream()
                .filter(a -> a.getCategory().name().equalsIgnoreCase(category))
                .toList();
        model.addAttribute("categoryName", category);
        model.addAttribute("articles", articles);
        return "category-list";
    }
}
