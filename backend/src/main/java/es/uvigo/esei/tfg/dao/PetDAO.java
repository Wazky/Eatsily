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

import es.uvigo.esei.tfg.entities.Pet;
import es.uvigo.esei.tfg.entities.Species;

/**
 * DAO class for the {@link Pet} entities.
 * 
 * @author Isma
 */
public class PetDAO extends DAO {
    private final static Logger LOG = Logger.getLogger(PetDAO.class.getName());

    /**
     * Returns a pet stored persisted in the system.
     * 
     * @param id identifier of the pet.
     * @return a pet with the provided identifier.
     * @throws DAOException if an error happens while retrieving the pet.
     * @throws IllegalArgumentException if the provided id does not corresponds
     * with any persisted pet.
     */    
    public Pet get(int id) 
    throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM pets WHERE pet_id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);

                try (final ResultSet result = statement.executeQuery()) {
                    return result.next() ? rowToEntity(result) : null;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting a pet", e);
            throw new DAOException(e);
        }
    }

    /**
     * Returns a list with all the pets persisted in the system.
     * 
     * @return a list with all the pets persisted in the system.
     * @throws DAOException if an error happens while retrieving the pets.
     */
    public List<Pet> list() throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM pets";
        
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                try (final ResultSet result = statement.executeQuery()) {
                    final List<Pet> pets = new LinkedList<>();
                    while (result.next()) {
                        pets.add(rowToEntity(result));
                    }

                    return pets;

                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listing pets", e);
            throw new DAOException(e);
        }
    }

    /**
     * Returns a list with all the pets persisted in the system that belongs to a
     * specific owner.
     * 
     * @param owner_id identifier of the owner of the pets.
     * @return a list with all the pets persisted in the system that belongs to the
     * provided owner.
     * @throws DAOException if an error happens while retrieving the pets.
     * @throws IllegalArgumentException if the provided owner_id does not corresponds
     */
    public List<Pet> listByOwner(int owner_id) 
    throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM pets WHERE owner_id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, owner_id);

                try (final ResultSet result = statement.executeQuery()) {
                    final List<Pet> pets = new LinkedList<>();
                    while (result.next()) {
                        pets.add(rowToEntity(result));
                    }

                    return pets;

                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listing pets", e);
            throw new DAOException(e);
        }
    }


    /**
     * Persist a new pet in the system. The pet must have a valid owner.
     * An identifier will be assigned automatically to the new pet.
     * 
     * @param name name of the pet. Can't be {@code null}.
     * @param specie specie of the pet. Can't be {@code null}.
     * @param breed breed of the pet.
     * @param owner_id identifier of the owner of the pet.
     * @return a {@link Pet} instance representing the persisted pet.
     * @throws DAOException if an error happens while persisting the new pet.
     * @throws IllegalArgumentException if the name or specie are {@code null}.
     */
    public Pet add(String name, Species specie, String breed, int owner_id)
    throws DAOException {
        try (Connection conn = this.getConnection()) {
            final String query = "INSERT INTO pets (pet_id, name, specie, breed, owner_id) VALUES (null, ?, ?, ?, ?)";

            try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, name);
                statement.setString(2, specie.name());
                statement.setString(3, breed);
                statement.setInt(4, owner_id);

                if (statement.executeUpdate() == 1) {
                    try (ResultSet resultKeys = statement.getGeneratedKeys()) {
                        if (resultKeys.next()) {
                            return new Pet(resultKeys.getInt(1), name, specie, breed, owner_id );                                
                        } else {
                            LOG.log(Level.SEVERE, "Error retrieving inserted id");
                            throw new SQLException("Error retrieving inserted id");
                        }
                    }
                } else {
                    LOG.log(Level.SEVERE, "Error inserting pet");
                    throw new SQLException("Error inserting pet");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error adding a pet", e);
            throw new DAOException(e);
        }
    }

    /**
     * Modifies a pet previously persisted in the system. The pet will be
     * identified by its identifier. It's current name, specie, breed and owner
     * will be replaced by the provided ones.
     * 
     * @param pet a {@link Pet} entity with the new data.
     * @throws DAOException if an error happens while modifying the pet.
     * @throws IllegalArgumentException if the pet is {@code null}.
     */
    public void modify(Pet pet)
    throws DAOException, IllegalArgumentException {
        try (Connection conn = this.getConnection()) {
            final String update_query = "UPDATE pets SET name=?, specie=?, breed=?, owner_id=? WHERE pet_id=?";

            try (PreparedStatement statement = conn.prepareStatement(update_query)) {
                statement.setString(1, pet.getName());
                statement.setString(2, pet.getSpecies().name());
                statement.setString(3, pet.getBreed());
                statement.setInt(4, pet.getOwnerId());
                statement.setInt(5, pet.getId());

                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("Invalid id");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error modifying a pet", e);
            throw new DAOException();
        }
    }

    /**
     * Removes a persisted pet from the system.
     * 
     * @param id identifier of the pet to be deleted.
     * @throws DAOException if an error happens while deleting the pet.
     * @throws IllegalArgumentException if the provided id does not corresponds
     * with any persisted pet.
     */
    public void delete(int id) 
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection()) {
            final String query = "DELETE FROM pets WHERE pet_id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);

                if (statement.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Invalid id");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error deleting a pet", e);
            throw new DAOException(e);
        }

    }


    private Pet rowToEntity(ResultSet row) throws SQLException{
        return new Pet(
            row.getInt("pet_id"),
            row.getString("name"),
            Species.valueOf(row.getString("specie")),
            row.getString("breed"),
            row.getInt("owner_id")
        );
    }


}
