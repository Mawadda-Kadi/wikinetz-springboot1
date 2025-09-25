package de.davaso.wikinetz.manager;


import de.davaso.wikinetz.api.ArticleService;
import de.davaso.wikinetz.api.IdGenerator;
import de.davaso.wikinetz.model.Article;
import de.davaso.wikinetz.model.Category;
import de.davaso.wikinetz.model.User;
import de.davaso.wikinetz.exception.ArticleNotFoundException;
import de.davaso.wikinetz.exception.InvalidArticleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class ArticleManager implements ArticleService{

    // Speichert alle Artikel im Speicher, während das Programm läuft
    private final Map<Integer, Article> articles = new HashMap<>();
    //Zuerst Logger importieren und initialisieren
    private static final Logger logger = LoggerFactory.getLogger(ArticleManager.class);

    // Gibt jedem Artikel eine eindeutige, fortlaufende ID
    private final IdGenerator idGen;  // <- inject

    public ArticleManager(IdGenerator idGen) {
        this.idGen = idGen;
    }


    // Erstellt einen neuen Artikel und fügt ihn der Liste hinzu.
    @Override
    public Article addArticle(String title, String content, Category category, User author) {

        if (title == null || title.isEmpty()) {
            logger.error("Ungültiger Titel beim Erstellen eines Artikels");
            throw new InvalidArticleException("Titel darf nicht leer sein");
        }
        if (author == null) {
            logger.error("Ungültiger Autor beim Erstellen eines Artikels");
            throw new InvalidArticleException("Autor darf nicht null sein");
        }

        int id = idGen.nextId();
        Article a = new Article(
                id,
                title,
                content,
                category,
                author.getUserId(),
                author.getUsername()
        );

        articles.put(a.getArticleId(), a);
        logger.info("Artikel erstellt: ID={}, Titel='{}', Autor={}", a.getArticleId(), title, author.getUsername());
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
        if (article == null) {
            logger.warn("Artikel nicht gefunden: ID={}", id);
            return null;
        }
        logger.debug("Artikel gefunden: ID={}", id);
        return article;
    }


    /**
     * Löscht einen Artikel per ID
     * @return true wenn gelöscht, sonst false
     */

    public boolean deleteArticleById(int id) {

        Article removed = articles.remove(id);
        if (removed != null) {
            logger.info("Artikel gelöscht: ID={}, Titel='{}'", id);
            throw new ArticleNotFoundException(id);

        }
        logger.info("Artikel gelöscht: ID={}, Titel='{}'", id, removed.getTitle());
        return true;

    }

  //editArticle
    public Article getArticleById(int articleId) {

        Article article = articles.get(articleId);
        if (article == null) {
            logger.warn("Artikel nicht gefunden: ID={}", articleId);
            throw new ArticleNotFoundException(articleId);
        }
        logger.debug("Artikel abgerufen: ID={}, Titel='{}'", articleId, article.getTitle());
        return article;
    }

}


