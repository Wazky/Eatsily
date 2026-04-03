package es.uvigo.esei.tfg.dao.recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

import es.uvigo.esei.tfg.dao.DAO;
import es.uvigo.esei.tfg.entities.recipe.Recipe;
import es.uvigo.esei.tfg.entities.recipe.RecipeTranslation;
import es.uvigo.esei.tfg.entities.user.User;
import es.uvigo.esei.tfg.exceptions.DAOException;

public class RecipeTranslationDAO extends DAO {
    private final static Logger LOG = Logger.getLogger(RecipeTranslationDAO.class.getName());

    
    //============     CREATE     ============

    public RecipeTranslation create(RecipeTranslation translation)
    throws DAOException, IllegalArgumentException {
        return create(translation,  null);
    }

    public RecipeTranslation create(RecipeTranslation translation, Connection externalConnection)
    throws DAOException, IllegalArgumentException {
        ensureRecipeTranslationDataIntegrity(translation);

        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "INSERT INTO recipe_translations" +
                " (recipe_id, locale, title, description)" +
                " VALUES (?, ?, ?, ?)";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, translation.getRecipeId());
                statement.setString(2, translation.getLocale());
                statement.setString(3, translation.getTitle());
                statement.setString(4, translation.getDescription());

                if (statement.executeUpdate() == 1) {
                    return translation; // The recipe ID and locale are already set 
                } else {
                    LOG.log(Level.SEVERE, "Failed to create recipe translation: {0}", translation);
                    throw new DAOException("Failed to create recipe translation.");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "SQL error while creating recipe translation: {0}", e.getMessage());
            throw new DAOException("SQL error while creating recipe translation.", e);
        } finally {
            if (!isExternalConnection) {
                closeConnection(conn, isExternalConnection);
            }
        }
    }

    //============     READ     ============

    public RecipeTranslation getByRecipeIdAndLocale(long recipeId, String locale)
    throws DAOException, IllegalArgumentException {
        if (recipeId <= 0) {
            throw new IllegalArgumentException("Recipe ID must be positive.");
        }
        if (locale == null || locale.isEmpty()) {
            throw new IllegalArgumentException("Locale cannot be null or empty.");
        }

        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM recipe_translations WHERE recipe_id = ? AND locale =  ?";

            try (final PreparedStatement statemnent = conn.prepareStatement(query)) {
                statemnent.setLong(1, recipeId);
                statemnent.setString(2, locale);

                try (final ResultSet result = statemnent.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        return null; // No translation found for the given recipe ID and locale
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "SQL error while retrieving recipe translation: " +  e.getMessage(), e);
            throw new DAOException("SQL error while retrieving recipe translation.", e);
        }
    }

    /**
     * Retrieves all translations for a given recipe ID.
     * 
     * @param recipeId the ID of the recipe for which to retrieve translations.
     * @return a list of {@link RecipeTranslation} objects associated with the specified recipe ID.
     * @throws DAOException if a database access error occurs while retrieving the translations.
     */
    public List<RecipeTranslation> getByRecipeId(long recipeId)
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM recipe_translations WHERE recipe_id = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);

                try (final ResultSet result = statement.executeQuery()) {
                    List<RecipeTranslation> translations = new ArrayList<>();
                    while (result.next()) {
                        translations.add(rowToEntity(result));
                    }
                    return translations;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "SQL error while retrieving recipe translations: " + e.getMessage(), e);
            throw new DAOException("SQL error while retrieving recipe translations.", e);
        }
    }

    /**
     * Retrieves a list of available locale codes for the translations of a specific recipe.
     * 
     * @param recipeId the ID of the recipe for which to retrieve available locales.
     * @return a list of available locale codes.
     * @throws DAOException if a database access error occurs while retrieving the locales.
     */
    public List<String> getAvailableLocales(long recipeId)
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT locale FROM recipe_translations WHERE recipe_id = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);

                try (final ResultSet result = statement.executeQuery()) {
                    List<String> locales = new ArrayList<>();
                    while (result.next()) {
                        locales.add(result.getString("locale"));
                    }
                    return locales;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "SQL error while retrieving available locales for recipe: " + e.getMessage(), e);
            throw new DAOException("SQL error while retrieving available locales for recipe.", e);
        }
    }

    //============     UPDATE     ============


    public void update(RecipeTranslation translation)
    throws DAOException, IllegalArgumentException {
        update(translation, null);
    }

    /**
     * Updates an existing recipe translation in the database. The translation to update is identified by the combination of recipe ID and locale.
     * 
     * @param translation the {@link RecipeTranslation} object containing the updated data. The recipe ID and locale fields are used to identify the translation to update.
     * @param externalConnection an optional external {@link Connection} to use for the database operation. If null, a new connection will be created and managed internally.
     * @throws DAOException if a database access error occurs while updating the translation.
     * @throws IllegalArgumentException if the provided translation object is null, has an invalid recipe ID, or has null/empty locale or title fields.
     */
    public void update(RecipeTranslation translation, Connection externalConnection)
    throws DAOException, IllegalArgumentException {
        ensureRecipeTranslationDataIntegrity(translation);

        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "UPDATE recipe_translations SET" +
                " title = ?, description = ?" +
                " WHERE recipe_id = ? AND locale = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, translation.getTitle());
                statement.setString(2, translation.getDescription());
                statement.setLong(3, translation.getRecipeId());
                statement.setString(4, translation.getLocale());

                if (statement.executeUpdate() == 0) {
                    LOG.log(Level.SEVERE, "Failed to update recipe translation: {0}", translation);
                    throw new IllegalArgumentException("No recipe translation found with the provided recipe ID and locale.");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "SQL error while updating recipe translation: {0}", e.getMessage());
            throw new DAOException("SQL error while updating recipe translation.", e);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }


    //============     DELETE     ============

    public void deleteByRecipeId(long recipeId)
    throws DAOException, IllegalArgumentException {
        deleteByRecipeId(recipeId, null);
    }

    public void deleteByRecipeId(long recipeId, Connection externalConnection)
    throws DAOException, IllegalArgumentException {
        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "DELETE FROM recipe_translations WHERE recipe_id = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);

                if (statement.executeUpdate() == 0) {
                    LOG.log(Level.SEVERE, "Failed to delete recipe translations with recipe ID {0}", recipeId);
                    throw new IllegalArgumentException("No recipe translations found with the provided recipe ID.");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "SQL error while deleting recipe translations with recipe ID {0}: {1}", new Object[]{recipeId, e.getMessage()});
            throw new DAOException("SQL error while deleting recipe translations.", e);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    public void deleteByRecipeIdAndLocale(long recipeId, String locale)
    throws DAOException, IllegalArgumentException {
        deleteByRecipeIdAndLocale(recipeId, locale, null);
    }

    /**
     * Deletes a recipe translation from the database based on the provided recipe ID and locale. 
     * The translation to delete is identified by the combination of recipe ID and locale.
     * 
     * @param recipeId the ID of the recipe for which to delete the translation.
     * @param locale the locale code (e.g. "en", "es") of the translation to delete.
     * @param externalConnection an optional external {@link Connection} to use for the database operation. If null, a new connection will be created and managed internally.
     * @throws DAOException if a database access error occurs while deleting the translation.
     * @throws IllegalArgumentException if the provided recipe ID is not positive or if the locale is null or empty. Also thrown if no translation is found with the provided recipe ID and locale.
     */
    public void deleteByRecipeIdAndLocale(long recipeId, String locale, Connection externalConnection)
    throws DAOException, IllegalArgumentException {
        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "DELETE FROM recipe_translations WHERE recipe_id = ? AND locale = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);
                statement.setString(2, locale);

                if (statement.executeUpdate() == 0) {
                    LOG.log(Level.SEVERE, "Failed to delete recipe translation with recipe ID {0} and locale {1}", new Object[]{recipeId, locale});
                    throw new IllegalArgumentException("No recipe translation found with the provided recipe ID and locale.");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "SQL error while deleting recipe translation with recipe ID {0} and locale {1}: {2}", new Object[]{recipeId, locale, e.getMessage()});
            throw new DAOException("SQL error while deleting recipe translation.", e);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }


    //============     AUXILIARY METHODS     ============

    private RecipeTranslation rowToEntity(ResultSet result) throws SQLException {
        RecipeTranslation translation = new RecipeTranslation();
        translation.setRecipeId(result.getLong("recipe_id"));
        translation.setLocale(result.getString("locale"));
        translation.setTitle(result.getString("title"));
        translation.setDescription(result.getString("description"));

        return translation;
    }

    /**
     * Ensures that the provided {@link RecipeTranslation} object has valid data before being persisted.
     * Checks include:
     * - Non-null translation object.
     * - Positive recipe ID.
     * - Non-null and non-empty locale.
     * - Non-null and non-empty title.
     * 
     * @param translation the {@link RecipeTranslation} object to validate.
     * @throws IllegalArgumentException if any of the validation checks fail.
     */
    private void ensureRecipeTranslationDataIntegrity(RecipeTranslation translation)
    throws IllegalArgumentException {
        if (translation == null) {
            throw new IllegalArgumentException("Recipe translation cannot be null.");
        }
        if (translation.getRecipeId() <= 0) {
            throw new IllegalArgumentException("Recipe ID must be positive.");
        }
        if (translation.getLocale() == null || translation.getLocale().isEmpty()) {
            throw new IllegalArgumentException("Locale cannot be null or empty.");
        }
        if (translation.getTitle() == null || translation.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
    }

}
