package es.uvigo.esei.tfg.dto;

import java.io.Serializable;

public class TokenResponse  implements Serializable{
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    private Long accessTokenExpiresIn; // Expiration time in milliseconds
    private Long refreshTokenExpiresIn; // Expiration time in milliseconds

    // Constructor for JSON deserialization
    public TokenResponse() {}

    /**
     * Constructor for creating a token response with access and 
     * refresh tokens along with their expiration times.
     * 
     * @param accessToken The JWT access token to be returned to the client
     * @param refreshToken The JWT refresh token to be returned to the client
     * @param accessTokenExpiresIn Expiration time for the access token in milliseconds
     * @param refreshTokenExpiresIn Expiration time for the refresh token in milliseconds
     */
    public TokenResponse(
        String accessToken, 
        String refreshToken, 
        Long accessTokenExpiresIn, 
        Long refreshTokenExpiresIn
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getAccessTokenExpiresIn() {
        return accessTokenExpiresIn;
    }

    public void setAccessTokenExpiresIn(Long accessTokenExpiresIn) {
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }

    public Long getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }

    public void setRefreshTokenExpiresIn(Long refreshTokenExpiresIn) {
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

}
