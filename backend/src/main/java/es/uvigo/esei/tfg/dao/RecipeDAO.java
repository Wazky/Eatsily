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

import es.uvigo.esei.tfg.entities.Recipe;
import es.uvigo.esei.tfg.exceptions.DAOException;

public class RecipeDAO extends DAO {
    private final static Logger LOG = Logger.getLogger(RecipeDAO.class.getName());

    public Recipe get(int id)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM recipes WHERE id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        throw new IllegalArgumentException("Invalid id");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting a recipe", e);
            throw new DAOException(e);
        }
    }

    public List<Recipe> list() throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM recipes";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                try (final ResultSet result = statement.executeQuery()) {
                    final List<Recipe> recipes = new LinkedList<>();
                    while (result.next()) {
                        recipes.add(rowToEntity(result));
                    }
                    
                    return recipes;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting recipes", e);
            throw new DAOException(e);
        }
    }

    public Recipe add(String name, String description, int preparationTime, int cookingTime, int servings) throws DAOException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "INSERT INTO recipes (name, description, preparation_time, cooking_time, servings) VALUES (?, ?, ?, ?, ?)";

            try (final PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, name);
                statement.setString(2, description);
                statement.setInt(3, preparationTime);
                statement.setInt(4, cookingTime);
                statement.setInt(5, servings);

                final int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new DAOException("Creating recipe failed, no rows affected.");
                }

                try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return get(generatedKeys.getInt(1));
                    } else {
                        throw new DAOException("Creating recipe failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error adding a recipe", e);
            throw new DAOException(e);
        }
    }

    public void update(Recipe recipe)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "UPDATE recipes SET name=?, description=?, preparation_time=?, cooking_time=?, servings=? WHERE id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, recipe.getName());
                statement.setString(2, recipe.getDescription());
                statement.setInt(3, recipe.getPreparationTime());
                statement.setInt(4, recipe.getCookingTime());
                statement.setInt(5, recipe.getServings());
                statement.setInt(6, recipe.getId());

                final int affectedRows =  statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new IllegalArgumentException("Invalid recipe id");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error updating a recipe", e);
            throw new DAOException(e);
        }
    }

    public void delete(int id)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "DELETE FROM recipes WHERE id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);

                final int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new IllegalArgumentException("Invalid recipe id");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error deleting a recipe", e);
            throw new DAOException(e);
        }
    }

    private Recipe rowToEntity(final ResultSet result) throws SQLException {
        final Recipe recipe = new Recipe();

        recipe.setId(result.getInt("id"));
        recipe.setName(result.getString("name"));
        recipe.setDescription(result.getString("description"));
        recipe.setPreparationTime(result.getInt("preparation_time"));
        recipe.setCookingTime(result.getInt("cooking_time"));
        recipe.setServings(result.getInt("servings"));

        return recipe;
    }
    
}
