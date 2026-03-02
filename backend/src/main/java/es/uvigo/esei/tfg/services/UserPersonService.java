package es.uvigo.esei.tfg.services;

import es.uvigo.esei.tfg.dao.PeopleDAO;
import es.uvigo.esei.tfg.dao.UsersDAO;
import es.uvigo.esei.tfg.dto.auth.RegisterRequest;
import es.uvigo.esei.tfg.entities.Person;
import es.uvigo.esei.tfg.entities.User;

import es.uvigo.esei.tfg.exceptions.DAOException;



import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

public class UserPersonService {
    private final static Logger LOG = Logger.getLogger(UserPersonService.class.getName());

    private final UsersDAO usersDAO;
    private final PeopleDAO peopleDAO;

    public UserPersonService() {
        this.usersDAO = new UsersDAO();
        this.peopleDAO = new PeopleDAO();
    }

    /**
     * Creates a new user and the associated person record in the database within a transaction.
     * This method ensures that both the user and person records are created successfully, 
     * and if any error occurs, the transaction is rolled back to maintain data integrity.
     * 
     * @param credentials the registration credentials containing the user information to be stored in the database.
     * @return a {@link User} entity representing the newly created user.
     * @throws DAOException if an error occurs while accessing the data source or if the transaction fails.
     */
    public User createUser(RegisterRequest credentials) 
    throws DAOException {
        Connection conn = null;
        
        try {            
            conn = usersDAO.getConnection(null);
            conn.setAutoCommit(false); 

            // Create person
            final Person person = peopleDAO.create(new Person(credentials.getName(), credentials.getSurname()), conn);

            // Create user and associate with person
            User user = credentialsToUser(credentials);
            user.setPerson(person);
            user = usersDAO.create(user, conn);
        
            // Commit the transaction if everything is successful
            conn.commit(); 
            LOG.info("User registered successfully: " + user.getUsername());

            return user;

        // Rollback the transaction in case of any error
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
            
            LOG.log(Level.SEVERE, "Error creating user", e);
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

    /**
     * Converts the provided registration credentials into a {@link User} entity.
     * 
     * @param credentials the registration credentials to convert.
     * @return a {@link User} entity with the data from the registration credentials.
     */
    private User credentialsToUser(RegisterRequest credentials) {
        User user = new User();

        user.setUsername(credentials.getUsername());
        user.setEmail(credentials.getEmail());

        // Hash the password before storing
        String passwordHash = BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt());
        user.setPasswordHash(passwordHash);

        return user;
    }

}
