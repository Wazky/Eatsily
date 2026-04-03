package es.uvigo.esei.tfg.dao.recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.tfg.dao.DAO;
import es.uvigo.esei.tfg.entities.recipe.Recipe;
import es.uvigo.esei.tfg.entities.recipe.RecipeStepTranslation;
import es.uvigo.esei.tfg.exceptions.DAOException;

public class RecipeStepTranslationDAO extends DAO {
    private final static Logger LOGGER = Logger.getLogger(RecipeStepTranslationDAO.class.getName());

    //============     CREATE     ============

    public RecipeStepTranslation create(RecipeStepTranslation translation)
    throws DAOException {
        return create(translation, null);
    }

    public RecipeStepTranslation create(RecipeStepTranslation translation, Connection externalConnection)
    throws DAOException {
        ensureRecipeStepTranslationDataIntegrity(translation);

        boolean isExternalConnection  = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "INSERT INTO recipe_step_translations" +
                " (step_id, locale, title, description)" +
                " VALUES (?, ?, ?, ?)";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, translation.getStepId());
                statement.setString(2, translation.getLocale());
                statement.setString(3, translation.getTitle());
                statement.setString(4, translation.getDescription());
                
                if (statement.executeUpdate() == 1) {
                    return translation;
                } else {
                    throw new DAOException("Creating recipe step translation failed, no rows affected.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error creating recipe step translation: " + ex.getMessage(), ex);
            throw new DAOException("Error creating recipe step translation", ex);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    public List<RecipeStepTranslation> createAll(List<RecipeStepTranslation> translations)
    throws DAOException {
        return createAll(translations, null);
    }

    public List<RecipeStepTranslation> createAll(List<RecipeStepTranslation> translations, Connection externalConnection)
    throws DAOException {
        if (translations == null || translations.isEmpty()) {
            throw new IllegalArgumentException("Translations list cannot be null or empty");
        }

        List<RecipeStepTranslation> createdTranslations = new LinkedList<>();
        for (RecipeStepTranslation translation : translations) {
            createdTranslations.add(create(translation, externalConnection));
        }
        return createdTranslations;
    }

    //============     READ     ============

    public RecipeStepTranslation getByStepIdAndLocale(long stepId, String locale)
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM recipe_step_translations WHERE step_id = ? AND locale = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, stepId);
                statement.setString(2, locale);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);                        
                    } else {
                        return null; 
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching recipe step translation by step ID and locale: " + ex.getMessage());
            throw new DAOException("Error fetching recipe step translation by step ID and locale", ex);
        }
    }

    public List<RecipeStepTranslation> getByStepId(long stepId)
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM recipe_step_translations WHERE step_id = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, stepId);

                try (final ResultSet result = statement.executeQuery()) {
                    List<RecipeStepTranslation> translations = new LinkedList<>();
                    while (result.next()) {
                        translations.add(rowToEntity(result));
                    }
                    return translations;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching recipe step translations by step ID: " + ex.getMessage());
            throw new DAOException("Error fetching recipe step translations by step ID", ex);
        }
    }

    public List<RecipeStepTranslation> getByRecipeIdAndLocale(long recipeId, String locale)
    throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT rst.*" +
                " FROM recipe_step_translations rst" +
                " JOIN recipe_steps rs ON rst.step_id = rs.id_recipe_step" +
                " WHERE rs.recipe_id = ? AND rst.locale = ?" +
                " ORDER BY rs.step_number ASC";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);
                statement.setString(2, locale);

                try (final ResultSet result = statement.executeQuery()) {
                    List<RecipeStepTranslation> translations = new LinkedList<>();
                    while (result.next()) {
                        translations.add(rowToEntity(result));
                    }
                    return translations;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching recipe step translations by recipe ID and locale: " + ex.getMessage());
            throw new DAOException("Error fetching recipe step translations by recipe ID and locale", ex);
        }
    }

    //============     UPDATE     ============

    public void update(RecipeStepTranslation translation)
    throws DAOException, IllegalArgumentException {
        update(translation, null);
    }

    public void update(RecipeStepTranslation translation, Connection externalConnection)
    throws DAOException, IllegalArgumentException {
        ensureRecipeStepTranslationDataIntegrity(translation);

        boolean isExternalConnection  = isExternalConnection(externalConnection);
        Connection conn = null;

        try {
            conn = this.getConnection(externalConnection);

            final String query = "UPDATE recipe_step_translations SET" +
                " title=?, description=?" +
                " WHERE step_id=? AND locale=?";
            
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, translation.getTitle());
                statement.setString(2, translation.getDescription());
                statement.setLong(3, translation.getStepId());
                statement.setString(4, translation.getLocale());

                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No recipe step translation found with step ID: " + 
                        translation.getStepId() + " and locale: " + translation.getLocale());
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating recipe step translation: " + ex.getMessage(), ex);
            throw new DAOException("Error updating recipe step translation", ex);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    //============     DELETE     ============

    public void deleteByStepId(long stepId)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "DELETE FROM recipe_step_translations WHERE step_id = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, stepId);
                
                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No recipe step translations found with step ID: " + stepId);
                }

            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting recipe step translation: " + ex.getMessage(), ex);
            throw new DAOException("Error deleting recipe step translation", ex);
        }
    }

    public void deleteByStepIdAndLocale(long stepId, String locale)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "DELETE FROM recipe_step_translations WHERE step_id = ? AND locale = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, stepId);
                statement.setString(2, locale);
                
                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No recipe step translation found with step ID: " + stepId + " and locale: " + locale);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting recipe step translation: " + ex.getMessage(), ex);
            throw new DAOException("Error deleting recipe step translation", ex);
        }
    }

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
            
            final String query = "DELETE rst FROM recipe_step_translations rst" +
                " JOIN recipe_steps rs ON rst.step_id = rs.id_recipe_step" +
                " WHERE rs.recipe_id = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);
                
                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No recipe step translations found for recipe ID: " + recipeId);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting recipe step translations: " + ex.getMessage(), ex);
            throw new DAOException("Error deleting recipe step translations", ex);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    public void deleteByRecipeIdAndLocale(long recipeId, String locale)
    throws DAOException, IllegalArgumentException {
        deleteByRecipeIdAndLocale(recipeId, locale, null);
    }

    public void deleteByRecipeIdAndLocale(long recipeId, String locale, Connection externalConnection)
    throws DAOException, IllegalArgumentException {
        boolean isExternalConnection = isExternalConnection(externalConnection);
        Connection conn = null;
        
        try {
            conn = this.getConnection(externalConnection);    
            
            final String query = "DELETE rst FROM recipe_step_translations rst" +
                " JOIN recipe_steps rs ON rst.step_id = rs.id_recipe_step" +
                " WHERE rs.recipe_id = ? AND rst.locale = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, recipeId);
                statement.setString(2, locale);
                
                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No recipe step translations found for recipe ID: " + recipeId + " and locale: " + locale);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting recipe step translations: " + ex.getMessage(), ex);
            throw new DAOException("Error deleting recipe step translations", ex);
        } finally {
            closeConnection(conn, isExternalConnection);
        }
    }

    //============     AUXILIARY METHODS     ============

    private RecipeStepTranslation rowToEntity(ResultSet result) 
    throws SQLException {
            return new RecipeStepTranslation(
                result.getLong("step_id"),
                result.getString("locale"),
                result.getString("title"),
                result.getString("description")
            );
    }

    private void ensureRecipeStepTranslationDataIntegrity(RecipeStepTranslation translation) 
    throws IllegalArgumentException {
        if (translation == null) {
            throw new IllegalArgumentException("Recipe step translation cannot be null");
        }

        if (translation.getStepId() <= 0) {
            throw new IllegalArgumentException("Recipe step translation must be associated with a valid recipe step");
        }

        if (translation.getLocale() == null || translation.getLocale().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe step translation locale cannot be null or blank");
        }

        if (translation.getDescription() == null || translation.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe step translation description cannot be null or blank");
        }
    }

}
