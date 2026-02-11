package es.uvigo.esei.tfg.entities;

import static java.util.Objects.requireNonNull;

public class Token {
    
    private long id;
    private String token;
    private String token_type;
    private boolean expired;
    private boolean revoked;
    private long idUser;

    // Constructor needed for the JSON conversion
    public Token() {}

    public Token(
        String token,
        String token_type,
        long idUser
    ) {
        this.token = token;
        this.token_type = token_type;
        this.idUser = idUser;
    }

    public Token(
        long id,
        String token,
        String token_type,
        boolean expired,
        boolean revoked,
        long idUser
    ) {
        this.id = id;
        this.token = token;
        this.token_type = token_type;
        this.expired = expired;
        this.revoked = revoked;
        this.idUser = idUser;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = requireNonNull(id);}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = requireNonNull(token); }

    public String getToken_type() { return token_type; }
    public void setToken_type(String token_type) { this.token_type = requireNonNull(token_type); }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public long getIdUser() { return idUser; }
    public void setIdUser(long idUser) { this.idUser = requireNonNull(idUser); }

}
