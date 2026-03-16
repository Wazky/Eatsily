package es.uvigo.esei.tfg.dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.tfg.dao.DAO;
import es.uvigo.esei.tfg.entities.user.Token;
import es.uvigo.esei.tfg.exceptions.DAOException;

public class TokenDAO extends DAO {
    
    private final static Logger LOG = Logger.getLogger(TokenDAO.class.getName());

	//============     CREATE     ============
	
    public Token create(Token token) 
    throws DAOException {
        return create(token, null);
    }

    public Token create(Token token, Connection externalConnection) 
    throws DAOException {
        ensureTokenDataIntegrity(token);

        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "INSERT INTO tokens" +
                " (token, token_type, user_id) " +
                " VALUES (?, ?, ?)";

            try (final PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, token.getToken());
                statement.setString(2, token.getTokenType());
                statement.setLong(3, token.getIdUser());

                if (statement.executeUpdate() == 1) {
                    try (final ResultSet resultKeys = statement.getGeneratedKeys()) {
                        if (resultKeys.next()) {
                            token.setId(resultKeys.getInt(1));
                            return token;
                        } else {
                            LOG.log(Level.SEVERE, "Error retrieving inserted id");
                            throw new DAOException("Error retrieving inserted id");
                        }
                    }
                } else {
                    LOG.log(Level.SEVERE, "Error inserting value");
                    throw new DAOException("Error inserting value");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error inserting token", e);
            throw new DAOException("Error inserting token: " + e.getMessage());
            
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    
	//============     READ     ============

    public Token get(long id)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM tokens WHERE id_token = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, id);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);                        
                    } else {
                        throw new IllegalArgumentException("No token found with the provided id");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error retrieving token", e);
            throw new DAOException(e);
        }
    }

    public Token getByToken(String token)
    throws DAOException, IllegalArgumentException {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token can't be null or empty");
        }
        
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM tokens WHERE token = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, token);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);                        
                    } else {
                        throw new IllegalArgumentException("No token found with the provided token string");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error retrieving token by token string", e);
            throw new DAOException(e);
        }
    }

	// Add specific read methods if needed

	//============     UPDATE     ============
    
	// Add specific update methods if needed

	//============     DELETE     ============
    
    // Add specific delete methods if needed

	// Add specific delete methods if needed

	//============ OTHER METHODS  ============

    public void revokeAllUserTokens(long userId) 
    throws DAOException {
        revokeAllUserTokens(userId, null);
    }

    public void revokeAllUserTokens(long userId, Connection externalConnection) 
    throws DAOException {

        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "UPDATE tokens SET revoked = true WHERE user_id = ? AND revoked = false";
                
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, userId);
                statement.executeUpdate();
            }             
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error revoking user tokens", e);
            throw new DAOException("Error revoking user tokens: " + e.getMessage(), e);

        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

	//============   AUXILIARY   ============

    /**
     * Converts the current row of the provided {@link ResultSet} into a {@link Token} entity.
     * 
     * @param result the {@link ResultSet} positioned at the row to convert.
     * @return a {@link Token} entity with the data from the current row of the provided {@link ResultSet}.
     * @throws SQLException if an error happens while accessing the data from the provided {@link ResultSet}.
     */
    private Token rowToEntity(ResultSet result) throws SQLException {
        return new Token(
            result.getInt("id_token"),
            result.getString("token"),
            result.getString("token_type"),
            result.getBoolean("expired"),
            result.getBoolean("revoked"),
            result.getLong("user_id")
        );
    }

    /**
     * Ensures that the provided token has all the necessary data to be persisted in the database.
     * 
     * @param token the token to check.
     * @throws IllegalArgumentException if the token is {@code null} or any of its required fields is {@code null} or empty.
     */
    private void ensureTokenDataIntegrity(Token token) throws IllegalArgumentException {
        if (token == null) {
            throw new IllegalArgumentException("token can't be null");
        }

        if (token.getToken() == null || token.getToken().trim().isEmpty()) {
            throw new IllegalArgumentException("token can't be null or empty");
        }

        if (token.getTokenType() == null || token.getTokenType().trim().isEmpty()) {
            throw new IllegalArgumentException("tokenType can't be null or empty");
        }

        if (token.getIdUser() <= 0) {
            throw new IllegalArgumentException("idUser must be a positive number");
        }                
    }


}
