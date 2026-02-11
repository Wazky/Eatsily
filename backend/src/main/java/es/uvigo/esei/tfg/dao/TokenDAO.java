package es.uvigo.esei.tfg.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.tfg.entities.Token;
import es.uvigo.esei.tfg.entities.User;
import es.uvigo.esei.tfg.exceptions.DAOException;

public class TokenDAO extends DAO {
    
    private final static Logger LOG = Logger.getLogger(TokenDAO.class.getName());

	//============     CREATE     ============
	
    public Token create(Token token) 
    throws DAOException {
        if (token == null) {
            throw new IllegalArgumentException("token can't be null");
        }

        try (final Connection conn = this.getConnection()) {

            final String query = "INSERT INTO tokens" +
                " (token, token_type, user_id) " +
                " VALUES (?, ?, ?)";

            try (final PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, token.getToken());
                statement.setString(2, token.getToken_type());
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
        }
    }

    public Token create(Connection conn, Token token) 
    throws SQLException, DAOException {
        if (token == null) {
            throw new IllegalArgumentException("token can't be null");
        }

        final String query = "INSERT INTO tokens" +
            " (token, token_type, user_id) " +
            " VALUES (?, ?, ?)";

        try (final PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, token.getToken());
            statement.setString(2, token.getToken_type());
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
    }

    
	//============     READ     ============

    public Token get(long id)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection()) {
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
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM tokens WHERE id_token = ?";

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

    public void revokeAllUserTokens(User user) {
        try (final Connection conn = this.getConnection()) {
            final String query = "UPDATE tokens SET revoked = true WHERE user_id = ? AND revoked = false";
                
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, user.getId());
                statement.executeUpdate();
            }             
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error revoking user tokens", e);
        }
    }

	//============   AUXILIARY   ============

    private Token rowToEntity(ResultSet result) throws SQLException {
        return new Token(
            result.getInt("id"),
            result.getString("token"),
            result.getString("token_type"),
            result.getBoolean("expired"),
            result.getBoolean("revoked"),
            result.getLong("id_user")
        );
    }

}
