package de.davaso.wikinetz.exception;
//Wenn ein Artikel nicht gefunden wird, wird ArticleNotFoundException ausgelöst.

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(int articleId) {

        super("Artikel mit ID " + articleId + " nicht gefunden.");
    }
}
