package es.uvigo.esei.tfg.exceptions;

import es.uvigo.esei.tfg.dto.ErrorResponse;

public class ValidationException extends Exception {
    
    private static final long serialVersionUID = 1L;

    private ErrorResponse error;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(ErrorResponse error) {
        super(error.getMessage());
        this.error = error;
    }

    public ErrorResponse getError() {
        return error;
    }

}
