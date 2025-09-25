package de.davaso.wikinetz.exception;
//AuthorizationException bei fehlenden Rechten.

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}
