package de.davaso.wikinetz.exception;
//AuthenticationException bei Login-Problemen.

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}

