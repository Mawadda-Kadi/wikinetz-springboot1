package de.davaso.wikinetz;

import de.davaso.wikinetz.model.*;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArticleManager manager = new ArticleManager();

    // ---- AUTH ----
    private static final UserStore userStore = new UserStore();
    private static final AuthService auth = new AuthService(userStore);

    public static void main(String[] args) {
        System.out.println("Hello, TeamWiki!");
        userStore.ensureAdmin("admin", "admin123", "admin@example.com");
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
                case "5" -> login();
                case "6" -> logout();
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
        System.out.println("1) Artikel hinzufügen");
        System.out.println("2) Artikel auflisten");
        System.out.println("3) Artikeldetails anzeigen (per ID)");
        System.out.println("4) Artikel löschen (per ID)");
        System.out.println("5) Login");
        System.out.println("6) Logout");
        System.out.println("0) Beenden");

        // ---- AUTH ----
        System.out.print("Status: ");
        if (auth.isLoggedIn()) {
            var u = auth.getCurrentUser().get();
            System.out.println(u.getUsername() + " (" + u.getRole() + ") ist eingeloggt.");
        } else {
            System.out.println("Niemand eingeloggt.");
        }
        // -------------------------

        System.out.print("Deine Wahl: ");
    }

    private static void addArticle() {
        // ---- AUTH: Erlaubt USER/ADMIN das Hinzufügen; VIEWER darf es nicht ----
        if (!auth.hasAnyRole(Role.ADMIN, Role.BENUTZER)) {
            System.out.println("Zugriff verweigert. Bitte einloggen mit Rolle ADMIN oder BENUTZER.");
            return;
        }
        System.out.print("Titel: ");
        String title = scanner.nextLine();
        System.out.print("Inhalt: ");
        String content = scanner.nextLine();

        Category category = askForCategory();
        if (category == null) {
            System.out.println("Abgebrochen: keine gültige Kategorie ausgewählt.");
            return;
        }

        Article a = manager.addArticle(title, content, category);
        System.out.println("Hinzugefügt: " + a);
    }

    private static Category askForCategory() {
        while (true) {
            System.out.println("Kategorie auswählen:");
            for (Category c : Category.values()) {
                System.out.println(c.getCATEGORY_ID() + ") " + c.getCATEGORY_NAME());
            }
            System.out.print("Deine Wahl (oder Enter zum Abbrechen): ");
            String catStr = scanner.nextLine().trim();
            if (catStr.isEmpty()) return null;

            try {
                int catId = Integer.parseInt(catStr);
                Category category = Category.getCategoryById(catId);
                if (category != null) return category;
                System.out.println("Bitte eine bestehende Kategorie auswählen.");
            } catch (NumberFormatException e) {
                System.out.println("Bitte eine Zahl eingeben.");
            }
        }
    }

    private static void listArticles() {
        List<Article> all = manager.getAllArticles();
        if (all.isEmpty()) {
            System.out.println("Keine Artikel vorhanden.");
            return;
        }
        all.sort(Comparator.comparingInt(Article::getArticleId));
        System.out.println("Artikel (id - Titel):" );
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
            System.out.println("Kategorie: " + (a.getCategory() != null ? a.getCategory().getCATEGORY_NAME() : "-"));
            System.out.println("Erstellt:  " + a.getCreated_at());
            System.out.println("Geändert:  " + a.getUpdated_at());
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

    // ---- AUTH helpers ----
    private static void login() {
        System.out.print("Benutzername: ");
        String username = scanner.nextLine().trim();
        System.out.print("Passwort: ");
        String password = scanner.nextLine();

        boolean ok = auth.login(username, password);
        if (ok) {
            System.out.println("Login erfolgreich.");
        } else {
            System.out.println("Login fehlgeschlagen (Benutzer unbekannt, Konto deaktiviert oder Passwort falsch).");
        }
    }

    private static void logout() {
        if (!auth.isLoggedIn()) {
            System.out.println("Niemand ist eingeloggt.");
            return;
        }
        auth.logout();
        System.out.println("Logout erfolgreich.");
    }
}