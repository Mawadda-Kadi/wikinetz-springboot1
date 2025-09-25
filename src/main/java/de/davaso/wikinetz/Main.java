package de.davaso.wikinetz;

import de.davaso.wikinetz.api.PasswordHasher;
import de.davaso.wikinetz.manager.ArticleManager;
import de.davaso.wikinetz.manager.MediaManager;
import de.davaso.wikinetz.manager.UserStore;
import de.davaso.wikinetz.manager.VersionManager;
import de.davaso.wikinetz.model.*;
import de.davaso.wikinetz.service.AuthServiceImp;
import de.davaso.wikinetz.service.BCryptPasswordHasher;

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
    private static PasswordHasher hasher = new BCryptPasswordHasher(12);
    private static final UserStore userStore = new UserStore(hasher);
    private static final AuthServiceImp auth = new AuthServiceImp(userStore, hasher);
    public static void main(String[] args) { System.out.println("Hello, WikiNetz!");
        userStore.ensureAdmin("admin", "admin123", "admin@example.com");
        startWiki();
    }

    private static void startWiki() {
        boolean running = true; while (running) {
            printArticlesMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> login();
                case "2" -> registerUser();
                case "3" -> addArticle();
                case "4" -> listArticles();
                case "5" -> viewArticleById();
                case "6" -> searchArticles();
                case "7" -> showMediaForArticle();
                case "8" -> editArticle();
                case "9" -> deleteMediaById();
                case "10" -> deleteArticleById();
                case "11" -> versionToolsMenu();
                case "12" -> manageUsers();
                case "13" -> logout();
                case "0" -> { running = false; System.out.println("Programm beendet."); }
                default -> System.out.println("Ungültige Option. Bitte erneut versuchen.");
            }
        }
    }
    private static void printArticlesMenu() { System.out.println("\n === Menu: ===");
        System.out.println("1) Login");
        System.out.println("2) Registrieren");
        System.out.println("3) Artikel hinzufügen [ADMIN/BENUTZER]");
        System.out.println("4) Artikel auflisten");
        System.out.println("5) " + "Artikeldetails anzeigen (per ID)");
        System.out.println("6) Artikel suchen (Titel/Inhalt/Kategorie)");
        System.out.println("7) Medien anzeigen (per Artikel-ID)");
        System.out.println("8) Artikel bearbeiten [ADMIN/BENUTZER]");
        System.out.println("9) Medien löschen (per Medien-ID) [ADMIN/BENUTZER]");
        System.out.println("10) Artikel löschen (per ID) [ADMIN/ursprünglicher Autor]");
        System.out.println("11) Änderungsverlauf anzeigen (per Artikel-ID)");
        System.out.println("12) Benutzer verwalten [ADMIN]");
        System.out.println("13) Logout");
        System.out.println("0) Beenden");
        // ---- AUTH ----
        System.out.print("Status: ");
        auth.getCurrentUser().ifPresentOrElse( u -> System.out.println(u.getUsername() + " (" + u.getRole() + ") ist eingeloggt."), () -> System.out.println("Niemand eingeloggt.") );
        System.out.print("=== Deine Wahl: ===\n");
    }

    // ---- Media Snapshot ----
    private static java.util.List<MediaSnapshot> currentMediaSnapshots(int articleId) {
        java.util.List<Media> media = mediaManager.getMediaByArticleId(articleId);
        java.util.List<MediaSnapshot> snaps = new java.util.ArrayList<>();
        for (Media m : media) { snaps.add(new MediaSnapshot(m.getFilename(), m.getFilepath(), m.getType()));
        } return snaps; }

// ==================ARTIKEL CRUD & MEDIEN ==================
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
    addMediaToArticle(a.getArticleId());

    // v1 erstellen
    var snaps = currentMediaSnapshots(a.getArticleId());
    versionManager.ensureInitial(a, snaps);
    System.out.println("Hinzugefügt: " + a);
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

        // ================== Switch-Case MediaType Auswahl ==================
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

