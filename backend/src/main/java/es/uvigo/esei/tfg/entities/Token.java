package es.uvigo.esei.tfg.entities;

import static java.util.Objects.requireNonNull;

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
    public void setId(long id) { this.id = requireNonNull(id);}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = requireNonNull(token); }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = requireNonNull(tokenType); }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public long getIdUser() { return idUser; }
    public void setIdUser(long idUser) { this.idUser = requireNonNull(idUser); }

}
