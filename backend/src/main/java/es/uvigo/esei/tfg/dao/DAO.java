package es.uvigo.esei.tfg.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Simple base class for DAO (Data Access Object) classes. This super-class is
 * responsible for providing a {@link java.sql.Connection} to its sub-classes.
 *  
 * @author Miguel Reboiro Jato
 *
 */
public abstract class DAO {
	private final static Logger LOG = Logger.getLogger(DAO.class.getName());
	private final static String JNDI_NAME = "java:/comp/env/jdbc/eatsily"; 
	
	private DataSource dataSource;
	
	/**
	 * Constructs a new instance of {@link DAO}.
	 */
	public DAO() {
		try {
			this.dataSource = (DataSource) new InitialContext().lookup(JNDI_NAME);
		} catch (NamingException e) {
			LOG.log(Level.SEVERE, "Error initializing DAO", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns a {@link java.sql.Connection} to the database. The caller is responsible
	 * for closing the connection after using it.
	 * 
	 * @param externalConnection an optional connection to use. 
	 * If this parameter is not {@code null}, the provided connection will be returned. 
	 * Otherwise, a new connection will be obtained from the connection pool and returned.
	 * @return a {@link java.sql.Connection} to the database.
	 * @throws SQLException if an error happens while obtaining the connection.
	 */
	public Connection getConnection(Connection externalConnection) throws SQLException {
		return externalConnection != null ? externalConnection : this.dataSource.getConnection();
	}

    /**
     * Safely closes a connection ONLY if it was NOT provided externally.
     * 
     * @param conn the connection to close
     * @param isExternal true if the connection was provided externally
     */
    protected void closeConnection(Connection conn, boolean isExternal) {
        if (conn != null && !isExternal) {
            try {
                conn.close();
                LOG.fine("Connection closed");
            } catch (SQLException e) {
                LOG.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }

	/**
     * Checks if a connection is external (not null when passed as parameter).
     */
    protected boolean isExternalConnection(Connection externalConnection) {
        return externalConnection != null;
    }

}