private static MediaType promptMediaTypeAllowEmpty(boolean allowEmpty) {
    System.out.println("1) IMAGE");
    System.out.println("2) VIDEO");
    System.out.println("3) LINK");
    if (allowEmpty) System.out.println("(Enter = unverändert)");
    System.out.print("Deine Wahl: ");
    String s = scanner.nextLine().trim();
    if (s.isEmpty() && allowEmpty) return null;

    return switch (s) {
        case "1" -> MediaType.IMAGE;
        case "2" -> MediaType.VIDEO;
        case "3" -> MediaType.LINK;
        default -> null;
    };
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
        System.out.println("-----");
        System.out.println("Geändert:  " + a.getUpdatedAt());
        var latestOpt = versionManager.latest(id);
        if (latestOpt.isPresent()) {
            Version v = latestOpt.get();
            System.out.println("Letzte Änderung: v" + v.getVersionNumber()
                    + " | " + v.getCreatedAt()
                    + " | von " + (v.getEditorUsername() != null ? v.getEditorUsername() : "SYSTEM")
                    + " | " + v.getNote());
        } else {
            System.out.println("Letzte Änderung: (keine Versionsdaten)");
        }

        System.out.println("-----");

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

// ================== Bearbeiten & Snapshot ==================
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

        // Kategorie bearbeiten
        System.out.print("Kategorie ändern? (j/n): ");
        String changeCat = scanner.nextLine().trim().toLowerCase();
        if (changeCat.equals("j")) {
            Category newCategory = askForCategory();
            if (newCategory != null) {
                article.setCategory(newCategory);
            }
        }
        // Medien bearbeiten
        boolean mediaChanged = editMediaForArticle(article.getArticleId());
        // EIN Snapshot am Ende mit aktueller Medienlage
        Optional<User> editorOpt = auth.getCurrentUser();
        var snaps = currentMediaSnapshots(article.getArticleId());
        String note = mediaChanged ? "Bearbeitung (inkl. Medien)" : "Bearbeitung";

        // Version speichern
        versionManager.snapshot(article, editorOpt.orElse(null), note, snaps);
        String editorName = editorOpt.map(User::getUsername).orElse("SYSTEM");
        System.out.println("Artikel wurde aktualisiert und Version gespeichert (Editor: " + editorName + ").");

    } catch (NumberFormatException e) {
        System.out.println("Ungültige Artikel-ID.");
    }
}

