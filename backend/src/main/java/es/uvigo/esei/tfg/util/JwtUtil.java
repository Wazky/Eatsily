package es.uvigo.esei.tfg.util;

import es.uvigo.esei.tfg.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

public class JwtUtil {
    
    private static final Logger LOG = Logger.getLogger(JwtUtil.class.getName());

    // Secret key for signing tokens (in a sreal application, use a secure method to store this)
    // Change this to a secure key in production 
    private static final String SECRET_KEY_STRING = "MySecretKeyForJWTTokenGenerationMustBeLongEnough256Bits!!"; 
    private final SecretKey SECRET_KEY;

    // Token expiration (in milliseconds)
    private static final long EXPIRATION_TIME = 15 * 60 * 1000; // 15 minutes

    // Token prefix for the Authorization header
    private static final String TOKEN_PREFIX = "Bearer ";

    // Header string for the Authorization header
    private static final String HEADER_STRING = "Authorization";

    public JwtUtil() {
        // Initialize the secret key
        this.SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());
        //this.SECRET_KEY = Jwts.SIG.HS256.key().build();
    }

    /**
     * Generates a JWT token for the given username.
     * 
     * @param username the username for which the token is generated.
     * @return the generated JWT token.
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        String token = Jwts.builder()
                .subject(username)
                .expiration(expirationDate)
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();

        LOG.log(Level.INFO, "Generated JWT token for user: " + username);
        return token;
    }

    /**
     * Generates a JWT token for the given username with a custom expiration time.
     * 
     * @param username the username for which the token is generated.
     * @param expirationTimeMillis the custom expiration time in milliseconds.
     * @return the generated JWT token.
     */
    public String generateToken(String username, long expirationTimeMillis) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTimeMillis);

        String token = Jwts.builder()
                .subject(username)
                .expiration(expirationDate)
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();

        LOG.log(Level.INFO, "Generated JWT token for user: " + username);
        return token;
    }

    /**
     * Validates the provided JWT token.
     * 
     * @param token the JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);

            return true;

        } catch (JwtException e) {
            LOG.log(Level.WARNING, "Invalid JWT token", e);
        }
        return false;
    } 

    public boolean isTokenValid(String token, User user) {
        final String tokenUsername = getUsernameFromToken(token);

        return (tokenUsername.equals(user.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Extracts the username from the provided JWT token.
     * 
     * @param token the JWT token from which to extract the username.
     * @return the username extracted from the token, or null if the token is invalid.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extracts the expiration date from the provided JWT token.
     * 
     * @param token the JWT token from which to extract the expiration date.
     * @return the expiration date extracted from the token, or null if the token is invalid.
     */
    public Date getExpirationDateFromToken(String token) {
        return parseClaims(token).getExpiration();
    }

    /**
     * Checks if the provided JWT token is expired.
     * 
     * @param token the JWT token to check for expiration.
     * @return true if the token is expired, false otherwise. If the token is invalid, it will be considered expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        
        } catch (JwtException e) {
            LOG.log(Level.WARNING, "Invalid JWT token while checking expiration", e);
            return true; // Consider invalid tokens as expired
        }
    }

    /**
     * Extracts the JWT token from the Authorization header.
     * 
     * @param authHeader the Authorization header containing the JWT token.
     * @return the extracted JWT token, or null if the header is invalid.
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * Parses the claims from the provided JWT token.
     * 
     * @param token the JWT token from which to parse the claims.
     * @return the claims extracted from the token.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
