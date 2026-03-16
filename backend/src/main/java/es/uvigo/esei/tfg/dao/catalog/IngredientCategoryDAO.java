package es.uvigo.esei.tfg.dao.catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.tfg.dao.DAO;
import es.uvigo.esei.tfg.entities.catalog.IngredientCategory;
import es.uvigo.esei.tfg.exceptions.DAOException;

/**
 * DAO class for the {@link IngredientCategory} entities.
 * Ingredient categories are used to classify the ingredients in the system, 
 * and can be assigned to ingredients to group them by type or other criteria.
 */
public class IngredientCategoryDAO extends DAO {
    private final static Logger LOG = Logger.getLogger(IngredientCategoryDAO.class.getName());

	//============     CREATE     ============

    /**
     * Persists a new ingredient category in the system. An identifier will be assigned
     * automatically to the new category.
     * 
     * @param category the IngredientCategory entity containing the information of the new category to be persisted.
     *                 The id field will be ignored, as it will be generated automatically.
     * @return a {@link IngredientCategory} entity representing the persisted category, including the generated identifier.
     * @throws DAOException if an error happens while persisting the new category.
     * @throws IllegalArgumentException if any of the required fields of the category is null or invalid.
     */
    public IngredientCategory create(IngredientCategory category) 
    throws DAOException, IllegalArgumentException {
        ensureIngredientCategoryDataIntegrity(category);

        try (final Connection conn = this.getConnection(null)) {

            final String query = "INSERT INTO ingredient_categories" +
                " (name, description)" +
                " VALUES (?, ?)";

            try (final PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, category.getName());
                statement.setString(2, category.getDescription());

                if (statement.executeUpdate() == 1) {
                    try (final ResultSet resultKeys = statement.getGeneratedKeys()) {
                        if (resultKeys.next()) {
                            category.setId(resultKeys.getLong(1));
                            return category;
                        } else {
                            LOG.log(Level.SEVERE, "Error retrieving generated ID for ingredient category");
                            throw new DAOException("Error retrieving generated ID for ingredient category");
                        }
                    }
                } else {
                    LOG.log(Level.SEVERE, "Error creating ingredient category: no rows affected");
                    throw new DAOException("Error creating ingredient category: no rows affected");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error creating ingredient category", e);
            throw new DAOException(e);
        } 
    }

    //============     READ     ============

    /**
     * Returns an ingredient category stored in the system.
     * 
     * @param id the identifier of the ingredient category to retrieve.
     * @return a {@link IngredientCategory} entity representing the retrieved category.
     * @throws DAOException if an error happens while retrieving the category.
     * @throws IllegalArgumentException if the provided identifier does not correspond to any existing category.
     */
    public IngredientCategory get(long id)
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM ingredient_categories WHERE id_ingredient_category=?";

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
            LOG.log(Level.SEVERE, "Error getting an ingredient category", e);
            throw new DAOException(e);
        }
    }

    /**
     * Returns a list of all ingredient categories stored in the system.
     * 
     * @return a list of {@link IngredientCategory} entities representing all the categories stored in the system.
     * @throws DAOException if an error happens while retrieving the categories.
     */
    public List<IngredientCategory> list()
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM ingredient_categories ORDER BY name ASC";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                try (final ResultSet result = statement.executeQuery()) {
                    final List<IngredientCategory> categories = new LinkedList<>();
                    while (result.next()) {
                        categories.add(rowToEntity(result));
                    }
                    return categories;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listing ingredient categories", e);
            throw new DAOException(e);
        }
    }

    //============     UPDATE     ============

    /**
     * Updates the information of an existing ingredient category in the system.
     * 
     * @param category the IngredientCategory entity containing the updated information of the category. The id field must correspond to an existing category in the system.
     * @throws DAOException if an error happens while updating the category.
     * @throws IllegalArgumentException if any of the required fields of the category is null or invalid, or if the id does not correspond to any existing category.
     */
    public void update(IngredientCategory category)
    throws DAOException, IllegalArgumentException {
        ensureIngredientCategoryDataIntegrity(category);

        try (final Connection conn = this.getConnection(null)) {
            final String query = "UPDATE ingredient_categories SET" +
                " name=?, description=?" +
                " WHERE id_ingredient_category=?";
            
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, category.getName());
                statement.setString(2, category.getDescription());
                statement.setLong(3, category.getId());

                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No ingredient category found with the provided id");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error updating ingredient category", e);
            throw new DAOException(e);
        }
    }

    //============     DELETE     ============

    /**
    * Deletes an ingredient category from the system.
    * 
    * @param id the identifier of the ingredient category to delete.
    * @throws DAOException if an error happens while deleting the category.
    * @throws IllegalArgumentException if the provided identifier does not correspond to any existing category.
    */
    public void delete(long id) 
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "DELETE FROM ingredient_categories WHERE id_ingredient_category=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, id);

                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No ingredient category found with the provided id");
                }                
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error deleting ingredient category", e);
            throw new DAOException(e);
        }
    }

    //============     AUXILIARY METHODS     ============

    /**
     * Converts a ResultSet row into an IngredientCategory entity.
     * 
     * @param result the ResultSet containing the data of the ingredient category.
     * @return an IngredientCategory entity representing the data in the ResultSet row.
     * @throws SQLException if an error happens while accessing the ResultSet data.
     */
    private IngredientCategory rowToEntity(ResultSet result)
    throws SQLException {
        return new IngredientCategory(
            result.getLong("id_ingredient_category"),
            result.getString("name"),
            result.getString("description")
        );
    }

    /**
     * Ensures the integrity of the data for an ingredient category.
     * 
     * @param category the IngredientCategory entity to validate.
     * @throws IllegalArgumentException if any of the required fields of the category is null or invalid.
     */
    private void ensureIngredientCategoryDataIntegrity(IngredientCategory category)
    throws IllegalArgumentException {
        if (category == null) {
            throw new IllegalArgumentException("Ingredient category can't be null");
        }

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient category name can't be null or empty");
        }

    }
}
