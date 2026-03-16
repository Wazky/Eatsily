package es.uvigo.esei.tfg.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class UserProfileResponse implements Serializable {
    
    // User Data
    private long id;
    private String username;
    private String email;
    private String role;
    private LocalDate creationDate;
    private LocalDate lastLogin;

    // Person Data
    private String name;
    private String surname;

    public UserProfileResponse() {}

    public UserProfileResponse(
        long id,
        String username,
        String email,
        String role,
        LocalDate creationDate,
        LocalDate lastLogin,
        String name,
        String surname
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.creationDate = creationDate;
        this.lastLogin = lastLogin;
        this.name = name;
        this.surname = surname;
    }

    // Getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

}
