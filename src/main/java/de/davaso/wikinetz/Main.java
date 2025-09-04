package de.davaso.wikinetz;

import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.ArticleManager;

import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArticleManager manager = new ArticleManager();

    public static void main(String[] args) {
        System.out.println("Hello, TeamWiki!");
        startWiki();
    }

    private static void startWiki() {
        boolean running = true;
        while (running) {
            printArticlesMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addArticle();
                case "2" -> listArticles();
                case "3" -> viewArticleById();
                case "4" -> deleteArticleById();
                case "0" -> {
                    running = false;
                    System.out.println("Bye");
                }
                default -> System.out.println("Ungültige Option. Bitte erneut versuchen.");
            }
        }
    }

    private static void printArticlesMenu() {
        System.out.println("\nMenu:");
        System.out.println("1) Add article");
        System.out.println("2) List articles");
        System.out.println("3) View article details by ID");
        System.out.println("4) Delete article by ID");
        System.out.println("0) Exit");
        System.out.print("Your choice: ");
    }

    private static void addArticle() {
        System.out.print("Titel: ");
        String title = scanner.nextLine();
        System.out.print("Inhalt: ");
        String content = scanner.nextLine();
        Article a = manager.addArticle(title, content);
        System.out.println("Hinzugefügt: " + a);
    }

    private static void listArticles() {
        List<Article> all = manager.getAllArticles();
        if (all.isEmpty()) {
            System.out.println("Keine Artikel vorhanden.");
            return;
        }
        System.out.println("Artikel (ID - Titel):");
        for (Article a : all) {
            System.out.println(a.getArticleId() + " - " + a.getTitle());
        }
    }

    private static void viewArticleById() {
        System.out.print("Artikel-ID zum Anzeigen eingeben: ");
        String idStr = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(idStr);
            Article a = manager.findArticleById(id);
            if (a == null) {
                System.out.println("Kein Artikel mit der ID " + id);
                return;
            }
            System.out.println("-----");
            System.out.println("ID:      " + a.getArticleId());
            System.out.println("Title:   " + a.getTitle());
            System.out.println("Inhalt:  " + a.getContent());
            System.out.println("-----");
        } catch (NumberFormatException e) {
            System.out.println("Bitte eine Zahl eingeben.");
        }
    }

    private static void deleteArticleById() {
        System.out.print("Artikel-ID zum Löschen eingeben: ");
        String idStr = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(idStr);
            boolean ok = manager.deleteArticleById(id);
            if (ok) {
                System.out.println("Gelöscht: " + id);
            } else {
                System.out.println("Kein Artikel mit ID " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Bitte eine Zahl eingeben.");
        }
    }
}