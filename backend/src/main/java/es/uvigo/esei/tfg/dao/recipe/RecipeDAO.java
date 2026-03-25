package es.uvigo.esei.tfg.dao.recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;


import es.uvigo.esei.tfg.dao.DAO;
import es.uvigo.esei.tfg.entities.recipe.Recipe;
import es.uvigo.esei.tfg.entities.user.User;
import es.uvigo.esei.tfg.exceptions.DAOException;

/**
 * DAO class for the {@link Recipe} entities.
 */
public class RecipeDAO extends DAO {
    private final static Logger LOG = Logger.getLogger(RecipeDAO.class.getName());

    private final static String USER_PREFIX = "user_";

    //============     CREATE     ============
    
    public Recipe create(Recipe recipe)
    throws DAOException, IllegalArgumentException {
        return create(recipe, null);
    }

    /**
     * Persists a new recipe in the database. The recipe's ID will be
     * set to the generated value upon successful creation.
     * 
     * @param recipe The recipe to create. Must not be null and must have a valid user set.
     * @return The created recipe with its ID set.
     * @throws IllegalArgumentException if the recipe is null, has a null or blank title, has non-positive servings, or has a null user.
     * @throws DAOException if a database error occurs during creation.
     */
    public Recipe create(Recipe recipe, Connection externalConnection)
    throws DAOException, IllegalArgumentException {
        ensureRecipeDataIntegrity(recipe);

        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "INSERT INTO recipes" +
                " (title, description, preparation_time, cooking_time, difficulty, servings," +
                " is_public, is_lunchbox, image_path, user_id, root_recipe_id, created_at)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
            
            try (final PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, recipe.getTitle());
                statement.setString(2, recipe.getDescription());
                statement.setInt(3, recipe.getPreparationTime());
                statement.setInt(4, recipe.getCookingTime());
                if (recipe.getDifficulty() != null) {
                    statement.setString(5, recipe.getDifficulty().name());
                } else {
                    statement.setNull(5, java.sql.Types.VARCHAR);
                }
                statement.setInt(6, recipe.getServings());
                statement.setBoolean(7, recipe.isPublic());
                statement.setBoolean(8, recipe.isLunchbox());
                statement.setString(9, recipe.getImagePath());
                statement.setLong(10, recipe.getUser().getId());
                if (recipe.getRootRecipeId() != null) {
                    statement.setLong(11, recipe.getRootRecipeId());
                } else {
                    statement.setNull(11, java.sql.Types.BIGINT);
                }

                if (statement.executeUpdate() == 1) {
                    try (final ResultSet result = statement.getGeneratedKeys()) { 
                        if (result.next()) {                            
                            recipe.setId(result.getLong(1));
                            recipe.setCreatedAt(java.time.LocalDateTime.now()); // Set createdAt to current time since we know it was set to NOW() in the query
                            return recipe;
                        } else {
                            LOG.log(Level.SEVERE, "Failed to retrieve generated recipe ID");
                            throw new DAOException("Failed to retrieve generated recipe ID");
                        }
                    }
                } else {
                    LOG.log(Level.SEVERE, "Failed to create recipe, no rows affected");
                    throw new DAOException("Failed to create recipe, no rows affected");
                }
            }
        
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error creating recipe: " + ex.getMessage(), ex);
            throw new DAOException("Error creating recipe", ex);
        
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    //============     READ     ============

