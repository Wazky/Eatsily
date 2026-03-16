package es.uvigo.esei.tfg.dao.recipe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.tfg.dao.DAO;
import es.uvigo.esei.tfg.entities.catalog.Ingredient;
import es.uvigo.esei.tfg.entities.catalog.IngredientCategory;
import es.uvigo.esei.tfg.entities.catalog.MeasurementUnit;
import es.uvigo.esei.tfg.entities.recipe.Recipe;
import es.uvigo.esei.tfg.entities.recipe.RecipeIngredient;
import es.uvigo.esei.tfg.exceptions.DAOException;

/**
 * Data Access Object (DAO) for managing {@link RecipeIngredient} entities in the database.
 */
public class RecipeIngredientDAO extends DAO{
    private final static Logger LOGGER = Logger.getLogger(RecipeIngredientDAO.class.getName());

    private final static String RECIPE_PREFIX = "recipe_";
    private final static String INGREDIENT_PREFIX = "ingredient_";
    private final static String MEASUREMENT_UNIT_PREFIX = "measurement_unit_";
    private final static String INGREDIENT_CATEGORY_PREFIX = "ingredient_category_";

    //============     CREATE     ============

    public RecipeIngredient create(RecipeIngredient recipeIngredient)
    throws DAOException, IllegalArgumentException {
        return create(recipeIngredient, null);
    }

