package de.davaso.wikinetz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//Ungültige Eingaben lösen InvalidArticleException aus.
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidArticleException extends RuntimeException {
    public InvalidArticleException(String message) {
        super(message);
    }
}
