package es.uvigo.esei.tfg.exceptions;

import es.uvigo.esei.tfg.dto.ErrorResponse;

public class AuthenticationException extends Exception {
    
    private static final long serialVersionUID = 1L;

    private ErrorResponse error;

    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(ErrorResponse error) {
        super(error.getMessage());
        this.error = error;
    }

    public ErrorResponse getError() {
        return error != null ? error : null;
    }

}
