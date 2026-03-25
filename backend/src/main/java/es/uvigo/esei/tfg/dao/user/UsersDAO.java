package es.uvigo.esei.tfg.dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;



import es.uvigo.esei.tfg.dao.DAO;
import es.uvigo.esei.tfg.entities.user.Person;
import es.uvigo.esei.tfg.entities.user.User;
import es.uvigo.esei.tfg.exceptions.DAOException;

/**
 * DAO class for managing the users of the system.
 * 
 * @author DRM
 */
public class UsersDAO extends DAO {
	private final static Logger LOG = Logger.getLogger(UsersDAO.class.getName());
	
	//============     CREATE     ============
	

	/**
	 * Persists a new user in the system. An identifier will be assigned
	 * automatically to the new user.
	 * 
	 * @param user the User entity containing the information of the new user to be persisted. 
	 *             The id field will be ignored, as it will be generated automatically.
	 * @return a {@link User} entity representing the persisted user, including the generated identifier.
	 * @throws DAOException if an error happens while persisting the new user.
	 * @throws IllegalArgumentException if any of the required fields of the user is null or invalid.
	 */
	public User create(User user)
	throws DAOException {
		return create(user, null);
	}

	/**
	 * Persists a new user in the system. An identifier will be assigned
	 * automatically to the new user.
	 * 
	 * @param user the User entity containing the information of the new user to be persisted. 
	 *             The id field will be ignored, as it will be generated automatically.
	 * @return a {@link User} entity representing the persisted user, including the generated identifier.
	 * @throws DAOException if an error happens while persisting the new user.
	 * @throws IllegalArgumentException if any of the required fields of the user is null or invalid.
	 */
	public User create (User user, Connection externalConnection) 
	throws DAOException {
		ensureUserDataIntegrity(user);

		boolean isExternalConnection = isExternalConnection(externalConnection);
		Connection conn = null;

		try {
			conn = this.getConnection(externalConnection);

			final String query = "INSERT INTO users" + 
				" (username, password_hash, email, role, person_id)" +
				" VALUES (?, ?, ?, ?, ?)";
			
			try (final PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
				statement.setString(1,  user.getUsername());
				statement.setString(2,  user.getPasswordHash());
				statement.setString(3,  user.getEmail());
				statement.setString(4,  (user.getRole() != null) ? user.getRole() : "USER");
				statement.setObject(5, user.getPerson().getId());
				
				if (statement.executeUpdate() == 1) {
					try (final ResultSet resultKeys = statement.getGeneratedKeys()) {
						if (resultKeys.next()) {							
							return new User(
								resultKeys.getLong(1),
								user.getUsername(),
								user.getPasswordHash(),
								user.getEmail(),
								(user.getRole() != null) ? user.getRole() : "USER",
								user.getPerson()
							);
						} else {
							LOG.log(Level.SEVERE, "Error retrieving inserted id");
							throw new SQLException("Error retrieving inserted id");
						}
					}
				} else {
					LOG.log(Level.SEVERE, "Error inserting value");
					throw new SQLException("Error inserting value");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error creating a user", e);
			throw new DAOException(e);

		} finally {
			closeConnection(conn, isExternalConnection);
		}
	}

	//============     READ     ============

	/**
	 * Returns a user stored persisted in the system.
	 * 
	 * @param id the identifier of the user to retrieve.
	 * @return a {@link User} entity representing the retrieved user.
	 * @throws DAOException if an error happens while retrieving the user.
	 * @throws IllegalArgumentException if the provided identifier does not correspond to any existing user.
	 */
	public User get(long id) 
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection(null)) {
			final String query = "SELECT * FROM users WHERE id_user=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setLong(1, id);
				
				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return rowToEntity(result);
					} else {
						throw new IllegalArgumentException("Invalid id");
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error getting a user by id", e);
			throw new DAOException(e);
		}
	} 

	/**
	 * Returns a user stored persisted in the system by its username.
	 * 
	 * @param username the username of the user to retrieve.
	 * @return a {@link User} entity representing the retrieved user.
	 * @throws DAOException if an error happens while retrieving the user.
	 * @throws IllegalArgumentException if the provided username does not correspond to any existing user.
	 */
	public User getByUsername(String username) 
	throws DAOException, IllegalArgumentException {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or blank");
		}

