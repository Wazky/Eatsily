package es.uvigo.esei.tfg.exceptions;

public class AccountBlockedException extends Exception {
    
    public AccountBlockedException(String message) {
        super(message);
    }

    public AccountBlockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
