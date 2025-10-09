package de.davaso.wikinetz.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Welcome to WikiNetz");
        return "index"; // src/main/resources/templates/index.html
    }
}
