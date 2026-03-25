package es.uvigo.esei.tfg.services.recipe;

import es.uvigo.esei.tfg.dao.catalog.IngredientDAO;
import es.uvigo.esei.tfg.dao.catalog.MeasurementUnitDAO;
import es.uvigo.esei.tfg.dao.recipe.RecipeDAO;
import es.uvigo.esei.tfg.dao.recipe.RecipeIngredientDAO;
import es.uvigo.esei.tfg.dao.recipe.RecipeStepDAO;
import es.uvigo.esei.tfg.dao.user.UsersDAO;
import es.uvigo.esei.tfg.dto.recipe.requests.CreateRecipeRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.RecipeIngredientRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.RecipeStepRequest;
import es.uvigo.esei.tfg.dto.recipe.responses.RecipeDetailResponse;
import es.uvigo.esei.tfg.dto.recipe.responses.RecipeIngredientResponse;
import es.uvigo.esei.tfg.dto.recipe.responses.RecipeStepResponse;
import es.uvigo.esei.tfg.dto.recipe.responses.RecipeSummaryResponse;
import es.uvigo.esei.tfg.entities.catalog.Ingredient;
import es.uvigo.esei.tfg.entities.catalog.MeasurementUnit;
import es.uvigo.esei.tfg.entities.recipe.Recipe;
import es.uvigo.esei.tfg.entities.recipe.Recipe.Difficulty;
import es.uvigo.esei.tfg.entities.recipe.RecipeIngredient;
import es.uvigo.esei.tfg.entities.recipe.RecipeStep;
import es.uvigo.esei.tfg.entities.user.User;
import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.exceptions.ValidationException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class RecipeManagmentService {
    private final static Logger LOG = Logger.getLogger(RecipeManagmentService.class.getName());

    private final RecipeDAO recipeDAO;
    private final RecipeIngredientDAO recipeIngredientDAO;
    private final RecipeStepDAO recipeStepDAO;
    private final IngredientDAO ingredientDAO;
    private final MeasurementUnitDAO measurementUnitDAO;
    private final UsersDAO usersDAO;

    public RecipeManagmentService() {
        this.recipeDAO = new RecipeDAO();
        this.recipeIngredientDAO = new RecipeIngredientDAO();
        this.recipeStepDAO = new RecipeStepDAO();
        this.ingredientDAO = new IngredientDAO();  
        this.measurementUnitDAO = new MeasurementUnitDAO();
        this.usersDAO = new UsersDAO();
    }

    public RecipeDetailResponse createRecipe(CreateRecipeRequest request, String username) 
    throws ValidationException, DAOException, IllegalArgumentException {
        validateCreateRequest(request);

        User user = usersDAO.getByUsername(username); // Check if user exists, will throw an exception if not

        // Resolve ingredient and measurement unit references to ensure they exist and to get the full entities
        List<RecipeIngredient> resolvedIngredients = resolveIngredients(request.getIngredients());

        Connection conn = null;
        try {
            conn = recipeDAO.getConnection(null);
            conn.setAutoCommit(false);

            // Build and persist the recipe entity
            Recipe recipe = buildRecipeEntityFromRequest(request, user);
            recipe = recipeDAO.create(recipe, conn);

            // Persist the recipe ingredients, associating them with the created recipe
            List<RecipeIngredient> ingredients = recipeIngredientDAO.createAllForRecipe(resolvedIngredients, recipe.getId(), conn);

            // Persist the recipe steps, associating them with the created recipe
            List<RecipeStep> steps = buildRecipeStepEntitiesFromRequest(request.getSteps(), recipe.getId());
            steps = recipeStepDAO.createAll(steps, conn);

            conn.commit();
            LOG.info("Recipe created successfully with id " + recipe.getId());

            return buildRecipeDetailResponse(recipe, ingredients, steps);
        
        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        }
    }

    public RecipeDetailResponse getRecipebyId(long recipeId, String username)
    throws DAOException, IllegalArgumentException {
        Recipe recipe = recipeDAO.get(recipeId);

        User user = usersDAO.getByUsername(username);
        if (!recipe.isPublic() && recipe.getUser().getId() != user.getId()) {
            throw new IllegalArgumentException("Recipe not found");
        }

        User recipeAuthor = usersDAO.get(recipe.getUser().getId());
        List<RecipeIngredient> ingredients = recipeIngredientDAO.getByRecipeId(recipeId);
        List<RecipeStep> steps = recipeStepDAO.getByRecipeId(recipeId);

        recipe.setUser(recipeAuthor);
        return buildRecipeDetailResponse(recipe, ingredients, steps);
    }

    public List<RecipeSummaryResponse> getPublicRecipes()
    throws DAOException {
        List<Recipe> recipes = recipeDAO.getPublic();
        return toSummaryResponseList(recipes);
    }

    public List<RecipeSummaryResponse> getUserRecipes(String username)
    throws DAOException, IllegalArgumentException {
        User user = usersDAO.getByUsername(username);
        List<Recipe> recipes = recipeDAO.getByUserId(user.getId());
        return toSummaryResponseList(recipes);
    }

    public RecipeDetailResponse updateRecipe(long recipeId, CreateRecipeRequest request, String username)
    throws ValidationException, DAOException, IllegalArgumentException {
        validateCreateRequest(request);

        // Ensure the user is the owner of the recipe and get the user entity for later use
        User user = usersDAO.getByUsername(username); 
        ensureRecipeOwnership(recipeId, user.getId());

        List<RecipeIngredient> resolvedIngredients = resolveIngredients(request.getIngredients());

        Connection conn = null;
        try {
            conn = recipeDAO.getConnection(null);
            conn.setAutoCommit(false);

            Recipe oldRecipe = recipeDAO.get(recipeId);
            Recipe newRecipe = buildRecipeEntityFromRequest(request, user);

            newRecipe.setId(recipeId);
            newRecipe.setUser(user);
            recipeDAO.update(newRecipe, conn);

            //
            recipeIngredientDAO.deleteByRecipeId(recipeId, conn);
            recipeStepDAO.deleteByRecipeId(recipeId, conn);
            
            List<RecipeIngredient> ingredients = recipeIngredientDAO.createAllForRecipe(resolvedIngredients, recipeId, conn);

            List<RecipeStep> steps = buildRecipeStepEntitiesFromRequest(request.getSteps(), recipeId);
            steps = recipeStepDAO.createAll(steps, conn);

            conn.commit();
            LOG.info("Recipe updated succesfully with ID: " + recipeId);

            return buildRecipeDetailResponse(newRecipe, ingredients, steps);
        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        }
    }

    public void updateVisibility(long recipeId, boolean isPublic, String username)
    throws DAOException, IllegalArgumentException {
        User user = usersDAO.getByUsername(username);
        ensureRecipeOwnership(recipeId, user.getId());
        recipeDAO.updateVisibility(recipeId, isPublic);
        LOG.info("Recipe " + recipeId + " visibility updated to: " + isPublic);
    }

    public void deleteRecipe(long recipeId, String username)
    throws DAOException, IllegalArgumentException {
        User user = usersDAO.getByUsername(username);
        ensureRecipeOwnership(recipeId, user.getId());
        Connection conn = null;

        try {
            conn = recipeDAO.getConnection(null);
            conn.setAutoCommit(false);

            recipeIngredientDAO.deleteByRecipeId(recipeId, conn);
            recipeStepDAO.deleteByRecipeId(recipeId, conn);
            recipeDAO.delete(recipeId, conn);

            conn.commit();
            LOG.info("Recipe deleted successfully with id: " + recipeId);

        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        }
    }

    //========== AUXILIARY METHODS ==========

    /**
     * Validates the fields of a {@link CreateRecipeRequest} to 
     * ensure that all required information is present and correctly formatted.
     *
     * @param request the create recipe request to validate
     * @throws ValidationException if the request is invalid
     */
    private void validateCreateRequest(CreateRecipeRequest request)
    throws ValidationException {
        if (request == null) {
            throw new ValidationException("Request can't be null");
        }
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new ValidationException("Recipe title is required");
        }
        if (request.getPreparationTime() < 1) {
            throw new ValidationException("Preparation time must be a positive integer");
        }
        if (request.getServings() < 1) {
            throw new ValidationException("Servings must be a positive integer");
        }
        if (request.getIngredients() == null || request.getIngredients().isEmpty()) {
            throw new ValidationException("At least one ingredient is required");
        } 
        if (request.getSteps() == null || request.getSteps().isEmpty()) {
            throw new ValidationException("At least one step is required");
        }
        for (RecipeIngredientRequest ingredient : request.getIngredients()) {
            if (ingredient.getIngredientId() == null) {
                throw new ValidationException("Ingredient ID is required for all ingredients");
            }
            if (ingredient.getQuantity() == null || ingredient.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Ingredient quantity must be greater than 0");
            }
            if (ingredient.getUnitId() == null) {
                throw new ValidationException("Unit id is required for all ingredients");
            }
        }
        for (RecipeStepRequest step : request.getSteps()) {
            if (step.getStepNumber() < 1) {
                throw new ValidationException("Step number must be a positive integer");
            }
            if (step.getDescription() == null || step.getDescription().isEmpty()) {
                throw new ValidationException("Step description is required for all steps");
            }
        }
    }

    private User ensureRecipeOwnership(long recipeId, long userId)
    throws DAOException, IllegalArgumentException {
        if (!recipeDAO.isOwner(recipeId, userId)) {
            throw new IllegalArgumentException("Access denied: you  are not owner of the recipe");
        }
        return usersDAO.get(userId);
    }

    private List<RecipeIngredient> resolveIngredients(List<RecipeIngredientRequest> ingredients) 
    throws ValidationException, DAOException {
        List<RecipeIngredient> resolved = new LinkedList<>();
        for (RecipeIngredientRequest ing : ingredients) {
            Ingredient ingredient;
            try {
                ingredient = ingredientDAO.get(ing.getIngredientId());
            } catch (IllegalArgumentException e) {
                LOG.log(Level.SEVERE, "Ingredient not found with id " + ing.getIngredientId(), e);
                throw new ValidationException("Ingredient with id " + ing.getIngredientId() + " does not exist");
            }
            
            MeasurementUnit unit;
            try {
                unit = measurementUnitDAO.get(ing.getUnitId());
            } catch (IllegalArgumentException e) {
                LOG.log(Level.SEVERE, "Measurement unit not found with id " + ing.getUnitId(), e);
                throw new ValidationException("Measurement unit with id " + ing.getUnitId() + " does not exist");
            }
            
            RecipeIngredient resolvedIngredient = new RecipeIngredient();
            resolvedIngredient.setIngredient(ingredient);
            resolvedIngredient.setQuantity(ing.getQuantity());
            resolvedIngredient.setUnit(unit);
            resolvedIngredient.setNotes(ing.getNotes());
            resolved.add(resolvedIngredient);
        }
        return resolved;
    }

    private Recipe buildRecipeEntityFromRequest(CreateRecipeRequest request, User user) {
        Recipe recipe = new Recipe();
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setPreparationTime(request.getPreparationTime() != null ? request.getPreparationTime() : 0);
        recipe.setCookingTime(request.getCookingTime() != null ? request.getCookingTime() : 0);
        recipe.setServings(request.getServings() != null ? request.getServings() : 0);
        recipe.setDifficulty(request.getDifficulty() != null ? Difficulty.valueOf(request.getDifficulty().toUpperCase()) : null);
        recipe.setPublic(request.isPublic());
        recipe.setLunchbox(request.isLunchbox());
        recipe.setImagePath(null); // image path will be set later when the image is uploaded
        recipe.setUser(user);
        recipe.setRootRecipeId(null); // root recipe will be set later if this is a variation of an existing recipe

        return recipe;        
    }

    private List<RecipeStep> buildRecipeStepEntitiesFromRequest(List<RecipeStepRequest> stepRequests, long recipeId) {
        List<RecipeStep> steps = new LinkedList<>();
        for (RecipeStepRequest stepReq : stepRequests) {
            RecipeStep step = new RecipeStep();
            step.setStepNumber(stepReq.getStepNumber());
            step.setTitle(stepReq.getTitle());
            step.setDescription(stepReq.getDescription());
            step.setImagePath(null); // image path will be set later when the image is uploaded
            step.setRecipe(new Recipe(recipeId));
            steps.add(step);
        }
        return steps;
    }

    private RecipeDetailResponse buildRecipeDetailResponse(Recipe recipe, List<RecipeIngredient> ingredients, List<RecipeStep> steps) {
        List<RecipeIngredientResponse> ingredientResponses = new LinkedList<>();
        for (RecipeIngredient ingredient : ingredients) {
            ingredientResponses.add(new RecipeIngredientResponse(
                ingredient.getId(),
                ingredient.getIngredient().getId(),
                ingredient.getIngredient().getName(),
                ingredient.getIngredient().getCategory().getName(),
                ingredient.getQuantity(),
                ingredient.getUnit().getId(),
                ingredient.getUnit().getName(),
                ingredient.getUnit().getAbbreviation(),
                ingredient.getNotes()
            ));
        }

        List<RecipeStepResponse> stepResponses = new LinkedList<>();
        for (RecipeStep step : steps) {
            stepResponses.add(new RecipeStepResponse(
                step.getId(),
                step.getStepNumber(),
                step.getTitle(),
                step.getDescription(),
                step.getImagePath()
            ));
        }

        return new RecipeDetailResponse(
            recipe.getId(),
            recipe.getTitle(),
            recipe.getDescription(),
            recipe.getPreparationTime(),
            recipe.getCookingTime(),
            recipe.getDifficulty() != null ? recipe.getDifficulty().name() : null,
            recipe.getServings(),
            recipe.isPublic(),
            recipe.isLunchbox(),
            recipe.getImagePath(),
            recipe.getUser().getId(),
            recipe.getUser().getUsername(),
            recipe.getRootRecipeId() != null ? recipe.getRootRecipeId() : 0,
            recipe.getCreatedAt().toLocalDate(),
            recipe.getUpdatedAt() != null ? recipe.getUpdatedAt().toLocalDate() : null,
            ingredientResponses,
            stepResponses
        );
    }

    private List<RecipeSummaryResponse> toSummaryResponseList(List<Recipe> recipes) {
        List<RecipeSummaryResponse> summaries = new LinkedList<>();
        for (Recipe recipe : recipes) {
            summaries.add(
                new RecipeSummaryResponse(
                    recipe.getId(),
                    recipe.getTitle(),
                    recipe.getDescription(),
                    recipe.getPreparationTime(),
                    recipe.getCookingTime(),
                    recipe.getDifficulty() != null ? recipe.getDifficulty().name() : null,
                    recipe.getServings(),
                    recipe.isPublic(),
                    recipe.isLunchbox(),
                    recipe.getImagePath(),
                    recipe.getUser().getUsername(),
                    recipe.getCreatedAt().toLocalDate()
                )
            );
        }
        return summaries;
    }

    private void rollback(Connection conn, Exception e) {
        if (conn != null) {
            try {
                conn.rollback();
                LOG.warning("Transaction rolled back due to error: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                LOG.log(Level.SEVERE, "Error rolling back transaction", rollbackEx);                
            }
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException closeEx) {
                LOG.log(Level.SEVERE, "Error closing connection", closeEx);
            }
        }
    }

}
