package de.davaso.wikinetz;

import de.davaso.wikinetz.manager.ArticleManager;
import de.davaso.wikinetz.manager.MediaManager;
import de.davaso.wikinetz.manager.UserStore;
import de.davaso.wikinetz.manager.VersionManager;
import de.davaso.wikinetz.model.*;
import de.davaso.wikinetz.service.AuthService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class Main {
    // ---- Scanner ----
    private static final Scanner scanner = new Scanner(System.in);

    // ---- Managers ----
    private static final ArticleManager manager = new ArticleManager();
    private static final MediaManager mediaManager = new MediaManager();
    private static final VersionManager versionManager = new VersionManager();

    // ---- AUTH ----
    private static final UserStore userStore = new UserStore();
    private static final AuthService auth = new AuthService(userStore);

    public static void main(String[] args) {
        System.out.println("Hello, WikiNetz!");
        userStore.ensureAdmin("admin", "admin123", "admin@example.com");
        startWiki();
    }

    private static void startWiki() {
        boolean running = true;
        while (running) {
            printArticlesMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1"  -> login();
                case "2"  -> registerUser();
                case "3"  -> addArticle();
                case "4"  -> listArticles();
                case "5"  -> viewArticleById();
                case "6"  -> searchArticles();
                case "7"  -> showMediaForArticle();
                case "8"  -> editArticle();
                case "9"  -> deleteMediaById();
                case "10" -> deleteArticleById();
                case "11" -> showArticleVersions();
                case "12" -> manageUsers();
                case "13" -> logout();
                case "0"  -> {
                    running = false;
                    System.out.println("Programm beendet.");
                }
                default -> System.out.println("Ungültige Option. Bitte erneut versuchen.");
            }
        }
    }

    private static void printArticlesMenu() {
        System.out.println("\n === Menu: ===");
        System.out.println("1)  Login");
        System.out.println("2)  Registrieren");
        System.out.println("3)  Artikel hinzufügen [ADMIN/BENUTZER]");
        System.out.println("4)  Artikel auflisten");
        System.out.println("5)  Artikeldetails anzeigen (per ID)");
        System.out.println("6)  Artikel suchen (Titel/Inhalt/Kategorie)");
        System.out.println("7)  Medien anzeigen (per Artikel-ID)");
        System.out.println("8)  Artikel bearbeiten [ADMIN/BENUTZER]");
        System.out.println("9)  Medien löschen (per Medien-ID) [ADMIN/BENUTZER]");
        System.out.println("10) Artikel löschen (per ID) [ADMIN/ursprünglicher Autor]");
        System.out.println("11) Änderungsverlauf anzeigen (per Artikel-ID)");
        System.out.println("12) Benutzer verwalten [ADMIN]");
        System.out.println("13) Logout");
        System.out.println("0)  Beenden");
        // ---- AUTH ----
        System.out.print("Status: ");
        auth.getCurrentUser().ifPresentOrElse(
                u -> System.out.println(u.getUsername() + " (" + u.getRole() + ") ist eingeloggt."),
                () -> System.out.println("Niemand eingeloggt.")
        );
        System.out.print("=== Deine Wahl: ===\n");
    }

    // ---------- ARTIKEL CRUD & MEDIEN ----------
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

        // set original author (logged-in user)
        User author = auth.getCurrentUser().orElse(null);
        Article a = manager.addArticle(title, content, category, author);
        // Erstversion anlegen
        versionManager.ensureInitial(a);
        System.out.println("Hinzugefügt: " + a);
        // Medien hinzufügen
        addMediaToArticle(a.getArticleId());
    }

    private static void addMediaToArticle(int articleId) {
        System.out.println("=== Möchtest du Medien zum Artikel hinzufügen? (j/n) ===");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (!answer.equals("j")) return;

        while (true) {
            System.out.print("Dateiname: ");
            String filename = scanner.nextLine();
            System.out.print("Dateipfad: ");
            String filepath = scanner.nextLine();

            // --- Switch-Case MediaType Auswahl ---
            System.out.println("=== Medientyp auswählen: ===");
            System.out.println("1) IMAGE");
            System.out.println("2) VIDEO");
            System.out.println("3) LINK");
            System.out.print("Deine Wahl: ");
            String typeChoice = scanner.nextLine().trim();

            MediaType type;
            switch (typeChoice) {
                case "1" -> type = MediaType.IMAGE;
                case "2" -> type = MediaType.VIDEO;
                case "3" -> type = MediaType.LINK;
                default -> {
                    System.out.println("Ungültige Auswahl. Abbruch dieses Mediums.");
                    type = null;
                }
            }

            if (type != null) {
                Media media = mediaManager.addMedia(articleId, filename, filepath, type);
                System.out.println("Medien hinzugefügt: " + media);
            }

            System.out.println("Noch ein Medium hinzufügen? (j/n)");
            String again = scanner.nextLine().trim().toLowerCase();
            if (!again.equals("j")) break;
        }
    }

    private static void showMediaForArticle() {
        System.out.print("Artikel-ID für Medienanzeige: ");
        String idStr = scanner.nextLine().trim();
        try {
            int articleId = Integer.parseInt(idStr);
            List<Media> mediaList = mediaManager.getMediaByArticleId(articleId);
            if (mediaList.isEmpty()) {
                System.out.println("Keine Medien für diesen Artikel gefunden.");
            } else {
                System.out.println("Medien für Artikel " + articleId + ":");
                for (Media m : mediaList) {
                    System.out.println("- ID: " + m.getMediaId());
                    System.out.println("  Dateiname: " + m.getFilename());
                    System.out.println("  Pfad: " + m.getFilepath());
                    System.out.println("  Typ: " + m.getType());
                    System.out.println();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Bitte eine gültige Zahl eingeben.");
        }
    }

    private static void deleteMediaById() {
        // VIEWER darf NICHT löschen
        if (!auth.hasAnyRole(Role.ADMIN, Role.BENUTZER)) {
            System.out.println("Zugriff verweigert. Nur ADMIN oder BENUTZER.");
            return;
        }
        System.out.print("Medien-ID zum Löschen: ");
        String idStr = scanner.nextLine().trim();
        try {
            int mediaId = Integer.parseInt(idStr);
            boolean deleted = mediaManager.deleteMediaById(mediaId);
            if (deleted) {
                System.out.println("Medienobjekt gelöscht: ID " + mediaId);
            } else {
                System.out.println("Kein Medienobjekt mit dieser ID gefunden.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Bitte eine gültige Zahl eingeben.");
        }
    }

    private static Category askForCategory() {
        while (true) {
            System.out.println("Kategorie auswählen:");
            for (Category c : Category.values()) {
                System.out.println(c.getCategoryId() + ") " + c.getCategoryName());
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
            System.out.println("Kategorie: " + (a.getCategory() != null ? a.getCategory().getCategoryName() : "-"));
            System.out.println("Autor:    " + a.getCreatorUsername());
            System.out.println(" (id=" + a.getCreatorId() + ")");
            System.out.println("Erstellt:  " + a.getCreatedAt());
            System.out.println("Geändert:  " + a.getUpdatedAt());
            System.out.println("-----");

            List<Media> mediaList = mediaManager.getMediaByArticleId(id);
            if (mediaList.isEmpty()) {
                System.out.println("Keine Medien zu diesem Artikel.");
            } else {
                System.out.println("Medien:");
                for (Media m : mediaList) {
                    System.out.println("- ID: " + m.getMediaId());
                    System.out.println("  Dateiname: " + m.getFilename());
                    System.out.println("  Pfad: " + m.getFilepath());
                    System.out.println("  Typ: " + m.getType());
                    System.out.println();
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Bitte eine Zahl eingeben.");

        }
    }

    private static void deleteArticleById() {
        if (!auth.isLoggedIn()) {
            System.out.println("Zugriff verweigert. Bitte zuerst einloggen.");
            return;
        }
        System.out.print("Artikel-ID zum Löschen eingeben: ");
        String idStr = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(idStr);
            Article a = manager.findArticleById(id);
            if (a == null) {
                System.out.println("Kein Artikel mit ID " + id);
                return;
            }

            User current = auth.getCurrentUser().orElse(null);
            boolean isAdmin = auth.hasAnyRole(Role.ADMIN);
            boolean isOriginalAuthor = (current != null && a.getCreatorId() == current.getUserId());

            if (!isAdmin && !isOriginalAuthor) {
                System.out.println("Zugriff verweigert. Nur ADMIN oder der ursprüngliche Autor darf löschen.");
                return;
            }
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

   private static void showArticleVersions() {
       System.out.print("Artikel-ID: ");
       String idStr = scanner.nextLine().trim();
       try {
           int articleId = Integer.parseInt(idStr);
           List<Version> versions = versionManager.listVersions(articleId);
           if (versions.isEmpty()) {
               System.out.println("Keine Versionen gefunden.");
           } else {
               System.out.println("Änderungsverlauf für Artikel " + articleId + ":");
               for (Version v : versions) {
                   System.out.println("v" + v.getVersionNumber() + "  |  " +
                           v.getCreatedAt() + "  |  " +
                           (v.getEditorUsername() != null ? v.getEditorUsername() : "SYSTEM") + "  |  " +
                           v.getNote());
               }
           }
       } catch (NumberFormatException e) {
           System.out.println("Ungültige Artikel-ID.");
       }
   }

    // ---- Bearbeiten & Snapshot ----
    private static void editArticle() {
        // VIEWER darf NICHT bearbeiten
        if (!auth.hasAnyRole(Role.ADMIN, Role.BENUTZER)) {
            System.out.println("Zugriff verweigert. Bitte einloggen mit Rolle ADMIN oder BENUTZER.");
            return;
        }

        System.out.print("Artikel-ID zum Bearbeiten: ");
        String idStr = scanner.nextLine().trim();
        try {
            int articleId = Integer.parseInt(idStr);
            Article article = manager.getArticleById(articleId);
            if (article == null) {
                System.out.println("Artikel nicht gefunden.");
                return;
            }

            System.out.println("Aktueller Titel: " + article.getTitle());
            System.out.print("Neuer Titel (leer lassen für keine Änderung): ");
            String newTitle = scanner.nextLine().trim();
            if (!newTitle.isEmpty()) {
                article.setTitle(newTitle);
            }

            System.out.println("Aktueller Inhalt: " + article.getContent());
            System.out.print("Neuer Inhalt (leer lassen für keine Änderung): ");
            String newContent = scanner.nextLine().trim();
            if (!newContent.isEmpty()) {
                article.setContent(newContent);
            }

            // Kategorie ändern
            System.out.print("Kategorie ändern? (j/n): ");
            String changeCat = scanner.nextLine().trim().toLowerCase();
            if (changeCat.equals("j")) {
                Category newCategory = askForCategory();
                if (newCategory != null) {
                    article.setCategory(newCategory);
                }
            }

            // Version speichern (Snapshot)
            Optional<User> editor = auth.getCurrentUser();
            versionManager.snapshot(
                    article,
                    editor.orElse(null),
                    "Bearbeitung"
            );

            System.out.println("Artikel wurde aktualisiert und Version gespeichert.");

        } catch (NumberFormatException e) {
            System.out.println("Ungültige Artikel-ID.");
        }
    }


    // ---------- SEARCH ----------
    private static void searchArticles() {
        System.out.print("Suchbegriff (Titel/Inhalt; leer = abbrechen): ");
        String q = scanner.nextLine().trim();
        if (q.isEmpty()) return;

        System.out.print("Nach Kategorie filtern? (j/n): ");
        String byCat = scanner.nextLine().trim().toLowerCase();

        // Nur EINMAL zuweisen -> effektiv final
        final Category filterCat = byCat.equals("j") ? askForCategory() : null;

        String qLower = q.toLowerCase();
        List<Article> all = manager.getAllArticles();
        all.stream()
                .filter(a ->
                        (a.getTitle() != null && a.getTitle().toLowerCase().contains(qLower)) ||
                                (a.getContent() != null && a.getContent().toLowerCase().contains(qLower))
                )
                .filter(a -> filterCat == null || a.getCategory() == filterCat) // ok, Category ist Enum, '==' passt
                .sorted(Comparator.comparingInt(Article::getArticleId))
                .forEach(a -> System.out.println(a.getArticleId() + " - " + a.getTitle() +
                        " [" + (a.getCategory() != null ? a.getCategory().getCategoryName() : "-") + "]"));
    }

    // ================== BENUTZER-VERWALTUNG (ADMIN) ==================
    private static void manageUsers() {
        if (!auth.hasAnyRole(Role.ADMIN)) {
            System.out.println("Zugriff verweigert. Nur ADMIN.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n=== Benutzerverwaltung (ADMIN) ===");
            System.out.println("1) Benutzer auflisten");
            System.out.println("2) Benutzer registrieren");
            System.out.println("3) Rolle ändern");
            System.out.println("4) Benutzer aktivieren/deaktivieren");
            System.out.println("5) Passwort zurücksetzen");
            System.out.println("6) Benutzer löschen");
            System.out.println("0) Zurück");
            System.out.print("Deine Wahl: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listUsers();
                case "2" -> registerUser();
                case "3" -> changeUserRole();
                case "4" -> toggleUserEnabled();
                case "5" -> resetUserPassword();
                case "6" -> deleteUser();
                case "0" -> back = true;
                default  -> System.out.println("Ungültige Option.");
            }
        }
    }

    private static void changeUserRole() {
        System.out.print("Username für Rollenänderung: ");
        String username = scanner.nextLine().trim();

        Optional<User> userOpt = userStore.findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("Benutzer nicht gefunden.");
            return;
        }
        User target = userOpt.get();

        // Rolle wählen
        Role newRole = promptRole("Neue Rolle wählen");
        if (newRole == null) {
            System.out.println("Abgebrochen.");
            return;
        }

        // Schutz: letzten aktiven Admin nicht entmachten
        if (target.getRole() == Role.ADMIN && newRole != Role.ADMIN && countEnabledAdmins() <= 1) {
            System.out.println("Abbruch: Das ist der letzte aktive ADMIN. Mindestens ein ADMIN muss erhalten bleiben.");
            return;
        }

        target.setRole(newRole);
        System.out.println("Rolle geändert: " + target.getUsername() + " -> " + target.getRole());
    }

    private static void toggleUserEnabled() {
        System.out.print("Username aktivieren/deaktivieren: ");
        String username = scanner.nextLine().trim();

        Optional<User> userOpt = userStore.findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("Benutzer nicht gefunden.");
            return;
        }
        User target = userOpt.get();

        System.out.println("Aktueller Status von " + target.getUsername() + ": enabled=" + target.isEnabled());
        System.out.print("Neuer Status? (e = enable, d = disable, Enter = abbrechen): ");
        String cmd = scanner.nextLine().trim().toLowerCase();

        if (cmd.isEmpty()) return;

        boolean enable;
        if (cmd.equals("e")) {
            enable = true;
        } else if (cmd.equals("d")) {
            // Schutz: letzten aktiven Admin nicht deaktivieren
            if (target.getRole() == Role.ADMIN && target.isEnabled() && countEnabledAdmins() <= 1) {
                System.out.println("Abbruch: Das ist der letzte aktive ADMIN. Mindestens ein ADMIN muss aktiv bleiben.");
                return;
            }
            enable = false;
        } else {
            System.out.println("Ungültige Eingabe.");
            return;
        }

        target.setEnabled(enable);
        System.out.println("Status aktualisiert: " + target.getUsername() + " -> enabled=" + target.isEnabled());
    }

    private static void resetUserPassword() {
        System.out.print("Username für Passwort-Reset: ");
        String username = scanner.nextLine().trim();

        Optional<User> userOpt = userStore.findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("Benutzer nicht gefunden.");
            return;
        }
        User target = userOpt.get();

        System.out.print("Neues Passwort: ");
        String pw1 = scanner.nextLine();
        System.out.print("Neues Passwort wiederholen: ");
        String pw2 = scanner.nextLine();

        if (!pw1.equals(pw2)) {
            System.out.println("Passwörter stimmen nicht überein.");
            return;
        }
        if (pw1.isBlank()) {
            System.out.println("Passwort darf nicht leer sein.");
            return;
        }

        target.setPassword(pw1); // User kümmert sich um Hashing via PasswordUtil
        System.out.println("Passwort aktualisiert für " + target.getUsername() + ".");
    }

    private static void deleteUser() {
        System.out.print("Username zum Löschen: ");
        String username = scanner.nextLine().trim();

        Optional<User> userOpt = userStore.findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("Benutzer nicht gefunden.");
            return;
        }
        User target = userOpt.get();

        // Schutz: nicht sich selbst löschen
        if (auth.getCurrentUser().map(User::getUsername).filter(username::equals).isPresent()) {
            System.out.println("Du kannst dich nicht selbst löschen.");
            return;
        }

        // Optionaler Schutz: Standard-Admin nicht löschen
        if (username.equalsIgnoreCase("admin")) {
            System.out.println("Schutz: Standard-Admin nicht löschen.");
            return;
        }

        // Schutz: letzten aktiven Admin nicht löschen
        if (target.getRole() == Role.ADMIN && target.isEnabled() && countEnabledAdmins() <= 1) {
            System.out.println("Abbruch: Das ist der letzte aktive ADMIN. Mindestens ein ADMIN muss aktiv bleiben.");
            return;
        }

        userStore.deleteByUsername(username);
        System.out.println("Benutzer (falls vorhanden) gelöscht: " + username);
    }

    private static Role promptRole(String title) {
        System.out.println(title + ":");
        System.out.println("1) ADMIN");
        System.out.println("2) BENUTZER");
        System.out.println("3) VIEWER");
        System.out.print("Deine Wahl: ");
        String r = scanner.nextLine().trim();
        return switch (r) {
            case "1" -> Role.ADMIN;
            case "2" -> Role.BENUTZER;
            case "3" -> Role.VIEWER;
            default  -> {
                System.out.println("Ungültig. Abbruch.");
                yield null;
            }
        };
    }

    private static int countEnabledAdmins() {
        int count = 0;
        for (User u : userStore.listAll()) {
            if (u.getRole() == Role.ADMIN && u.isEnabled()) {
                count++;
            }
        }
        return count;
    }

    // ---------- USERS (Register & List) ----------
    private static void registerUser() {
        System.out.println("=== Benutzer registrieren ===");
        System.out.print("Benutzername: ");
        String username = scanner.nextLine().trim();
        System.out.print("E-Mail: ");
        String email = scanner.nextLine().trim();
        System.out.print("Passwort: ");
        String password = scanner.nextLine();

        Role role;

        if (auth.hasAnyRole(Role.ADMIN)) {
            role = promptRole("=== Rolle wählen ===");
            if (role == null) {
                System.out.println("Abgebrochen.");
                return;
            }
        } else {
            role = Role.VIEWER;
            System.out.println("Hinweis: Ohne ADMIN-Rechte wird als VIEWER registriert.");
        }

        try {
            User u = userStore.register(username, password, email, role);
            System.out.println("Registriert: " + u.getUsername() + " (" + u.getRole() + ")");
            // Automatisches Login nur bei Selbstregistrierung (wenn momentan niemand eingeloggt ist)
            if (!auth.isLoggedIn()) {
                boolean loggedIn = auth.login(u.getUsername(), password);
                if (loggedIn) {
                    System.out.println("Du bist jetzt eingeloggt als " + u.getUsername() + " (" + u.getRole() + ").");
                } else {
                    System.out.println("Hinweis: Bitte jetzt mit deinen Zugangsdaten einloggen.");
                }
            } else if (auth.hasAnyRole(Role.ADMIN)) {
                System.out.println("Hinweis: Admin bleibt eingeloggt. Neuer Nutzer muss sich selbst einloggen.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Fehler: " + ex.getMessage());
        }
    }

    private static void listUsers() {
        if (!auth.hasAnyRole(Role.ADMIN)) {
            System.out.println("Zugriff verweigert. Nur ADMIN.");
            return;
        }
        System.out.println("=== Benutzerliste ===");
        for (User u : userStore.listAll()) {
            System.out.println("- " + u.getUsername() + " | " + u.getEmail() + " | Rolle: " + u.getRole() + " | enabled=" + u.isEnabled());
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












































































