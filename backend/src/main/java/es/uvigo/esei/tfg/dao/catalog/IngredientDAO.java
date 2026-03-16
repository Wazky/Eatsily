package es.uvigo.esei.tfg.dao.catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.tfg.dao.DAO;
import es.uvigo.esei.tfg.entities.catalog.Ingredient;
import es.uvigo.esei.tfg.entities.catalog.IngredientCategory;
import es.uvigo.esei.tfg.exceptions.DAOException;

/**
 * DAO class for the {@link Ingredient} entities.
 */
public class IngredientDAO extends DAO {
    private final static Logger LOG = Logger.getLogger(IngredientDAO.class.getName());

    private final static String INGREDIENT_CATEGORY_PREFIX = "ingredient_category_";

	//============     CREATE     ============

    /**
     * Persists a new ingredient in the system. An identifier will be assigned
     * automatically to the new ingredient.
     * 
     * @param ingredient the ingredient to create. Can't be {@code null}.
     * @return a {@link Ingredient} entity representing the persisted ingredient.
     * @throws DAOException if an error happens while persisting the new ingredient.
     * @throws IllegalArgumentException if the ingredient is {@code null} or has invalid data.
     */
    public Ingredient create(Ingredient ingredient) 
    throws DAOException, IllegalArgumentException {
        ensureIngredientDataIntegrity(ingredient);

        try (final Connection conn = this.getConnection(null)) {
            final String query = "INSERT INTO ingredients" +
                " (name, category_id)" +
                " VALUES (?, ?)";
            
            try (final PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, ingredient.getName());
                if (ingredient.getCategory() != null) {
                    statement.setLong(2, ingredient.getCategory().getId());
                } else {
                    statement.setNull(2, Types.BIGINT);
                }

                if (statement.executeUpdate() == 1) {
                    try (final ResultSet result = statement.getGeneratedKeys()) {
                        if (result.next()) {
                            ingredient.setId(result.getLong(1));
                            return ingredient;
                        } else {
                            LOG.log(Level.SEVERE, "Error creating an ingredient: no ID obtained");
                            throw new DAOException("Error creating an ingredient: no ID obtained");
                        }
                    }
                } else {
                    LOG.log(Level.SEVERE, "Error creating an ingredient: no rows affected");
                    throw new DAOException("Error creating an ingredient: no rows affected");
                }               
            }    
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error creating an ingredient: " + e);
            throw new DAOException(e);
        }
    }

    //============     READ     ============

    /**
     * Retrieves an ingredient by its identifier.
     * 
     * @param id the identifier of the ingredient to retrieve.
     * @return a {@link Ingredient} entity representing the retrieved ingredient.
     * @throws DAOException if an error happens while retrieving the ingredient.
     * @throws IllegalArgumentException if no ingredient exists with the provided identifier.
     */
    public Ingredient get(long id)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT i.*," +
                " ic.name AS " + INGREDIENT_CATEGORY_PREFIX + "name," +
                " ic.description AS " + INGREDIENT_CATEGORY_PREFIX + "description" +
                " FROM ingredients i" +
                " LEFT JOIN ingredient_categories ic ON i.category_id = ic.id_ingredient_category" +
                " WHERE i.id_ingredient=?";

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
            LOG.log(Level.SEVERE, "Error getting an ingredient", e);
            throw new DAOException(e);
        }
    }

    /**
     * Retrieves an ingredient by its name.
     * 
     * @param name the name of the ingredient to retrieve.
     * @return a {@link Ingredient} entity representing the retrieved ingredient.
     * @throws DAOException if an error happens while retrieving the ingredient.
     * @throws IllegalArgumentException if no ingredient exists with the provided name, or if the name is null or blank.
     */
    public Ingredient getByName(String name)
    throws DAOException, IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or blank");
        }

        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT i.*," +
                " ic.name AS " + INGREDIENT_CATEGORY_PREFIX + "name," +
                " ic.description AS " + INGREDIENT_CATEGORY_PREFIX + "description" +
                " FROM ingredients i" +
                " LEFT JOIN ingredient_categories ic ON i.category_id = ic.id_ingredient_category" +
                " WHERE i.name=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, name);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        throw new IllegalArgumentException("Invalid name");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting an ingredient by name", e);
            throw new DAOException(e);
        }
    }

    /**
     * Searches for ingredients whose name contains the provided search term.
     * 
     * @param term the search term to look for in the ingredient names. Can't be {@code null} or blank.
     * @return a list of {@link Ingredient} entities representing the ingredients whose name contains the search term. The list will be empty if no ingredient matches the search criteria.
     * @throws DAOException if an error happens while searching for ingredients.
     * @throws IllegalArgumentException if the search term is {@code null} or blank.
     */
    public List<Ingredient> search(String term)
    throws DAOException, IllegalArgumentException {
        if (term == null || term.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or blank");
        }

        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT i.*," +
                " ic.name AS " + INGREDIENT_CATEGORY_PREFIX + "name," +
                " ic.description AS " + INGREDIENT_CATEGORY_PREFIX + "description" +
                " FROM ingredients i" +
                " LEFT JOIN ingredient_categories ic ON i.category_id = ic.id_ingredient_category" +
                " WHERE i.name LIKE ?" +
                " ORDER BY i.name ASC";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, "%" + term + "%");

                try (final ResultSet result = statement.executeQuery()) {
                    final List<Ingredient> ingredients = new LinkedList<>();
                    while (result.next()) {
                        ingredients.add(rowToEntity(result));
                    }
                    return ingredients;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error searching ingredients", e);
            throw new DAOException(e);
        }
    }

    /**
     * Retrieves all the ingredients persisted in the system.
     * 
     * @return a list of {@link Ingredient} entities representing all the ingredients persisted in the system. The list will be empty if no ingredient is persisted.
     * @throws DAOException if an error happens while retrieving the ingredients.
     */
    public List<Ingredient> list()
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT i.*," +
                " ic.name AS " + INGREDIENT_CATEGORY_PREFIX + "name," +
                " ic.description AS " + INGREDIENT_CATEGORY_PREFIX + "description" +
                " FROM ingredients i" + 
                " LEFT JOIN ingredient_categories ic ON i.category_id = ic.id_ingredient_category" +
                " ORDER BY i.name ASC";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                try (final ResultSet result = statement.executeQuery()) {
                    final List<Ingredient> ingredients = new LinkedList<>();  
                    while (result.next()) {
                        ingredients.add(rowToEntity(result));
                    }
                    return ingredients;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting ingredients", e);
            throw new DAOException(e);
        }
    }

    //============     UPDATE     ============

    /**
     * Modifies an ingredient previously persisted in the system. The ingredient will be
     * retrieved by the provided id and its current name and category will be updated
     * with the values of the provided ingredient.
     * 
     * @param ingredient the ingredient with the updated data. Can't be {@code null}.
     * The ingredient must have a valid id corresponding to a persisted ingredient, and valid name and category data.
     * @throws DAOException if an error happens while updating the ingredient.
     * @throws IllegalArgumentException if the ingredient is {@code null}, has invalid data, or if no persisted ingredient exists with the provided id.
     */
    public void update(Ingredient ingredient)
    throws DAOException, IllegalArgumentException {
        ensureIngredientDataIntegrity(ingredient);
        
        try (final Connection conn = this.getConnection(null)) {
            final String query = "UPDATE ingredients SET" +
                " name=?, category_id=?" +
                " WHERE id_ingredient=?";
            
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, ingredient.getName());
                if (ingredient.getCategory() != null) {
                    statement.setLong(2, ingredient.getCategory().getId());
                } else {
                    statement.setNull(2, Types.BIGINT);
                }
                statement.setLong(3, ingredient.getId());

                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No ingredient found with the provided id");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error updating an ingredient", e);
            throw new DAOException(e);
        }
    }

    //============     DELETE     ============

    /**
     * Removes a persisted ingredient from the system.
     * 
     * @param id the identifier of the ingredient to remove.
     * @throws DAOException if an error happens while deleting the ingredient.
     * @throws IllegalArgumentException if no ingredient exists with the provided identifier.
     */
    public void delete(long id)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "DELETE FROM ingredients WHERE id_ingredient=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, id);

                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No ingredient found with the provided id");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error deleting an ingredient", e);
            throw new DAOException(e);
        }
    }

    //============     AUXILIARY METHODS     ============
    
    /**
     * Converts a ResultSet row into an Ingredient entity.
     * 
     * @param result the ResultSet containing the data of the ingredient.
     * @return an Ingredient entity representing the data in the ResultSet row.
     * @throws SQLException if an error happens while accessing the ResultSet data.
     */
    private Ingredient rowToEntity(ResultSet result)
    throws SQLException {
        Set<String> columnNames = this.getColumnNames(result);

        IngredientCategory category = extractIngredientCategory(result, columnNames);
        
        return new Ingredient(
            result.getLong("id_ingredient"),
            result.getString("name"),
            category
        );
    }

    /**
     * Extracts an IngredientCategory entity from the ResultSet if the category_id column is present and not null.
     * 
     * @param result the ResultSet containing the data of the ingredient and its category.
     * @param columnNames the set of column names present in the ResultSet.
     * @return an IngredientCategory entity representing the category of the ingredient, or {@code null} if the category_id column is not present or is null.
     * @throws SQLException if an error happens while accessing the ResultSet data.
     */
    private IngredientCategory extractIngredientCategory(ResultSet result, Set<String> columnNames)
    throws SQLException {
        if (!columnNames.contains("category_id")) {
            return null; // If the column is not present, we cannot extract the category
        } 

        IngredientCategory category = new IngredientCategory();
        if (columnNames.contains("category_id") && !result.wasNull()) {
            category.setId(result.getLong("category_id"));
        }

        if (columnNames.contains(INGREDIENT_CATEGORY_PREFIX + "name")) {
            category.setName(result.getString(INGREDIENT_CATEGORY_PREFIX + "name"));
        }

        if (columnNames.contains(INGREDIENT_CATEGORY_PREFIX + "description")) {
            category.setDescription(result.getString(INGREDIENT_CATEGORY_PREFIX + "description"));
        }

        return category;
    }

    /**
     * Ensures the integrity of the data for an ingredient.
     * 
     * @param ingredient the Ingredient entity to validate.
     * @throws IllegalArgumentException if any of the required fields of the ingredient is null or invalid.
     */
    private void ensureIngredientDataIntegrity(Ingredient ingredient) throws IllegalArgumentException {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        if (ingredient.getName() == null || ingredient.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or blank");
        }
    }
    
}
