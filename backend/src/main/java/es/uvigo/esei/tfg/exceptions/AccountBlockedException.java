package es.uvigo.esei.tfg.exceptions;

import es.uvigo.esei.tfg.dto.ErrorResponse;

public class AccountBlockedException extends Exception {
    
    private static final long serialVersionUID = 1L;

    private ErrorResponse error;

    public AccountBlockedException(String message) {
        super(message);
    }

    public AccountBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountBlockedException(ErrorResponse error) {
        super(error.getMessage());
        this.error = error;
    }

    public ErrorResponse getError() {
        return error;
    }
}
