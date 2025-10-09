package de.davaso.wikinetz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//AuthenticationException bei Login-Problemen.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}