		try (final Connection conn = this.getConnection(null)) {
			final String query = "SELECT * FROM users WHERE username=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, username);
				
				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return rowToEntity(result);
					} else {
						throw new IllegalArgumentException("Invalid username");
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error getting a user by username", e);
			throw new DAOException(e);
		}
	}

	// Add specific read methods if needed

	//============     UPDATE     ============

	// Handle before calling it having all user fields with previous or new values
	public void update (User user) 
	throws DAOException, IllegalArgumentException {		
		ensureUserDataIntegrity(user);

		try (final Connection conn = this.getConnection(null)) {

			final String query = "UPDATE users SET" +
				" username=?, password_hash=?, email=?, role=?, active=?, blocked=?," +
				" failed_login_attempts=?, creation_date=?, last_login=?, person_id=?" +
				" WHERE id_user=?";
		
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1,  user.getUsername());
				statement.setString(2,  user.getPasswordHash());
				statement.setString(3,  user.getEmail());
				statement.setString(4,  user.getRole());
				statement.setBoolean(5, user.isActive());
				statement.setBoolean(6, user.isBlocked());
				statement.setInt(7,     user.getFailedLoginAttempts());
				statement.setObject(8,  user.getCreationDate());
				statement.setObject(9,  user.getLastLogin());
				statement.setObject(10, user.getPerson().getId());
				statement.setLong(11,   user.getId());
				
				if (statement.executeUpdate() == 0) {
					throw new IllegalArgumentException("Invalid id");
				}
			}

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error updating a user", e);
			throw new DAOException(e);
		}
	}

	/**
	 * Updates the last login timestamp for the user with the given identifier. 
	 * This method sets the 'last_login' field to the current timestamp.
	 * 
	 * @param userId the identifier of the user whose last login timestamp is to be updated.
	 * @throws DAOException if an error happens while updating the last login timestamp.
	 * @throws IllegalArgumentException if the provided userId does not correspond to any existing user.
	 */
	public void updateLastLogin(long userId)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection(null)) {

			final String query = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id_user = ?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {				
				statement.setLong(1, userId);
				
				if (statement.executeUpdate() != 1) {
					throw new IllegalArgumentException("Could not update last login, no rows affected");
				}			
			}
		
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error updating last login", e);
			throw new DAOException(e);
		}
	}

	// Add specific update methods if needed

	//============     DELETE     ============

	public void delete(long id)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection(null)) {
			final String query = "DELETE FROM users WHERE id_user=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setLong(1, id);
				
				if (statement.executeUpdate() == 0) {
					throw new IllegalArgumentException("Invalid id");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error deleting a user", e);
			throw new DAOException(e);
		}
	}
	
	public void deleteByUsername(String username)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection(null)) {
			final String query = "DELETE FROM users WHERE username=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, username);
				
				if (statement.executeUpdate() == 0) {
					throw new IllegalArgumentException("Invalid username");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error deleting a user", e);
			throw new DAOException(e);
		}
	}

	// Add specific delete methods if needed

	//============ OTHER METHODS  ============

	/**
	 * Records a failed login attempt for the user with the given identifier. 
	 * This method increments the failed login attempts counter for the user. 
	 * It should be called when a user fails to authenticate, in order to keep track of 
	 * failed login attempts and potentially lock the account after reaching a certain threshold.
	 * 
	 * @param userId the identifier of the user for which to record the failed login attempt.
	 * @throws DAOException if an error happens while recording the failed login attempt.
	 */
	public void recordFailedLoginAttempt(long userId)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection(null)) {

			final String query = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1 WHERE id_user = ?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {				
				statement.setLong(1, userId);
				
				if (statement.executeUpdate() != 1) {
					throw new IllegalArgumentException("Could not record failed login attempt, no rows affected");
				}			
			}

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error recording failed login attempt", e);
			throw new DAOException(e);
		}
	}

	/**
	 * Resets the failed login attempts counter for the user with the given identifier. 
	 * This method sets the failed login attempts counter to zero.
	 * It should be called when a user successfully authenticates,
	 * in order to clear any previous failed login attempts.
	 * 
	 * @param userId the identifier of the user for which to reset the failed login attempts counter.
	 * @throws DAOException if an error happens while resetting the failed login attempts counter.
	 * @throws IllegalArgumentException if the provided userId does not correspond to any existing user.
	 */
	public void resetFailedLoginAttempts(long userId)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection(null)) {

			final String query = "UPDATE users SET failed_login_attempts = 0 WHERE id_user = ?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {				
				statement.setLong(1, userId);
				
				if (statement.executeUpdate() != 1) {
					throw new IllegalArgumentException("Could not reset failed login attempts, no rows affected");
				}			
			}
		
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error resetting failed login attempts", e);
			throw new DAOException(e);
		}
	}

	/**
	 * Locks the user account with the given identifier. 
	 * This method sets the 'blocked' status of the user to true, effectively locking the account. 
	 * It should be called when a user exceeds the maximum allowed failed login attempts,
	 * in order to prevent further login attempts until the account is unlocked by an administrator or through a recovery process.
	 * 
	 * @param userId the identifier of the user whose account is to be locked.
	 * @throws DAOException if an error happens while locking the user account.
	 * @throws IllegalArgumentException if the provided userId does not correspond to any existing user.
	 */
	public void lockUserAccount(long userId)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection(null)) {

			final String query = "UPDATE users SET blocked = true WHERE id_user = ?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {				
				statement.setLong(1, userId);
				
				if (statement.executeUpdate() == 0) {
					throw new IllegalArgumentException("Locking user account failed, no rows affected.");
				}
			}

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error locking user account", e);
			throw new DAOException(e);
		}
	}

	/**
	 * Unlocks the user account with the given identifier. 
	 * This method sets the 'blocked' status of the user to false and
	 *  resets the failed login attempts counter to zero, effectively unlocking the account. 
	 * It should be called when an administrator or a recovery process determines 
	 * that the user should regain access to their account.
	 *  
	 * @param userId the identifier of the user whose account is to be unlocked.
	 * @throws DAOException if an error happens while unlocking the user account.
	 * @throws IllegalArgumentException if the provided userId does not correspond to any existing user.
	 */
	public void unlockUserAccount(long userId)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection(null)) {

			final String query = "UPDATE users SET blocked = false, failed_login_attempts = 0 WHERE id_user = ?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {				
				statement.setLong(1, userId);
				
				if (statement.executeUpdate() == 0) {
					throw new IllegalArgumentException("Unlocking user account failed, no rows affected.");
				}
			}

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error unlocking user account", e);
			throw new DAOException(e);
		}
	}

	//============   AUXILIARY   ============

	/**
	 * Checks if a user with the given username already exists in the system.
	 * 
	 * @param username the username to check for existence.
	 * @return true if a user with the given username exists, false otherwise.
	 */
	public boolean existsByUsername(String username) 
	throws DAOException {
		try (Connection conn = this.getConnection(null)) {

			final String query = "SELECT 1 FROM users WHERE username = ?";

			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, username);

				try (ResultSet result = statement.executeQuery()) {
					return result.next();
				}
			}

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error checking if username exists", e);
			throw new DAOException(e);
		}
	}

	/**
	 * Checks if a user with the given email already exists in the system.
	 * 
	 * @param email the email to check for existence.
	 * @return true if a user with the given email exists, false otherwise.
	 */
	public boolean existsByEmail(String email)
	throws DAOException {
		try (Connection conn = this.getConnection(null)) {

			final String query = "SELECT 1 FROM users WHERE email = ?";

			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, email);

				try (ResultSet result = statement.executeQuery()) {
					return result.next();
				}
			}

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error checking if email exists", e);
			throw new DAOException(e);
		}
	}

	/**
	 * Converts a ResultSet row into a User entity.
	 * The ResultSet must contain at least the following columns:
	 * id_user, username, password_hash, email, role and person_id.
	 * 
	 * @param result the ResultSet positioned at the row to be converted.
	 * @return the corresponding User entity.
	 * @throws SQLException if an error happens while accessing the ResultSet.
	 */
	private User rowToEntity(ResultSet result) throws SQLException {

		Set<String> columnNames = this.getColumnNames(result);

		Person person = extractPerson(result, columnNames);

		User user = new User(
			result.getLong("id_user"),
			result.getString("username"),
			result.getString("password_hash"),
			result.getString("email"),
			result.getString("role"),
			person				
		);

		if (columnNames.contains("active")) {
			user.setActive(result.getBoolean("active"));
		}

		if (columnNames.contains("blocked")) {
			user.setBlocked(result.getBoolean("blocked"));
		}

		if (columnNames.contains("failed_login_attempts")) {
			user.setFailedLoginAttempts(result.getInt("failed_login_attempts"));
		}

		if (columnNames.contains("creation_date")) {
			Timestamp createdAt = result.getTimestamp("creation_date");
			if (createdAt != null) {
				user.setCreationDate(createdAt.toLocalDateTime());
			}
		}

		if (columnNames.contains("last_login")) {
			Timestamp lastLogin = result.getTimestamp("last_login");
			if (lastLogin != null) {
				user.setLastLogin(lastLogin.toLocalDateTime());
			}
		}

		return user;
	}


	private Person extractPerson(ResultSet result, Set<String> columnNames)
	throws SQLException {
		String prefix = "person_";
		String id = result.getString("person_id");
		Person person = new Person(Long.parseLong(id));

		if (columnNames.contains(prefix + "name")) {
			person.setName(result.getString(prefix + "name"));
		}

		if (columnNames.contains(prefix + "surname")) {
			person.setSurname(result.getString(prefix + "surname"));
		}

		return person;
	}

	/**
	 * Ensures that the provided User entity has all the necessary data to be persisted in the system.
	 * This method checks that the user is not null neither all his required fields 
	 * (username, password hash, email and person) are null or blank.
	 * 
	 * @param user the User entity to be checked.
	 * @throws IllegalArgumentException if the user is null or if any of the required fields is null or blank.
	 */
	private void ensureUserDataIntegrity(User user)
	throws IllegalArgumentException {
		if (user == null) {
			throw new IllegalArgumentException("User can't be null");
		}

		if (user.getUsername() == null || isBlank(user.getUsername())) {
			throw new IllegalArgumentException("Username can't be null or blank");
		}

		if (user.getPasswordHash() == null || isBlank(user.getPasswordHash())) {
			throw new IllegalArgumentException("Password hash can't be null or blank");
		}

		if (user.getEmail() == null || isBlank(user.getEmail())) {
			throw new IllegalArgumentException("Email can't be null or blank");
		}

		if (user.getPerson() == null || user.getPerson().getId() <= 0) {
			throw new IllegalArgumentException("Person can't be null and must have a valid id");
		}
	}

	private boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
}


}
