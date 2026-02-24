package es.uvigo.esei.tfg.dto.auth;

import java.io.Serializable;

import es.uvigo.esei.tfg.dto.TokenResponse;
import es.uvigo.esei.tfg.dto.UserResponse;

public class AuthResponse implements Serializable{
    
    private TokenResponse tokenResponse;    
    private UserResponse user;
    private String message;
    
    // Constructor for JSON deserialization
    public AuthResponse() {}

    /**
     * Constructor for creating an authentication response 
     * with token information, user details, and a message.
     * 
     * @param tokenResponse The token response containing access and refresh tokens along with their expiration times
     * @param user The user details to be returned to the client
     * @param message A message indicating the result of the authentication operation (e.g., "Login successful", "Registration successful")
     */
    public AuthResponse(
        TokenResponse tokenResponse,
        UserResponse user,
        String message
    ) {
        this.tokenResponse = tokenResponse;
        this.user = user;
        this.message = message;
    }

    /**
     * Constructor for creating an authentication response with 
     * individual token parameters, user details, and a message.
     * This constructor allows for more flexibility when creating the token response on the fly.
     * 
     * @param accessToken The JWT access token to be returned to the client
     * @param refreshToken The JWT refresh token to be returned to the client
     * @param accessTokenExpiresIn Expiration time for the access token in milliseconds
     * @param refreshTokenExpiresIn Expiration time for the refresh token in milliseconds
     * @param user The user details to be returned to the client
     * @param message A message indicating the result of the authentication operation (e.g., "Login successful", "Registration successful")
     */
    public AuthResponse(
        String accessToken, 
        String refreshToken, 
        Long accessTokenExpiresIn, 
        Long refreshTokenExpiresIn,
        UserResponse user,
        String message
    ) {
        this.tokenResponse = new TokenResponse(
            accessToken, 
            refreshToken, 
            accessTokenExpiresIn, 
            refreshTokenExpiresIn
        );
        this.user = user;
        this.message = message;
    }

    public TokenResponse getTokenResponse() {
        return tokenResponse;
    }

    public void setTokenResponse(TokenResponse tokenResponse) {
        this.tokenResponse = tokenResponse;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static AuthResponse registrationSuccess(
        TokenResponse tokenResponse, 
        UserResponse user
    ) {
        return new AuthResponse(
            tokenResponse, 
            user, 
            "Registration successful"
        );
    }

    public static AuthResponse registrationSuccess(
        String accessToken, 
        String refreshToken, 
        Long accessTokenExpiresIn, 
        Long refreshTokenExpiresIn,
        UserResponse user
    ) {
        return new AuthResponse(
            accessToken, 
            refreshToken, 
            accessTokenExpiresIn, 
            refreshTokenExpiresIn,
            user,
            "Registration successful"
        );
    }

    public static AuthResponse loginSuccess(
        TokenResponse tokenResponse, 
        UserResponse user
    ) {
        return new AuthResponse(
            tokenResponse, 
            user, 
            "Login successful"
        );
    }

    public static AuthResponse loginSuccess(
        String accessToken, 
        String refreshToken, 
        Long accessTokenExpiresIn, 
        Long refreshTokenExpiresIn,
        UserResponse user
    ) {
        return new AuthResponse(
            accessToken, 
            refreshToken, 
            accessTokenExpiresIn, 
            refreshTokenExpiresIn,
            user,
            "Login successful"
        );
    }

}
