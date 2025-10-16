package de.davaso.wikinetz.web;


import de.davaso.wikinetz.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ArticleService articleService;

    public HomeController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Welcome to WikiNetz");
        model.addAttribute("articles", articleService.findAll());
        return "index"; // src/main/resources/templates/index.html
    }
}
