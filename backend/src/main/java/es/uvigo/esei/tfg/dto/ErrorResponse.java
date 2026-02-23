package es.uvigo.esei.tfg.dto;

import java.io.Serializable;
import java.util.Map;

public class ErrorResponse implements Serializable {
    
    private String code;                    // Unique error code for client-side handling
    private String message;                 // User-friendly error message (Debbuging)
    private Map<String, Object> details;    // Optional additional details (e.g., validation errors)
    private long timestamp;                 // Timestamp of when the error occurred

    // Constructor for JSON deserialization
    public ErrorResponse() {}

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = System.currentTimeMillis();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
