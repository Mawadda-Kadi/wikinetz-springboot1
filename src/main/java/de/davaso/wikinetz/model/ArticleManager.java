package de.davaso.wikinetz.model;


import java.util.*;


public class ArticleManager {

    // Speichert alle Artikel im Speicher, während das Programm läuft
    private final Map<Integer, Article> articles = new HashMap<>();

    // Gibt jedem Artikel eine eindeutige, fortlaufende ID
    private int nextArticleId = 1;


    // Erstellt einen neuen Artikel und fügt ihn der Liste hinzu.
    public Article addArticle(String title, String content, Category category) {
        Article a = new Article(nextArticleId++, title, content, category);
        articles.put(a.getArticleId(), a);
        return a;
    }

    // Gibt eine Kopie aller Artikel zurück
    public List<Article> getAllArticles() {
        return new ArrayList<>(articles.values());
    }

    // Findet einen Artikel per ID oder gibt null zurück
    public Article findArticleById(int id) {
        return articles.get(id);
    }

    /**
     * Löscht einen Artikel per ID
     * @return true wenn gelöscht, sonst false
     */

    public boolean deleteArticleById(int id) {
        return articles.remove(id) != null;

    }
  //editArticle
    public Article getArticleById(int articleId)
    {
        for (Article a : articles.values())
        {
            if (a.getArticleId() == articleId)
            {
                return a;

            }

        }
        return null;
    }
}