    /**
     * Retrieves a recipe by its ID.
     * 
     * @param id The ID of the recipe to retrieve.
     * @return The recipe with the given ID, or null if no such recipe exists.
     * @throws IllegalArgumentException if the ID is not positive.
     * @throws DAOException if a database error occurs during retrieval.
     */
    public Recipe get(long id)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM recipes WHERE id_recipe = ?";
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, id);
                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        return null; // No recipe found with the given ID
                    }
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error retrieving recipe: " + ex.getMessage(), ex);
            throw new DAOException("Error retrieving recipe", ex);
        }
    }

    /**
     * Retrieves all public recipes, ordered by creation date (newest first).
     * 
     * @return A list of all public recipes. The list will be empty if no public recipes exist.
     * @throws DAOException if a database error occurs during retrieval.
     */
    public List<Recipe> getPublic()
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT r.*," +
                " u.username AS " + USER_PREFIX + "username" +
                " FROM recipes r" +
                " JOIN users u ON r.user_id = u.id_user" +
                " WHERE is_public = TRUE" +
                " ORDER BY created_at DESC";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {                

                try (final ResultSet result = statement.executeQuery()) {
                    List<Recipe> recipes = new LinkedList<>();
                    while (result.next()) {
                        recipes.add(rowToEntity(result));
                    }
                    return recipes;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error retrieving public recipes: ",e);
            throw new DAOException(e);
        }
    }

    /**
     * Retrieves all recipes created by a specific user, ordered by creation date (newest first).
     * 
     * @param userId The ID of the user whose recipes to retrieve.
     * @return A list of recipes created by the specified user. The list will be empty if the user has not created any recipes.
     * @throws IllegalArgumentException if the user ID is not positive.
     * @throws DAOException if a database error occurs during retrieval.
     */
    public List<Recipe> getByUserId(long userId) 
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT r.*," +
                " u.username AS " + USER_PREFIX + "username" +
                " FROM recipes r" +
                " JOIN users u ON r.user_id = u.id_user" +
                " WHERE user_id=?" +
                " ORDER BY created_at DESC";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, userId);

                try (final ResultSet result = statement.executeQuery()) {
                    List<Recipe> recipes = new LinkedList<>();
                    while (result.next()) {
                        recipes.add(rowToEntity(result));
                    }
                    return recipes;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error retrieving recipes by user ID: ",e);
            throw new DAOException(e);
        }
    }

    //============     UPDATE     ============

    public void update(Recipe recipe)
    throws DAOException, IllegalArgumentException {
        update(recipe, null);
    }

    /**
     * Updates an existing recipe in the database. The recipe is identified by its ID.
     * 
     * @param recipe The recipe to update. Must not be null, must have a valid ID, and must have a valid user set.
     * @throws IllegalArgumentException if the recipe is null, has a non-positive ID, has a null or blank title, has non-positive servings, or has a null user.
     * @throws DAOException if a database error occurs during the update or if no recipe with the given ID exists.
     */
    public void update(Recipe recipe, Connection externalConnection)
    throws DAOException, IllegalArgumentException {
        ensureRecipeDataIntegrity(recipe);

        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "UPDATE recipes SET " +
                " title=?, description=?, preparation_time=?, cooking_time=?," +
                " difficulty=?, servings=?, is_public=?, is_lunchbox=?," +
                " image_path=?, root_recipe_id=?, updated_at=NOW()" +
                " WHERE id_recipe=?";
            
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, recipe.getTitle());
                statement.setString(2, recipe.getDescription());
                statement.setInt(3, recipe.getPreparationTime());
                statement.setInt(4, recipe.getCookingTime());
                if (recipe.getDifficulty() != null) {
                    statement.setString(5, recipe.getDifficulty().name());
                } else {
                    statement.setNull(5, java.sql.Types.VARCHAR);
                }
                statement.setInt(6, recipe.getServings());
                statement.setBoolean(7, recipe.isPublic());
                statement.setBoolean(8, recipe.isLunchbox());
                statement.setString(9, recipe.getImagePath());
                if (recipe.getRootRecipeId() != null) {
                    statement.setLong(10, recipe.getRootRecipeId());
                } else {
                    statement.setNull(10, java.sql.Types.BIGINT);
                }
                statement.setLong(11, recipe.getId());

                if (statement.executeUpdate() == 0) {
                    LOG.log(Level.SEVERE, "No recipe found with ID: " + recipe.getId());
                    throw new IllegalArgumentException("No recipe found with ID: " + recipe.getId());
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error updating recipe: " + ex.getMessage(), ex);
            throw new DAOException("Error updating recipe", ex);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    /**
     * Updates the visibility of a recipe (public or private).
     * 
     * @param recipeId The ID of the recipe to update.
     * @param isPublic The new visibility status of the recipe.
     * @throws IllegalArgumentException if no recipe with the given ID exists.
     * @throws DAOException if a database error occurs during the update.
     */
    public void updateVisibility(long recipeId, boolean isPublic)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "UPDATE recipes SET is_public=?, updated_at=NOW() WHERE id_recipe=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setBoolean(1, isPublic);
                statement.setLong(2, recipeId);

                if (statement.executeUpdate() == 0) {
                    LOG.log(Level.SEVERE, "No recipe found with ID: " + recipeId);
                    throw new IllegalArgumentException("No recipe found with ID: " + recipeId);
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error updating recipe visibility: " + ex.getMessage(), ex);
            throw new DAOException("Error updating recipe visibility", ex);
        }
    }

    //============     DELETE     ============

    public void delete(long id)
    throws DAOException, IllegalArgumentException {
        delete(id, null);
    }

    /**
     * Deletes a recipe from the database by its ID.
     * 
     * @param id The ID of the recipe to delete.
     * @throws IllegalArgumentException if no recipe with the given ID exists.
     * @throws DAOException if a database error occurs during deletion.
     */
    public void delete(long id, Connection eternalConnection)
    throws DAOException, IllegalArgumentException {
        boolean isExternalConnection = isExternalConnection(eternalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(eternalConnection);

            final String query = "DELETE FROM recipes WHERE id_recipe=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, id);
            
                if (statement.executeUpdate() == 0) {
                    LOG.log(Level.SEVERE, "No recipe found with ID: " + id);
                    throw new IllegalArgumentException("No recipe found with ID: " + id);
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error deleting recipe: " + ex.getMessage(), ex);
            throw new DAOException("Error deleting recipe", ex);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    //============     OTHER METHODS     ============
    
    /**
     * Checks if a given user is the owner of a specific recipe.
     * 
     * @param recipeId The ID of the recipe to check.
     * @param userId The ID of the user to check ownership for.
     * @return true if the user is the owner of the recipe, false otherwise.
     * @throws DAOException if a database error occurs during the check.
     */
    public boolean isOwner(long recipeId, long userId)
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT COUNT(*) FROM recipes WHERE id_recipe=? AND user_id=?";
            
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);
                statement.setLong(2, userId);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getInt(1) > 0;
                    } else {
                        return false; // Should never happen, but just in case
                    }
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error checking recipe ownership: " + ex.getMessage(), ex);
            throw new DAOException("Error checking recipe ownership", ex);
        }
    }

    //============     AUXILIARY METHODS     ============

    /**
     * Converts a ResultSet row into a Recipe entity. This method assumes that the ResultSet is already
     * positioned at the correct row and that all necessary columns are present.
     * 
     * @param result The ResultSet containing the recipe data.
     * @return A Recipe entity populated with the data from the ResultSet.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private Recipe rowToEntity(ResultSet result) throws SQLException {
        Set<String> columnNames = this.getColumnNames(result);

        Recipe recipe = new Recipe();
        recipe.setId(result.getLong("id_recipe"));
        recipe.setTitle(result.getString("title"));
        recipe.setDescription(result.getString("description"));
        recipe.setPreparationTime(result.getInt("preparation_time"));
        recipe.setCookingTime(result.getInt("cooking_time"));
        recipe.setServings(result.getInt("servings"));
        String difficultyStr = result.getString("difficulty");
        if (difficultyStr != null) {
            recipe.setDifficulty(Recipe.Difficulty.valueOf(difficultyStr));
        }
        recipe.setPublic(result.getBoolean("is_public"));
        recipe.setLunchbox(result.getBoolean("is_lunchbox"));
        recipe.setImagePath(result.getString("image_path"));
        recipe.setUser(extractUser(result, columnNames));

        if (columnNames.contains("root_recipe_id")) {
            recipe.setRootRecipeId(result.getLong("root_recipe_id"));
        }

        if (columnNames.contains("created_at")) {
            Timestamp createdAt = result.getTimestamp("created_at");
            if (createdAt != null) {
                recipe.setCreatedAt(createdAt.toLocalDateTime());
            }
        }

        if (columnNames.contains("updated_at")) {
            Timestamp updatedAt = result.getTimestamp("updated_at");
            if (updatedAt != null) {
                recipe.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        }
        return recipe;
    }

    private User extractUser(ResultSet result, Set<String> columnNames)
    throws SQLException {

        // 'user_id' column is mandatory
        User user = new User(result.getLong("user_id"));

        if (columnNames.contains(USER_PREFIX + "username")) {
            user.setUsername(result.getString(USER_PREFIX + "username"));
        }

        // Additional user fields can be extracted here if needed

        return user;
    }

    /**
     * Validates the integrity of a Recipe object before it is created or updated in the database.
     * This method checks that the recipe is not null, has a non-null and non-blank title, 
     * has positive servings, and has an associated user.
     * 
     * @param recipe The recipe to validate.
     * @throws IllegalArgumentException if any of the integrity checks fail.
     */
    private void ensureRecipeDataIntegrity(Recipe recipe)
    throws IllegalArgumentException {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null");
        }
        if (recipe.getTitle() == null || recipe.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe title cannot be null or blank");
        }
        if (recipe.getServings() <= 0) {
            throw new IllegalArgumentException("Recipe servings must be greater than zero");
        }
        if (recipe.getUser() == null) {
            throw new IllegalArgumentException("Recipe must have an associated user");
        }
    }

}
