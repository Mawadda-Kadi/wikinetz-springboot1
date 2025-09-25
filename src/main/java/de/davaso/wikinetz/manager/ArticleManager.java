package de.davaso.wikinetz.manager;


import de.davaso.wikinetz.api.ArticleService;
import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Category;
import de.davaso.wikinetz.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class ArticleManager implements ArticleService{

    // Speichert alle Artikel im Speicher, während das Programm läuft
    private final Map<Integer, Article> articles = new HashMap<>();
    //Zuerst Logger importieren und initialisieren
    private static final Logger logger = LoggerFactory.getLogger(ArticleManager.class);

    // Gibt jedem Artikel eine eindeutige, fortlaufende ID
    private int nextArticleId = 1;


    // Erstellt einen neuen Artikel und fügt ihn der Liste hinzu.
    public Article addArticle(String title, String content, Category category, User author) {
        int creatorId = (author != null) ? author.getUserId() : -1;
        String creatorUsername = (author != null) ? author.getUsername() : "SYSTEM";
        Article a = new Article(nextArticleId++, title, content, category, creatorId, creatorUsername);
        articles.put(a.getArticleId(), a);

        //Logging in Methoden einfügen
        logger.info("Artikel erstellt: ID={}, Titel='{}', Autor={}", a.getArticleId(), title, creatorUsername);
        return a;
    }

    // Gibt eine Kopie aller Artikel zurück
    public List<Article> getAllArticles() {
        logger.debug("Alle Artikel abgerufen. Anzahl={}", articles.size());
        return new ArrayList<>(articles.values());
    }

    // Findet einen Artikel per ID oder gibt null zurück
    public Article findArticleById(int id) {


        Article article = articles.get(id);
        if (article != null) {
            logger.debug("Artikel gefunden: ID={}", id);
        } else {
            logger.warn("Artikel nicht gefunden: ID={}", id);
        }
        return article;
    }


    /**
     * Löscht einen Artikel per ID
     * @return true wenn gelöscht, sonst false
     */

    public boolean deleteArticleById(int id) {



        Article removed = articles.remove(id);
        if (removed != null) {
            logger.info("Artikel gelöscht: ID={}, Titel='{}'", id, removed.getTitle());
            return true;
        } else {
            logger.error("Artikel konnte nicht gelöscht werden (nicht gefunden): ID={}", id);
            return false;
        }
    }

  //editArticle
    public Article getArticleById(int articleId)
    {

        Article article = articles.get(articleId);
        if (article != null) {
            logger.debug("Artikel abgerufen: ID={}, Titel='{}'", articleId, article.getTitle());
        } else {
            logger.warn("Artikel nicht gefunden: ID={}", articleId);
        }
        return article;
        }

    }


