package es.uvigo.esei.tfg.entities.user;

import static java.util.Objects.requireNonNull;

/**
 * This class represents a token that is used for authentication and authorization purposes.
 * It contains information about the token itself, its type, whether it is expired or revoked, 
 * and the ID of the user it belongs to.
 */
public class Token {
    
    private long id;
    private String token;
    private String tokenType;
    private boolean expired;
    private boolean revoked;
    private long idUser;

    // Constructor needed for the JSON conversion
    public Token() {}

    public Token(
        String token,
        String tokenType,
        long idUser
    ) {
        this.token = token;
        this.tokenType = tokenType;
        this.idUser = idUser;
    }

    public Token(
        long id,
        String token,
        String tokenType,
        boolean expired,
        boolean revoked,
        long idUser
    ) {
        this.id = id;
        this.token = token;
        this.tokenType = tokenType;
        this.expired = expired;
        this.revoked = revoked;
        this.idUser = idUser;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = requireNonNull(token); }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = requireNonNull(tokenType); }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public long getIdUser() { return idUser; }
    public void setIdUser(long idUser) { this.idUser = idUser; }

}
