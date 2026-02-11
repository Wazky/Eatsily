package es.uvigo.esei.tfg.dto.auth;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    
    private String username;
    private String password;

    // Constructor for JSON deserialization
    public LoginRequest() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