    /**
     * Persists a new {@link RecipeIngredient} in the database.
     * 
     * @param recipeIngredient the recipe ingredient to be created.
     * @return the created recipe ingredient with the generated ID.
     * @throws DAOException if there is an error during the database operation.
     * @throws IllegalArgumentException if the provided recipe ingredient is null or has invalid data.
     */
    public RecipeIngredient create(RecipeIngredient recipeIngredient, Connection externalConnection) 
    throws DAOException, IllegalArgumentException {
        ensureRecipeIngredientDataIntegrity(recipeIngredient);

        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "INSERT INTO recipe_ingredients" +
                " (quantity, notes, recipe_id, ingredient_id, measurement_unit_id)" +
                " VALUES (?, ?, ?, ?, ?)";

            try (final PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setBigDecimal(1, recipeIngredient.getQuantity());
                statement.setString(2, recipeIngredient.getNotes());
                statement.setLong(3, recipeIngredient.getRecipe().getId());
                statement.setLong(4, recipeIngredient.getIngredient().getId());
                statement.setLong(5, recipeIngredient.getUnit().getId());

                if (statement.executeUpdate() == 1) {
                    try (final ResultSet result = statement.getGeneratedKeys()) {
                        if (result.next()) {
                            recipeIngredient.setId(result.getLong(1));
                            return recipeIngredient;
                        } else {
                            LOGGER.log(Level.SEVERE, "Failed to retrieve generated key for recipe ingredient");
                            throw new RuntimeException("Failed to retrieve generated key for recipe ingredient");
                        }
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Failed to create recipe ingredient, no rows affected");
                    throw new RuntimeException("Failed to create recipe ingredient, no rows affected");
                }
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error creating recipe ingredient", ex);
            throw new RuntimeException("Error creating recipe ingredient", ex);
        
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    /**
     * Persists a list of {@link RecipeIngredient} in the database.
     * 
     * @param ingredients the list of recipe ingredients to be created.
     * @throws DAOException if there is an error during the database operation.
     * @throws IllegalArgumentException if the provided list is null or empty, or if any of the recipe ingredients has invalid data.
     */
    public void createAll(List<RecipeIngredient> ingredients) 
    throws DAOException, IllegalArgumentException {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("List of recipe ingredients cannot be null or empty");
        }

        for (RecipeIngredient ingredient : ingredients) {
            create(ingredient);
        }
    }

    public List<RecipeIngredient> createAllForRecipe(List<RecipeIngredient> ingredients, long recipeId)
    throws DAOException, IllegalArgumentException {
        return createAllForRecipe(ingredients, recipeId, null);
    }

    public List<RecipeIngredient> createAllForRecipe(List<RecipeIngredient> ingredients, long recipeId, Connection externalConnection) 
    throws DAOException, IllegalArgumentException {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("List of recipe ingredients cannot be null or empty");
        }

        List<RecipeIngredient> createdIngredients = new LinkedList<>();
        for (RecipeIngredient ingredient : ingredients) {
            ingredient.setRecipe(new Recipe(recipeId));
            createdIngredients.add(create(ingredient, externalConnection));
        }
        return createdIngredients;
    }

    //============     READ     ============

    /**
     * Retrieves a {@link RecipeIngredient} from the database by its ID.
     * 
     * @param id the ID of the recipe ingredient to retrieve.
     * @return the retrieved recipe ingredient.
     * @throws DAOException if there is an error during the database operation.
     * @throws IllegalArgumentException if no recipe ingredient with the given ID is found.
     */
    public RecipeIngredient get(long id) 
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM recipe_ingredients WHERE id_recipe_ingredient=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, id);
            
                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {                        
                        throw new IllegalArgumentException("No recipe ingredient found");
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving recipe ingredient with ID: " + id, ex);
            throw new DAOException("Error retrieving recipe ingredient with ID: " + id, ex);
        }
    }

    /**
     * Retrieves all {@link RecipeIngredient} associated with a given recipe ID.
     * 
     * @param recipeId the ID of the recipe whose ingredients to retrieve.
     * @return a list of recipe ingredients associated with the given recipe ID.
     * @throws DAOException if there is an error during the database operation.
     * @throws IllegalArgumentException if no ingredients are found for the given recipe ID.
     */
    public List<RecipeIngredient> getByRecipeId(long recipeId)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT ri.*," +
                " i.name AS " + INGREDIENT_PREFIX + "name, i.category_id," +
                " ic.name AS " + INGREDIENT_CATEGORY_PREFIX + "name," +
                " ic.description AS " + INGREDIENT_CATEGORY_PREFIX + "description," +
                " mu.name AS " + MEASUREMENT_UNIT_PREFIX + "name," +
                " mu.abbreviation AS " + MEASUREMENT_UNIT_PREFIX + "abbreviation," +
                " mu.type AS " + MEASUREMENT_UNIT_PREFIX + "type," +
                " FROM recipe_ingredients ri" +
                " JOIN ingredients i ON ri.ingredient_id = i.id_ingredient" +
                " LEFT JOIN ingredient_categories ic ON i.category_id = ic.id_category" +
                " JOIN measurement_units mu ON ri.measurement_unit_id = mu.id_measurement_unit" +
                " WHERE recipe_id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);
            
                try (final ResultSet result = statement.executeQuery()) {
                    List<RecipeIngredient> ingredients = new LinkedList<>();
                    while (result.next()) {
                        ingredients.add(rowToEntity(result));
                    }
                    return ingredients;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving recipe ingredients for recipe ID: " + recipeId, ex);
            throw new DAOException("Error retrieving recipe ingredients for recipe ID: " + recipeId, ex);
        }
    }

    //============     UPDATE     ============

    /**
     * Updates an existing {@link RecipeIngredient} in the database.
     * 
     * @param recipeIngredient the recipe ingredient to be updated.
     * @throws DAOException if there is an error during the database operation.
     * @throws IllegalArgumentException if the provided recipe ingredient is null, has invalid data, or does not exist in the database.
     */
    public void update(RecipeIngredient recipeIngredient)
    throws DAOException, IllegalArgumentException {
        ensureRecipeIngredientDataIntegrity(recipeIngredient);

        try (final Connection conn = this.getConnection(null)) {
            final String query = "UPDATE recipe_ingredients SET" +
                " quantity=?, notes=?, measurement_unit_id=?" +
                " WHERE id_recipe_ingredient=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setBigDecimal(1, recipeIngredient.getQuantity());
                statement.setString(2, recipeIngredient.getNotes());
                statement.setLong(3, recipeIngredient.getUnit().getId());
                statement.setLong(4, recipeIngredient.getId());

                if (statement.executeUpdate() == 0) {
                    LOGGER.log(Level.SEVERE, "Failed to update recipe ingredient");
                    throw new RuntimeException("Failed to update recipe ingredient");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating recipe ingredient with ID: " + recipeIngredient.getId(), ex);
            throw new DAOException("Error updating recipe ingredient with ID: " + recipeIngredient.getId(), ex);
        }
    }

    //============     DELETE     ============

    /**
     * Deletes a {@link RecipeIngredient} from the database by its ID.
     * 
     * @param id the ID of the recipe ingredient to delete.
     * @throws DAOException if there is an error during the database operation.
     * @throws IllegalArgumentException if no recipe ingredient with the given ID is found.
     */
    public void delete(long id)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "DELETE FROM recipe_ingredients WHERE id_recipe_ingredient=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, id);

                if (statement.executeUpdate() == 0) {
                    LOGGER.log(Level.SEVERE, "Failed to delete recipe ingredient with ID: " + id);
                    throw new RuntimeException("Failed to delete recipe ingredient with ID: " + id);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting recipe ingredient with ID: " + id, ex);
            throw new DAOException("Error deleting recipe ingredient with ID: " + id, ex);
        }
    }

    public void deleteByRecipeId(long recipeId)
    throws DAOException, IllegalArgumentException {
        deleteByRecipeId(recipeId, null);
    }

    /**
     * Deletes all {@link RecipeIngredient} associated with a given recipe ID.
     * 
     * @param recipeId the ID of the recipe whose ingredients to delete.
     * @throws DAOException if there is an error during the database operation.
     * @throws IllegalArgumentException if no ingredients are found for the given recipe ID.
     */
    public void deleteByRecipeId(long recipeId, Connection externalConnection)
    throws DAOException, IllegalArgumentException {
        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;
        
        try {
            conn = this.getConnection(externalConnection);

            final String query = "DELETE FROM recipe_ingredients WHERE recipe_id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting recipe ingredients for recipe ID: " + recipeId, ex);
            throw new DAOException("Error deleting recipe ingredients for recipe ID: " + recipeId, ex);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    //============     OTHER METHODS     ============

    //============     AUXILIARY METHODS     ============

    /**
     * Converts a database row from the recipe_ingredients table into a {@link RecipeIngredient} entity.
     * This method also extracts and sets the associated {@link Recipe}, {@link Ingredient}, {@link IngredientCategory}, and {@link MeasurementUnit} if their data is present in the ResultSet.
     *
     * @param result the ResultSet containing the data of a recipe ingredient row.
     * @return a RecipeIngredient entity populated with the data from the ResultSet.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    private RecipeIngredient rowToEntity(ResultSet result)
    throws SQLException {
        Set<String> columnNames = this.getColumnNames(result);

        Recipe recipe = extractRecipe(result, columnNames);
        Ingredient ingredient = extractIngredient(result, columnNames);
        IngredientCategory ingredientCategory = extractIngredientCategory(result, columnNames);
        MeasurementUnit unit = extractMeasurementUnit(result, columnNames);

        if (ingredient != null && ingredientCategory != null) {
            ingredient.setCategory(ingredientCategory);
        }

        return new RecipeIngredient(
            result.getLong("id_recipe_ingredient"),
            result.getBigDecimal("quantity"),
            result.getString("notes"),
            ingredient,
            unit,
            recipe
        );
    }

    /**
     * Extracts recipe information from the ResultSet.
     *
     * @param result the ResultSet containing the data.
     * @param columnNames the set of column names present in the ResultSet.
     * @return a Recipe entity populated with the data from the ResultSet.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    private Recipe extractRecipe(ResultSet result, Set<String> columnNames)
    throws SQLException {

        // 'recipe_id' column is mandatory
        Recipe recipe = new Recipe(result.getLong("recipe_id"));
        
        if (columnNames.contains(RECIPE_PREFIX + "title")) {
            recipe.setTitle(result.getString(RECIPE_PREFIX + "title"));
        }

        if (columnNames.contains(RECIPE_PREFIX + "description")) {
            recipe.setDescription(result.getString(RECIPE_PREFIX + "description"));
        }

        if (columnNames.contains(RECIPE_PREFIX + "preparation_time")) {
            recipe.setPreparationTime(result.getInt(RECIPE_PREFIX + "preparation_time"));
        }

        if (columnNames.contains(RECIPE_PREFIX + "cooking_time")) {
            recipe.setCookingTime(result.getInt(RECIPE_PREFIX + "cooking_time"));
        }

        if (columnNames.contains(RECIPE_PREFIX + "difficulty")) {
            recipe.setDifficulty(Recipe.Difficulty.valueOf(result.getString(RECIPE_PREFIX + "difficulty")));
        }

        if (columnNames.contains(RECIPE_PREFIX + "servings")) {
            recipe.setServings(result.getInt(RECIPE_PREFIX + "servings"));
        }

        if (columnNames.contains(RECIPE_PREFIX + "is_public")) {
            recipe.setPublic(result.getBoolean(RECIPE_PREFIX + "is_public"));
        }

        if (columnNames.contains(RECIPE_PREFIX + "is_lunchbox")) {
            recipe.setLunchbox(result.getBoolean(RECIPE_PREFIX + "is_lunchbox"));
        }

        if (columnNames.contains(RECIPE_PREFIX + "image_path")) {
            recipe.setImagePath(result.getString(RECIPE_PREFIX + "image_path"));
        }

        return recipe;
    }

    /**
     * Extracts ingredient information from the ResultSet.
     *
     * @param result the ResultSet containing the data.
     * @param columnNames the set of column names present in the ResultSet.
     * @return an Ingredient entity populated with the data from the ResultSet.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    private Ingredient extractIngredient(ResultSet result, Set<String> columnNames)
    throws SQLException {
        // 'ingredient_id' column is mandatory
        Ingredient ingredient = new Ingredient(result.getLong("ingredient_id"));

        if (columnNames.contains(INGREDIENT_PREFIX + "name")) {
            ingredient.setName(result.getString(INGREDIENT_PREFIX + "name"));
        }

        return ingredient;
    }

    /**
     * Extracts ingredient category information from the ResultSet.
     *
     * @param result the ResultSet containing the data.
     * @param columnNames the set of column names present in the ResultSet.
     * @return an IngredientCategory entity populated with the data from the ResultSet, or null if category data is not present.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    private IngredientCategory extractIngredientCategory(ResultSet result, Set<String> columnNames)
    throws SQLException {
            if (!columnNames.contains("category_id")) {
                return null;
            }

            IngredientCategory category = new IngredientCategory();
            category.setId(result.getLong("category_id"));

            if (columnNames.contains(INGREDIENT_CATEGORY_PREFIX + "name")) {
                category.setName(result.getString(INGREDIENT_CATEGORY_PREFIX + "name"));
            }

            if (columnNames.contains(INGREDIENT_CATEGORY_PREFIX + "description")) {
                category.setDescription(result.getString(INGREDIENT_CATEGORY_PREFIX + "description"));
            }

            return category;
    }

    /**
     * Extracts measurement unit information from the ResultSet.
     *
     * @param result the ResultSet containing the data.
     * @param columnNames the set of column names present in the ResultSet.
     * @return a MeasurementUnit entity populated with the data from the ResultSet.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    private MeasurementUnit extractMeasurementUnit(ResultSet result, Set<String> columnNames)
    throws SQLException {
        // 'measurement_unit_id' column is mandatory
        MeasurementUnit unit = new MeasurementUnit(result.getLong("measurement_unit_id"));

        if (columnNames.contains(MEASUREMENT_UNIT_PREFIX + "name")) {
            unit.setName(result.getString(MEASUREMENT_UNIT_PREFIX + "name"));
        }

        if (columnNames.contains(MEASUREMENT_UNIT_PREFIX + "abbreviation")) {
            unit.setAbbreviation(result.getString(MEASUREMENT_UNIT_PREFIX + "abbreviation"));
        }

        return unit;
    }

    /**
     * Validates the integrity of a {@link RecipeIngredient} entity before performing database operations.
     * This method checks that the recipe ingredient is not null, has a valid associated recipe and ingredient, has a positive quantity, and has a valid measurement unit.
     *
     * @param recipeIngredient the recipe ingredient to validate.
     * @throws IllegalArgumentException if any of the integrity checks fail.
     */
    private void ensureRecipeIngredientDataIntegrity(RecipeIngredient recipeIngredient) 
    throws IllegalArgumentException {
        if (recipeIngredient == null) {
            throw new IllegalArgumentException("Recipe ingredient cannot be null");
        }

        if (recipeIngredient.getRecipe() == null || recipeIngredient.getRecipe().getId() <= 0) {
            throw new IllegalArgumentException("Recipe ingredient must be associated with a valid recipe");
        }

        if (recipeIngredient.getIngredient() == null || recipeIngredient.getIngredient().getId() <= 0) {
            throw new IllegalArgumentException("Recipe ingredient must be associated with a valid ingredient");
        }

        if (recipeIngredient.getQuantity() == null || recipeIngredient.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Recipe ingredient quantity must be greater than zero");
        }

        if (recipeIngredient.getUnit() == null || recipeIngredient.getUnit().getId() <= 0) {
            throw new IllegalArgumentException("Recipe ingredient must be associated with a valid measurement unit");
        }
    }

}
