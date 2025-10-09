package de.davaso.wikinetz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//Wenn ein Artikel nicht gefunden wird, wird ArticleNotFoundException ausgelöst.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(int articleId) {

        super("Artikel mit ID " + articleId + " nicht gefunden.");
    }
}
