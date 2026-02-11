package es.uvigo.esei.tfg.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.tfg.entities.Person;
import es.uvigo.esei.tfg.exceptions.DAOException;

/**
 * DAO class for the {@link Person} entities.
 * 
 * @author DRM
 *
 */
public class PeopleDAO extends DAO {
	private final static Logger LOG = Logger.getLogger(PeopleDAO.class.getName());
	
	//============     CREATE     ============

	/**
	 * Persists a new person in the system. An identifier will be assigned
	 * automatically to the new person.
	 * 
	 * @param name name of the new person. Can't be {@code null}.
	 * @param surname surname of the new person. Can't be {@code null}.
	 * @return a {@link Person} entity representing the persisted person.
	 * @throws DAOException if an error happens while persisting the new person.
	 * @throws IllegalArgumentException if the name or surname are {@code null}.
	 */
	public Person create(String name, String surname)
	throws DAOException, IllegalArgumentException {
		if (name == null || surname == null) {
			throw new IllegalArgumentException("name and surname can't be null");
		}
		
		try (Connection conn = this.getConnection()) {
			final String query = "INSERT INTO people VALUES(null, ?, ?)";
			
			try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, name);
				statement.setString(2, surname);
				
				if (statement.executeUpdate() == 1) {
					try (ResultSet resultKeys = statement.getGeneratedKeys()) {
						if (resultKeys.next()) {
							return new Person(resultKeys.getInt(1), name, surname);
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
			LOG.log(Level.SEVERE, "Error adding a person", e);
			throw new DAOException(e);
		}
	}

	public Person create(Connection conn, String name, String surname) 
	throws SQLException, IllegalArgumentException {
		if (name == null || surname == null) {
			throw new IllegalArgumentException("name and surname can't be null");
		}

		final String query = "INSERT INTO people VALUES(null, ?, ?)";

		try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, name);
			statement.setString(2, surname);
			
			if (statement.executeUpdate() == 1) {
				try (ResultSet resultKeys = statement.getGeneratedKeys()) {
					if (resultKeys.next()) {
						return new Person(resultKeys.getInt(1), name, surname);
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
	}

	//============     READ     ============

	/**
	 * Returns a person stored persisted in the system.
	 * 
	 * @param id identifier of the person.
	 * @return a person with the provided identifier.
	 * @throws DAOException if an error happens while retrieving the person.
	 * @throws IllegalArgumentException if the provided id does not corresponds
	 * with any persisted person.
	 */
	public Person get(long id)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM people WHERE id_person=?";
			
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
			LOG.log(Level.SEVERE, "Error getting a person", e);
			throw new DAOException(e);
		}
	}
	
	/**
	 * Returns a list with all the people persisted in the system.
	 * 
	 * @return a list with all the people persisted in the system.
	 * @throws DAOException if an error happens while retrieving the people.
	 */
	public List<Person> list() throws DAOException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM people";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				try (final ResultSet result = statement.executeQuery()) {
					final List<Person> people = new LinkedList<>();
					
					while (result.next()) {
						people.add(rowToEntity(result));
					}
					
					return people;
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error listing people", e);
			throw new DAOException(e);
		}
	}
	
	// Add specific read methods if needed

	//============     UPDATE     ============

	/**
	 * Modifies a person previously persisted in the system. The person will be
	 * retrieved by the provided id and its current name and surname will be
	 * replaced with the provided.
	 * 
	 * @param person a {@link Person} entity with the new data.
	 * @throws DAOException if an error happens while modifying the new person.
	 * @throws IllegalArgumentException if the person is {@code null}.
	 */
	public void update(Person person)
	throws DAOException, IllegalArgumentException {
		if (person == null) {
			throw new IllegalArgumentException("person can't be null");
		}
		
		try (Connection conn = this.getConnection()) {
			final String query = "UPDATE people SET name=?, surname=? WHERE id_person=?";
			
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, person.getName());
				statement.setString(2, person.getSurname());
				statement.setLong(3, person.getId());
				
				if (statement.executeUpdate() != 1) {
					throw new IllegalArgumentException("name and surname can't be null");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error modifying a person", e);
			throw new DAOException();
		}
	}
	
	// Add specific update methods if needed

	//============     DELETE     ============

	/**
	 * Removes a persisted person from the system.
	 * 
	 * @param id identifier of the person to be deleted.
	 * @throws DAOException if an error happens while deleting the person.
	 * @throws IllegalArgumentException if the provided id does not corresponds
	 * with any persisted person.
	 */
	public void delete(long id)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection()) {
			final String query = "DELETE FROM people WHERE id_person=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setLong(1, id);
				
				if (statement.executeUpdate() != 1) {
					throw new IllegalArgumentException("Invalid id");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error deleting a person", e);
			throw new DAOException(e);
		}
	}
	
	// Add specific delete methods if needed

	//============ OTHER METHODS  ============

	
	//============   AUXILIARY   ============

	/**
	 * Obtain a connection from the connection pool.
	 * @return an open connection to the database.
	 * @throws SQLException if an error happens while establishing the connection with the database.
	 */
	public Connection getConnection() throws SQLException {
		return super.getConnection();
	}


	public boolean exists(long id) throws DAOException{
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT 1 FROM people WHERE id_person=?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setLong(1, id);

				try (final ResultSet result = statement.executeQuery()) {
					return result.next();
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error checking if a person exists", e);
			throw new DAOException(e);
		}
	}

	private Person rowToEntity(ResultSet row) throws SQLException {
		return new Person(
			row.getLong("id_person"),
			row.getString("name"),
			row.getString("surname")
		);
	}
}
