package es.uvigo.esei.tfg.services;

import es.uvigo.esei.tfg.dao.TokenDAO;
import es.uvigo.esei.tfg.dto.TokenResponse;
import es.uvigo.esei.tfg.entities.Token;
import es.uvigo.esei.tfg.entities.User;
import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.util.JwtUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TokenManagmentService {
    private final static Logger LOG = Logger.getLogger(TokenManagmentService.class.getName());
    
    private final static long JWT_EXPIRATION_TIME =  15 * 60 * 1000; // 15 minutes
    private final static long JWT_REFRESH_TOKEN_EXPIRATION_TIME = 2 * 24 * 60 * 60 * 1000; // 2 days

    private final JwtUtil jwtUtil;
    private final TokenDAO tokenDAO;

    public TokenManagmentService() {
        this.jwtUtil = new JwtUtil();
        this.tokenDAO = new TokenDAO();
    }

    /**
     * Generates new access and refresh tokens for the given user, saves the new refresh token in the database,
     * and returns a {@link TokenResponse} containing the generated tokens and their expiration times.
     * 
     * @param user the user for which to generate the tokens.
     * @return a {@link TokenResponse} containing the generated access and refresh tokens.
     */
    public TokenResponse generateNewTokens(User user) 
    throws DAOException {
        TokenResponse tokenResponse = generateTokenResponse(user.getUsername());
        saveUserToken(user.getId(), tokenResponse.getRefreshToken(), null);
        return tokenResponse;
    }

    /**
     * Refreshes the user's tokens by generating new access and refresh tokens, 
     * revoking the old refresh token in the database, and saving the new refresh token.
     * 
     * @param user the user for which to refresh the tokens.
     * @return a {@link TokenResponse} containing the new access and refresh tokens.
     */
    public TokenResponse refreshTokens(User user)
    throws DAOException {
        // Generate new tokens for the user
        final TokenResponse newTokenResponse = generateTokenResponse(user.getUsername());
        // Update the database to revoke the old refresh token and save the new one
        updateUserTokens(user.getId(), newTokenResponse.getRefreshToken());
        return newTokenResponse;
    }

    /**
     * Generates a {@link TokenResponse} containing an access token 
     * and a refresh token for the given username.
     * 
     * @param username the username for which to generate the tokens.
     * @return a {@link TokenResponse} containing the generated access 
     * and refresh tokens along with their expiration times.
     */
    private TokenResponse generateTokenResponse(String username) {
        String accessToken = jwtUtil.generateToken(username, JWT_EXPIRATION_TIME);
        String refreshToken = jwtUtil.generateToken(username, JWT_REFRESH_TOKEN_EXPIRATION_TIME);

        return new TokenResponse(
            accessToken, 
            refreshToken, 
            JWT_EXPIRATION_TIME, 
            JWT_REFRESH_TOKEN_EXPIRATION_TIME
        );        
    }    

    /**
     * Saves a new refresh token for the user in the database.
     * 
     * @param userId the ID of the user for which to save the refresh token.
     * @param refreshToken the refresh token to be saved in the database. 
     * @param externalConnection an optional connection to use for the database operation. 
     * @throws DAOException if an error occurs while accessing the data source or if the transaction fails.
     */
    private void saveUserToken(long userId, String refreshToken, Connection externalConnection) 
    throws DAOException {
        Token token = new Token();
        token.setToken(refreshToken);
        token.setTokenType("refresh");
        token.setIdUser(userId);

        this.tokenDAO.create(token, externalConnection);
    }

    /**
     * Updates the user's tokens in the database by revoking all existing tokens and saving the new refresh token.
     * 
     * @param userId the ID of the user for which to update the tokens.
     * @param newRefreshToken the new refresh token to be saved in the database.
     * @throws DAOException if an error occurs while accessing the data source or if the transaction fails.
     */
    private void updateUserTokens(long userId, String newRefreshToken)
    throws DAOException {
        Connection conn = null;
        try {
            conn = tokenDAO.getConnection(null);
            conn.setAutoCommit(false);

            // Revoke all existing tokens for the user
            tokenDAO.revokeAllUserTokens(userId, conn);
            
            // Save the new refresh token in the database
            saveUserToken(userId, newRefreshToken, conn);

            // Commit the transaction if everything is successful
            conn.commit();
            LOG.info("User tokens updated successfully for user ID: " + userId);
        //Rollback the transaction in case of any error
        } catch (SQLException | DAOException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LOG.warning("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    LOG.log(Level.SEVERE, "Error rolling back transaction", rollbackEx);
                    throw new DAOException("Error rolling back transaction: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            
            LOG.log(Level.SEVERE, "Error updating user tokens", e);
            throw new DAOException(e);

        // Finally block to ensure the connection is closed
        } finally {
            if (conn != null) {
                try {                    
                    conn.close();
                } catch (SQLException closeEx) {
                    LOG.log(Level.SEVERE, "Error closing connection", closeEx);
                }
            }
        }
    }

    public void revokeUserTokens(long userId)
    throws DAOException {
        tokenDAO.revokeAllUserTokens(userId);
    }

    public Token getByToken(String token) throws DAOException {
        Token storedToken = tokenDAO.getByToken(token);

        if (storedToken == null || storedToken.isRevoked()) {
            LOG.warning("Refresh token is invalid or revoked for token: " + token);
            throw new IllegalArgumentException("Invalid or revoked refresh token");
        }
        return storedToken;
    }

}
