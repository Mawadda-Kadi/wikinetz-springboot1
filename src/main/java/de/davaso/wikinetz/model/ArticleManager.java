package de.davaso.wikinetz.model;

import java.util.ArrayList;
import java.util.List;


public class ArticleManager {

    // Speichert alle Artikel im Speicher, während das Programm läuft
    private final List<Article> articles = new ArrayList<>();

    // Gibt jedem Artikel eine eindeutige, fortlaufende ID
    private int nextArticleId = 1;

    // Erstellt einen neuen Artikel und fügt ihn der Liste hinzu.
    public Article addArticle(String title, String content) {
        Article a = new Article(nextArticleId++, title, content);
        articles.add(a);
        return a;
    }

    // Gibt eine Kopie aller Artikel zurück
    public List<Article> getAllArticles() {
        return new ArrayList<>(articles);
    }

    // Findet einen Artikel per ID oder gibt null zurück
    public Article findArticleById(int id) {
        for (Article a : articles) {
            if (a.getArticleId() == id) {
                return a;
            }
        }
        return null;
    }

    /**
     * Löscht einen Artikel per ID
     * @return true wenn gelöscht, sonst false
     */
    public boolean deleteArticleById(int id) {
        for (int i = 0; i < articles.size(); i++) {
            if (articles.get(i).getArticleId() == id) {
                articles.remove(i);
                return true;
            }
        }
        return false;
    }
}