private static boolean editMediaForArticle(int articleId) {
    boolean anyChange = false;
    while (true) {
        System.out.println("\n=== Medien bearbeiten (Artikel-ID " + articleId + ") ===");
        System.out.println("1) Medien auflisten");
        System.out.println("2) Medium hinzufügen");
        System.out.println("3) Medium bearbeiten");
        System.out.println("4) Medium löschen");
        System.out.println("0) Fertig");
        System.out.print("Deine Wahl: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> {
                List<Media> media = mediaManager.getMediaByArticleId(articleId);
                if (media.isEmpty()) {
                    System.out.println("Keine Medien vorhanden.");
                } else {
                    System.out.println("Medien:");
                    for (Media m : media) {
                        System.out.println("- ID: " + m.getMediaId());
                        System.out.println("  Dateiname: " + m.getFilename());
                        System.out.println("  Pfad: " + m.getFilepath());
                        System.out.println("  Typ: " + m.getType());
                        System.out.println();
                    }
                }
            }
            case "2" -> {
                System.out.print("Dateiname: ");
                String filename = scanner.nextLine();
                System.out.print("Dateipfad/URL: ");
                String filepath = scanner.nextLine();
                MediaType type = promptMediaTypeAllowEmpty(false); // zwingend wählen
                if (type == null) {
                    System.out.println("Abgebrochen: ungültiger Medientyp.");
                } else {
                    var m = mediaManager.addMedia(articleId, filename, filepath, type);
                    System.out.println("Hinzugefügt: " + m);
                    anyChange = true;
                }
            }
            case "3" -> {
                System.out.print("Medien-ID zum Bearbeiten: ");
                String idStr = scanner.nextLine().trim();
                try {
                    int mediaId = Integer.parseInt(idStr);
                    var m = mediaManager.findById(mediaId);
                    if (m == null || m.getArticleId() != articleId) {
                        System.out.println("Kein Medium mit dieser ID für diesen Artikel gefunden.");
                        break;
                    }
                    System.out.println("Aktueller Dateiname: " + m.getFilename());
                    System.out.print("Neuer Dateiname (leer = unverändert): ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) newName = null;

                    System.out.println("Aktueller Pfad/URL: " + m.getFilepath());
                    System.out.print("Neuer Pfad/URL (leer = unverändert): ");
                    String newPath = scanner.nextLine().trim();
                    if (newPath.isEmpty()) newPath = null;

                    System.out.println("Aktueller Typ: " + m.getType());
                    System.out.println("Neuen Typ wählen (leer = unverändert):");
                    MediaType newType = promptMediaTypeAllowEmpty(true); // optional

                    boolean ok = mediaManager.updateMedia(mediaId, newName, newPath, newType);
                    if (ok) {
                        System.out.println("Medium aktualisiert.");
                        anyChange = true;
                    } else {
                        System.out.println("Aktualisierung fehlgeschlagen.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Bitte eine gültige Zahl eingeben.");
                }
            }
            case "4" -> {
                System.out.print("Medien-ID zum Löschen: ");
                String idStr = scanner.nextLine().trim();
                try {
                    int mediaId = Integer.parseInt(idStr);
                    var m = mediaManager.findById(mediaId);
                    if (m == null || m.getArticleId() != articleId) {
                        System.out.println("Kein Medium mit dieser ID für diesen Artikel gefunden.");
                        break;
                    }
                    boolean deleted = mediaManager.deleteMediaById(mediaId);
                    if (deleted) {
                        System.out.println("Medium gelöscht.");
                        anyChange = true;
                    } else {
                        System.out.println("Löschen fehlgeschlagen.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Bitte eine gültige Zahl eingeben.");
                }
            }
            case "0" -> {
                return anyChange;
            }
            default -> System.out.println("Ungültige Option.");
        }
    }
}

// ================== Version Tools ==================
private static void versionToolsMenu() {
    boolean back = false;
    while (!back) {
        System.out.println("\n=== Version-Tools ===");
        System.out.println("1) Neueste Version anzeigen (per Artikel-ID)");
        System.out.println("2) Anzahl Versionen anzeigen (per Artikel-ID)");
        System.out.println("3) Alle Versionen aller Artikel anzeigen");
        System.out.println("4) Einzelne Artikelversion anzeigen (per Version-ID)");
        System.out.println("5) Artikelversion wiederherstellen [ADMIN/ursprünglicher Autor]");
        System.out.println("6) Versionsverlauf für EINEN Artikel löschen [ADMIN]");
        System.out.println("0) Zurück");
        System.out.print("Deine Wahl: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> showLatestVersion();
            case "2" -> showVersionCount();
            case "3" -> listAllVersionsAcrossArticles();
            case "4" -> showSingleArticleVersion();
            case "5" -> restoreArticleVersion();
            case "6" -> clearVersionHistoryForArticle();
            case "0" -> back = true;
            default  -> System.out.println("Ungültige Option.");
        }
    }
}

private static void showLatestVersion() {
    System.out.print("Artikel-ID: ");
    String idStr = scanner.nextLine().trim();
    try {
        int articleId = Integer.parseInt(idStr);
        var opt = versionManager.latest(articleId);
        if (opt.isEmpty()) {
            System.out.println("Keine Versionen gefunden.");
            return;
        }
        Version v = opt.get();
        System.out.println("Neueste Version für Artikel " + articleId + ":");
        System.out.println("v" + v.getVersionNumber() + " | " + v.getCreatedAt() + " | " +
                (v.getEditorUsername() != null ? v.getEditorUsername() : "SYSTEM") + " | " + v.getNote());
    } catch (NumberFormatException e) {
        System.out.println("Bitte eine gültige Zahl eingeben.");
    }
}

private static void showVersionCount() {
    System.out.print("Artikel-ID: ");
    String idStr = scanner.nextLine().trim();
    try {
        int articleId = Integer.parseInt(idStr);
        int c = versionManager.count(articleId);
        System.out.println("Anzahl Versionen für Artikel " + articleId + ": " + c);
    } catch (NumberFormatException e) {
        System.out.println("Bitte eine gültige Zahl eingeben.");
    }
}

private static void listAllVersionsAcrossArticles() {
    var allArticles = manager.getAllArticles();
    if (allArticles.isEmpty()) {
        System.out.println("Keine Artikel vorhanden.");
        return;
    }
    boolean any = false;
    for (Article a : allArticles) {
        var versions = versionManager.listVersions(a.getArticleId());
        if (versions.isEmpty()) continue;
        any = true;
        System.out.println("\nÄnderungsverlauf für Artikel " + a.getArticleId() + " – " + a.getTitle() + ":");
        for (Version v : versions) {
            System.out.println("v" + v.getVersionNumber() + "  |  " +
                    v.getCreatedAt() + "  |  " +
                    (v.getEditorUsername() != null ? v.getEditorUsername() : "SYSTEM") + "  |  " +
                    v.getNote());
        }
    }
    if (!any) {
        System.out.println("Es gibt noch keine Versionen in irgendeinem Artikel.");
    }
}

private static void showSingleArticleVersion() {
    System.out.print("Artikel-ID: ");
    String idStr = scanner.nextLine().trim();
    try {
        int articleId = Integer.parseInt(idStr);

        Article article = manager.findArticleById(articleId);
        if (article == null) {
            System.out.println("Kein Artikel mit der ID " + articleId);
            return;
        }

        System.out.print("Versionsnummer: ");
        String vStr = scanner.nextLine().trim();
        int vNo = Integer.parseInt(vStr);

        java.util.Optional<Version> opt = versionManager.getVersion(articleId, vNo);
        if (opt.isEmpty()) {
            System.out.println("Version v" + vNo + " nicht gefunden.");
            return;
        }
        Version v = opt.get();

        System.out.println("\n----- Version-Details -----");
        System.out.println("Artikel-ID: " + articleId);
        System.out.println("Aktueller Titel: " + article.getTitle());
        System.out.println("Version: v" + v.getVersionNumber() + "  |  " + v.getCreatedAt());
        System.out.println("Editor: " + (v.getEditorUsername() != null ? v.getEditorUsername() : "SYSTEM"));
        System.out.println("Notiz:  " + v.getNote());
        System.out.println("Titel (Snapshot):   " + v.getTitle());
        System.out.println("Inhalt (Snapshot):  " + v.getContent());
        System.out.println("Kategorie (Snapshot): " + (v.getCategory() != null ? v.getCategory().getCategoryName() : "-"));

        var mediaSnaps = v.getMedia();
        if (mediaSnaps == null || mediaSnaps.isEmpty()) {
            System.out.println("\nMedien (Snapshot): Keine");
        } else {
            System.out.println("\nMedien (Snapshot):");
            for (MediaSnapshot ms : mediaSnaps) {
                System.out.println("- " + ms.getType() + " | " + ms.getFilename() + " | " + ms.getFilepath());
            }
        }

    } catch (NumberFormatException e) {
        System.out.println("Bitte eine gültige Zahl eingeben.");
    }
}


private static void restoreArticleVersion() {
    if (!auth.isLoggedIn()) {
        System.out.println("Zugriff verweigert. Bitte zuerst einloggen.");
        return;
    }

    System.out.print("Artikel-ID: ");
    String idStr = scanner.nextLine().trim();
    try {
        int articleId = Integer.parseInt(idStr);
        Article a = manager.findArticleById(articleId);
        if (a == null) {
            System.out.println("Kein Artikel mit der ID " + articleId);
            return;
        }

        User current = auth.getCurrentUser().orElse(null);
        boolean isAdmin = auth.hasAnyRole(Role.ADMIN);
        boolean isOriginalAuthor = current != null && current.getUserId() == a.getCreatorId();
        if (!isAdmin && !isOriginalAuthor) {
            System.out.println("Zugriff verweigert. Nur ADMIN oder der ursprüngliche Autor darf Versionen wiederherstellen.");
            return;
        }

        // Verfügbare Versionen anzeigen
        var versions = versionManager.listVersions(articleId);
        if (versions.isEmpty()) {
            System.out.println("Keine Versionen gefunden.");
            return;
        }
        System.out.println("Änderungsverlauf für Artikel " + articleId + ":");
        for (Version v : versions) {
            System.out.println("v" + v.getVersionNumber() + "  |  " +
                    v.getCreatedAt() + "  |  " +
                    (v.getEditorUsername() != null ? v.getEditorUsername() : "SYSTEM") + "  |  " +
                    v.getNote());
        }

        System.out.print("Welche Versionsnummer wiederherstellen? ");
        String vStr = scanner.nextLine().trim();
        int vNo = Integer.parseInt(vStr);

        boolean ok = versionManager.restore(a, vNo, current, "Wiederhergestellt aus v" + vNo);
        if (ok) {
            System.out.println("Artikel wurde auf v" + vNo + " zurückgesetzt und eine neue Version gespeichert.");
        } else {
            System.out.println("Version v" + vNo + " existiert nicht.");
        }
        // Restore Media
        var restored = versionManager.getVersion(articleId, vNo);
        restored.ifPresent(ver -> mediaManager.replaceAllForArticle(articleId, ver.getMedia()));

    } catch (NumberFormatException e) {
        System.out.println("Bitte eine gültige Zahl eingeben.");
    }
}

private static void clearVersionHistoryForArticle() {

    System.out.print("Artikel-ID: ");
    String idStr = scanner.nextLine().trim();
    try {
        int articleId = Integer.parseInt(idStr);
        Article a = manager.findArticleById(articleId);
        if (a == null) {
            System.out.println("Kein Artikel mit der ID " + articleId);
            return;
        }
        User current = auth.getCurrentUser().orElse(null);
        boolean isAdmin = auth.hasAnyRole(Role.ADMIN);
        boolean isOriginalAuthor = (current != null && a.getCreatorId() == current.getUserId());

        if (!isAdmin && !isOriginalAuthor) {
            System.out.println("Zugriff verweigert. Nur ADMIN oder der ursprüngliche Autor darf löschen.");
            return;
        }

        System.out.println("WARNUNG: Dies löscht den kompletten Versionsverlauf NUR für Artikel "
                + articleId + " („" + a.getTitle() + "“).");
        System.out.print("Zum Bestätigen 'LÖSCHEN " + articleId + "' eingeben (oder Enter zum Abbrechen): ");
        String confirm = scanner.nextLine().trim();
        if (!("LÖSCHEN " + articleId).equals(confirm)) {
            System.out.println("Abgebrochen.");
            return;
        }

        boolean ok = versionManager.clearForArticle(articleId);
        if (ok) {
            System.out.println("Versionsverlauf für Artikel " + articleId + " wurde gelöscht.");

        } else {
            System.out.println("Kein Versionsverlauf vorhanden oder bereits leer.");
        }

    } catch (NumberFormatException e) {
        System.out.println("Bitte eine gültige Zahl eingeben.");
    }
}


// ================== SEARCH ==================
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
            .filter(a -> filterCat == null || a.getCategory() == filterCat)
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

    userStore.updatePassword(target, pw1); // User kümmert sich um Hashing via PasswordUtil
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

        /*
        auth.getCurrentUser()
        Gibt ein Optional<User> zurück.
        Leer, wenn niemand eingeloggt ist; andernfalls enthält es den aktuellen User.

        .map(User::getUsername)
        Wenn ein User vorhanden ist, wird er in seinen Benutzernamen (String) umgewandelt.
        Das Ergebnis ist nun ein Optional<String>.

        .filter(username::equals)
        Behält den Wert nur dann, wenn er dem im Konsolenfeld eingegebenen username entspricht.
        Passt er nicht, wird das Optional leer.

        .isPresent()
        Prüft, ob nach dem Filtern noch ein Wert vorhanden ist.

        :: ist der Methodenreferenz-Operator.
        Er verweist auf eine Methode, ohne sie sofort auszuführen,
        z. B. User::getUsername bedeutet:
        „Benutze die Methode getUsername() jedes User-Objekts“.
        */

    // Schutz: nicht sich selbst löschen
    if (auth.getCurrentUser().map(User::getUsername).filter(username::equals).isPresent()) {
        System.out.println("Du kannst dich nicht selbst löschen.");
        return;
    }

    // Schutz: Standard-Admin nicht löschen
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
/* Diese Methode zählt, wie viele aktive Admin-Benutzer es gibt.
    Dies wird verwendet, um das System davor zu schützen, versehentlich
    - den letzten Admin zu löschen,
    - den letzten Admin zu deaktivieren.
    - oder die Rolle des letzten Admins zu ändern.
 */
private static int countEnabledAdmins() {
    int count = 0;
    for (User u : userStore.listAll()) {
        if (u.getRole() == Role.ADMIN && u.isEnabled()) {
            count++;
        }
    }
    return count;
}

// ================== USERS (Register & List) ==================
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

// ================== AUTH helpers ==================
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












































































