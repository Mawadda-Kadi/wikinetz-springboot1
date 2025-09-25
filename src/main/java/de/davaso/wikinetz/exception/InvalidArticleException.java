package de.davaso.wikinetz.exception;
//Ungültige Eingaben lösen InvalidArticleException aus.
public class InvalidArticleException extends RuntimeException {
    public InvalidArticleException(String message) {
        super(message);
    }
}
