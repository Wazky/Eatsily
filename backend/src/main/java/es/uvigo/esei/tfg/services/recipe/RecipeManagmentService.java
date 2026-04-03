package es.uvigo.esei.tfg.services.recipe;

import es.uvigo.esei.tfg.dao.catalog.IngredientDAO;
import es.uvigo.esei.tfg.dao.catalog.MeasurementUnitDAO;
import es.uvigo.esei.tfg.dao.recipe.RecipeDAO;
import es.uvigo.esei.tfg.dao.recipe.RecipeIngredientDAO;
import es.uvigo.esei.tfg.dao.recipe.RecipeStepDAO;
import es.uvigo.esei.tfg.dao.recipe.RecipeStepTranslationDAO;
import es.uvigo.esei.tfg.dao.recipe.RecipeTranslationDAO;
import es.uvigo.esei.tfg.dao.user.UsersDAO;
import es.uvigo.esei.tfg.dto.recipe.requests.AddRecipeStepRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.AddRecipeTranslationRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.CreateRecipeRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.RecipeIngredientRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.RecipeStepRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.RecipeStepTranslationRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.UpdateRecipeStepRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.UpdateRecipeTranslationRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.UpdateRecipeStepTranslationRequest;
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
import es.uvigo.esei.tfg.entities.recipe.RecipeStepTranslation;
import es.uvigo.esei.tfg.entities.recipe.RecipeTranslation;
import es.uvigo.esei.tfg.entities.user.User;
import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.exceptions.ValidationException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RecipeManagmentService {
    private final static Logger LOG = Logger.getLogger(RecipeManagmentService.class.getName());

    private final static String DEFAULT_LOCALE = "en"; // default locale for recipe translations, change to emvironment variable if needed

    private final RecipeDAO recipeDAO;
    private final RecipeTranslationDAO recipeTranslationDAO;

    private final RecipeIngredientDAO recipeIngredientDAO;

    private final RecipeStepDAO recipeStepDAO;
    private final RecipeStepTranslationDAO recipeStepTranslationDAO; 

    private final IngredientDAO ingredientDAO;
    private final MeasurementUnitDAO measurementUnitDAO;
    private final UsersDAO usersDAO;

    public RecipeManagmentService() {
        this.recipeDAO = new RecipeDAO();
        this.recipeTranslationDAO = new RecipeTranslationDAO();
        this.recipeIngredientDAO = new RecipeIngredientDAO();
        this.recipeStepDAO = new RecipeStepDAO();
        this.recipeStepTranslationDAO = new RecipeStepTranslationDAO();
        this.ingredientDAO = new IngredientDAO();  
        this.measurementUnitDAO = new MeasurementUnitDAO();
        this.usersDAO = new UsersDAO();
    }


    //============     CREATE     ============

    /**
     * Creates a new recipe with its initial translation, ingredients, and steps 
     * based on the provided {@link CreateRecipeRequest} 
     * and associates it with the user identified by the given username.
     * 
     * @param request the request object containing the details of the recipe to be created
     * @param username the username of the user creating the recipe
     * @return a {@link RecipeDetailResponse} containing the details of the created recipe, including its ID and associated data
     * @throws ValidationException if the request is invalid (e.g., missing required fields, invalid ingredient references)
     * @throws DAOException if a database access error occurs while creating the recipe or any of its associated entities
     * @throws IllegalArgumentException if the user identified by the username does not exist
     */
    public RecipeDetailResponse createRecipe(CreateRecipeRequest request, String username) 
    throws ValidationException, DAOException, IllegalArgumentException {
        validateCreateRequest(request);

        // Check if the user and ingredients exist and resolve the ingredient references
        User user = usersDAO.getByUsername(username);                                               
        List<RecipeIngredient> resolvedIngredients = resolveIngredients(request.getIngredients());  

        Connection conn = null;
        try {
            conn = recipeDAO.getConnection(null);
            conn.setAutoCommit(false);

            // Build and persist the recipe entity
            Recipe recipe = buildRecipeEntityFromRequest(request, user);
            recipe = recipeDAO.create(recipe, conn);

            RecipeTranslation translation = new RecipeTranslation(recipe.getId(), request.getLocale(), request.getTitle(), request.getDescription());
            recipeTranslationDAO.create(translation, conn);
            recipe.addTranslation(translation);

            // Persist the recipe ingredients, associating them with the created recipe
            List<RecipeIngredient> ingredients = recipeIngredientDAO.createAllForRecipe(resolvedIngredients, recipe.getId(), conn);

            // Persist the recipe steps and their translations, associating them with the created recipe
            List<RecipeStep> steps = createRecipeSteps(request.getSteps(), recipe.getId(), conn);
            List<RecipeStepTranslation> stepTranslations = createRecipeStepTranslations(request.getSteps(), steps, request.getLocale(), conn);
            steps = insertStepTranslationsIntoSteps(steps, stepTranslations);


            conn.commit();
            LOG.info("Recipe created successfully with id " + recipe.getId());

            return buildRecipeDetailResponse(recipe, translation, ingredients, steps);
        
        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Adds a new translation to an existing recipe, creating a new {@link RecipeTranslation} entity
     *  associated with the specified recipe ID and the details provided in the {@link AddRecipeTranslationRequest}.
     * 
     * @param recipeId the ID of the recipe to which the translation will be added
     * @param request the request object containing the details of the translation to be added
     * @param username the username of the user adding the translation, used to verify ownership of the recipe
     * @throws ValidationException if the request is invalid (e.g., missing required fields, invalid locale code)
     * @throws DAOException if a database access error occurs while adding the translation or if the recipe does not exist
     * @throws IllegalArgumentException if the user identified by the username does not exist or if the user is not the owner of the recipe
     */
    public RecipeDetailResponse addRecipeTranslation(long recipeId, AddRecipeTranslationRequest request, String username) 
    throws ValidationException, DAOException, IllegalArgumentException {
        validateAddTranslationRequest(request);
        ensureRecipeOwnership(recipeId, usersDAO.getByUsername(username).getId());

        RecipeTranslation existingTranslation = recipeTranslationDAO.getByRecipeIdAndLocale(recipeId, request.getLocale());
        if (existingTranslation != null) {
            throw new ValidationException("A translation for locale " + request.getLocale() + " already exists for this recipe." +
                " Use the update translation endpoint to modify it instead.");
        }

        List<RecipeStep> existingRecipeSteps = recipeStepDAO.getByRecipeId(recipeId);
        validateStepTranslationIds(request.getSteps(), existingRecipeSteps);

        Connection conn = null;
        try  {
            conn = recipeTranslationDAO.getConnection(null);
            conn.setAutoCommit(false);
            
            // Recipe data translation
            RecipeTranslation translation = new RecipeTranslation(
                recipeId,
                request.getLocale(),
                request.getTitle(),
                request.getDescription()
            );
            
            recipeTranslationDAO.create(translation, conn);

            // Steps translation
            List<RecipeStepTranslation> stepTranslations = new ArrayList<>();
            for (RecipeStepTranslationRequest stepTranslationReq : request.getSteps()) {
                RecipeStepTranslation stepTranslation = new RecipeStepTranslation(stepTranslationReq.getStepId(), request.getLocale(), stepTranslationReq.getTitle(), stepTranslationReq.getDescription());
                stepTranslations.add(recipeStepTranslationDAO.create(stepTranslation, conn));
            }

            conn.commit();
            LOG.info("Recipe translation added successfully for recipe " + recipeId + " with locale " + request.getLocale());

            return getRecipebyId(recipeId, username, request.getLocale());

        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        }
        
    }

    public RecipeDetailResponse addRecipeStep(long recipeId, AddRecipeStepRequest request, String username)
    throws ValidationException, DAOException, IllegalArgumentException {
        ensureRecipeOwnership(recipeId, usersDAO.getByUsername(username).getId());

        // Verify that the locale exists for this recipe
        RecipeTranslation recipeTranslation = recipeTranslationDAO.getByRecipeIdAndLocale(recipeId, request.getLocale());

        if (recipeTranslation == null) {
            throw new ValidationException("No recipe translation found for locale " + request.getLocale() + 
            ". Please add a translation for this locale before adding steps.");
        }

        Connection conn = null;
        try {
            conn = recipeStepDAO.getConnection(null);
            conn.setAutoCommit(false);

            RecipeStep step = new RecipeStep();
            step.setStepNumber(request.getStepNumber());
            step.setImagePath(null);
            step.setRecipe(new Recipe(recipeId));
            step = recipeStepDAO.create(step, conn);

            RecipeStepTranslation stepTranslation = new RecipeStepTranslation(step.getId(), request.getLocale(), request.getTitle(), request.getDescription());
            recipeStepTranslationDAO.create(stepTranslation, conn);

            conn.commit();
            LOG.info("Recipe step added successfully for recipe " + recipeId + " with locale " + request.getLocale());

            return getRecipebyId(recipeId, username, request.getLocale());

        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        }
    }

    //============     READ     ============

    public RecipeDetailResponse getRecipebyId(long recipeId, String username, String locale)
    throws DAOException, IllegalArgumentException {
        Recipe recipe = recipeDAO.get(recipeId);

        User user = usersDAO.getByUsername(username);
        if (!recipe.isPublic() && recipe.getUser().getId() != user.getId()) {
            throw new IllegalArgumentException("Recipe not found");
        }

        // Recipe data translation with fallback to default locale if the requested one is not available
        RecipeTranslation translation = loadTranslationWithFallback(recipeId, locale);
        
        User recipeAuthor = usersDAO.get(recipe.getUser().getId());
        recipe.setUser(recipeAuthor);

        List<RecipeIngredient> ingredients = recipeIngredientDAO.getByRecipeId(recipeId);

        // Recipe steps with their translations
        List<RecipeStep> steps = recipeStepDAO.getByRecipeId(recipeId);
        List<RecipeStepTranslation> stepTranslations = recipeStepTranslationDAO.getByRecipeIdAndLocale(recipeId, translation.getLocale());
        steps = insertStepTranslationsIntoSteps(steps, stepTranslations);

        // Load the available locales for this recipe
        List<String> availableLocales = recipeTranslationDAO.getAvailableLocales(recipeId);
        List<RecipeTranslation> translations = new ArrayList<>();
        for (String loc: availableLocales) {
            translations.add(new RecipeTranslation(recipeId, loc, null, null));
        }
        recipe.setTranslations(translations);

        return buildRecipeDetailResponse(recipe, translation, ingredients, steps);
    }

    public List<RecipeSummaryResponse> getPublicRecipes(String locale)
    throws DAOException {
        List<Recipe> recipes;
        if (locale == null || locale.trim().isEmpty()) {
            recipes = recipeDAO.getPublic();
            return toSummaryResponseListWithFallback(recipes, null); 
        }

        recipes = recipeDAO.getPublicByLocale(locale);
        return toSummaryResponseList(recipes, locale);        
    }

    public List<RecipeSummaryResponse> getUserRecipes(String username, String locale)
    throws DAOException, IllegalArgumentException {
        User user = usersDAO.getByUsername(username);
        List<Recipe> recipes = recipeDAO.getByUserId(user.getId());
        return toSummaryResponseListWithFallback(recipes, locale);
    }


    //============     UPDATE     ============

    /**
     * Updates the non-translatable field of an existing recipe identified 
     * by the given recipe ID with the new details provided in the {@link CreateRecipeRequest}.
     * 
     * @param recipeId the ID of the recipe to be updated
     * @param request the request object containing the new details for the recipe update
     * @param username the username of the user performing the update, used to verify ownership of the recipe
     * @return a {@link RecipeDetailResponse} containing the updated details of the recipe after the update operation is completed
     * @throws ValidationException if the request is invalid (e.g., missing required fields, invalid ingredient references)
     * @throws DAOException if a database access error occurs while updating the recipe or any of its associated entities, or if the recipe does not exist
     * @throws IllegalArgumentException if the user identified by the username does not exist or if the user is not the owner of the recipe
     */
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

            Recipe oldRecipe = recipeDAO.get(recipeId); // Ensure the recipe exists
            Recipe newRecipe = buildRecipeEntityFromRequest(request, user);

            newRecipe.setId(recipeId);
            newRecipe.setUser(user);
            recipeDAO.update(newRecipe, conn);

            // Replace ingredients 
            recipeIngredientDAO.deleteByRecipeId(recipeId, conn);
            List<RecipeIngredient> ingredients = recipeIngredientDAO.createAllForRecipe(resolvedIngredients, recipeId, conn);

            conn.commit();
            LOG.info("Recipe updated succesfully with ID: " + recipeId);

            return getRecipebyId(recipeId, username, request.getLocale());

        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Updates an existing translation of a recipe including the translation of its steps, 
     * identified by the given recipe ID and locale with the new details provided in the {@link UpdateRecipeTranslationRequest}.
     * 
     * @param recipeId the ID of the recipe for which to update the translation
     * @param locale the locale code for the translation to update (e.g., "en", "es")
     * @param request the request object containing the new details for the translation update
     * @param username the username of the user performing the update, used to verify ownership of the recipe
     * @return a {@link RecipeDetailResponse} containing the updated details of the recipe after the translation update operation is completed
     * @throws ValidationException if the request is invalid (e.g., missing required fields)
     * @throws DAOException if a database access error occurs while updating the translation or if the recipe or translation does not exist
     * @throws IllegalArgumentException if the user identified by the username does not exist or if the user is not the owner of the recipe
     * @throws DAOException if no existing translation is found for the specified locale
     * @throws IllegalArgumentException if the specified locale in the request does not match the locale parameter
     */
    public RecipeDetailResponse updateRecipeTranslation(long recipeId, String locale, UpdateRecipeTranslationRequest request, String username)
    throws ValidationException, DAOException, IllegalArgumentException {
        validateUpdateTranslationRequest(request);
        ensureRecipeOwnership(recipeId, usersDAO.getByUsername(username).getId());

        RecipeTranslation existingTranslation = recipeTranslationDAO.getByRecipeIdAndLocale(recipeId, locale);
        if (existingTranslation == null) {
            throw new ValidationException("No existing translation found for locale " + locale + " to update." +
                " Use the add translation endpoint to create it instead.");
        }

        List<RecipeStep> existingSteps = recipeStepDAO.getByRecipeId(recipeId);
        validateStepTranslationIds(request.getSteps(), existingSteps);

        Connection conn = null;
        try {
            conn = recipeTranslationDAO.getConnection(null);

            // Update recipe translation data
            RecipeTranslation updatedTranslation = new RecipeTranslation(recipeId, locale, request.getTitle(), request.getDescription());
            recipeTranslationDAO.update(updatedTranslation);            

            // Update steps translation data
            List<RecipeStepTranslation> updatedStepTranslations = new LinkedList<>();
            for (RecipeStepTranslationRequest stepTranslationReq : request.getSteps()) {
                RecipeStepTranslation translation = new RecipeStepTranslation(stepTranslationReq.getStepId(), locale, stepTranslationReq.getTitle(), stepTranslationReq.getDescription());
                recipeStepTranslationDAO.update(translation, conn);
                updatedStepTranslations.add(translation);
            }

            conn.commit();
            LOG.info("Recipe translation updated successfully for recipe " + recipeId + " with locale " + locale);

            return getRecipebyId(recipeId, username, locale);
        
        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        } 
    }

    public RecipeDetailResponse updateRecipeStep(long recipeId, long stepId, UpdateRecipeStepRequest request, String username, String locale)
    throws ValidationException, DAOException, IllegalArgumentException {
        validateUpdateStepRequest(request);
        ensureRecipeOwnership(recipeId, usersDAO.getByUsername(username).getId());
        ensureStepBelongsToRecipe(stepId, recipeId);

        RecipeStep step = recipeStepDAO.get(stepId);
        step.setStepNumber(request.getStepNumber());
        if (request.getImagePath() != null) {
            step.setImagePath(request.getImagePath());
        }

        recipeStepDAO.update(step);
        LOG.info("Recipe step updated successfully for step " + stepId + " of recipe " + recipeId);
        
        return getRecipebyId(recipeId, username, locale);
    }

    public RecipeDetailResponse updateRecipeStepTranslation(long recipeId, long stepId, String locale, UpdateRecipeStepTranslationRequest request, String username)
    throws ValidationException, DAOException, IllegalArgumentException {
        validateUpdateStepTranslationRequest(request);
        ensureRecipeOwnership(recipeId, usersDAO.getByUsername(username).getId());
        ensureStepBelongsToRecipe(stepId, recipeId);

        RecipeStepTranslation translation = recipeStepTranslationDAO.getByStepIdAndLocale(stepId, locale);
        if (translation == null) {
            throw new IllegalArgumentException("No existing translation found for step with id " + stepId + " and locale " + locale + " to update." +
                " Use the add translation endpoint to create it instead.");
        }

        RecipeStepTranslation updatedTranslation = new RecipeStepTranslation(stepId, locale, request.getTitle(), request.getDescription());
        recipeStepTranslationDAO.update(updatedTranslation);
        LOG.info("Recipe step translation updated successfully for step " + stepId + " of recipe " + recipeId + " with locale " + locale);

        return getRecipebyId(recipeId, username, locale);
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
            recipeStepDAO.deleteByRecipeId(recipeId, conn); // Cascade deletes step translations as well
            recipeDAO.delete(recipeId, conn); // Cascade deletes recipe translations as well

            conn.commit();
            LOG.info("Recipe deleted successfully with id: " + recipeId);

        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        }
    }

    public void deleteRecipeTranslation(long recipeId, String locale, String username) 
    throws ValidationException, IllegalArgumentException, DAOException {
        User user = usersDAO.getByUsername(username);
        ensureRecipeOwnership(recipeId, user.getId());

        List<String> availableLocales = recipeTranslationDAO.getAvailableLocales(recipeId);
        if (availableLocales.size() <= 1) {
            throw new ValidationException("Cannot delete the only available translation for this recipe.");
        }

        Connection conn = null;
        try {
            conn = recipeTranslationDAO.getConnection(null);
            conn.setAutoCommit(false);
    
            recipeTranslationDAO.deleteByRecipeIdAndLocale(recipeId, locale, conn);
            recipeStepTranslationDAO.deleteByRecipeIdAndLocale(recipeId, locale, conn);

            conn.commit();
            LOG.info("Translation deleted successfully for recipe " + recipeId + " with locale " + locale);
        } catch (SQLException | DAOException e) {
            rollback(conn, e);
            throw new DAOException(e);
        } finally {
            closeConnection(conn);
        }
    }

    public RecipeDetailResponse deleteRecipeStep(long recipeId, long stepId, String username, String locale)
    throws DAOException, IllegalArgumentException {
        ensureRecipeOwnership(recipeId, usersDAO.getByUsername(username).getId());
        ensureStepBelongsToRecipe(stepId, recipeId);

        recipeStepDAO.delete(stepId); // Cascade deletes step translations as well
        LOG.info("Recipe step deleted successfully for step " + stepId + " of recipe " + recipeId);

        return getRecipebyId(recipeId, username, locale);
    }

    //========== AUXILIARY METHODS ==========

    private List<RecipeStep> createRecipeSteps(List<RecipeStepRequest> stepRequests, long recipeId, Connection conn) 
    throws DAOException {
        List<RecipeStep> steps = new LinkedList<>();
        for (RecipeStepRequest stepReq : stepRequests) {
            RecipeStep step = new RecipeStep();
            step.setStepNumber(stepReq.getStepNumber());
            step.setImagePath(null); // image path will be set later when the image is uploaded
            step.setRecipe(new Recipe(recipeId));

            // Persist the step to get its generated ID for the translations
            recipeStepDAO.create(step, conn);
            step.setId(step.getId()); // Set the generated ID back to the step entity
            steps.add(step);
        }
        return steps;
    }

    private List<RecipeStepTranslation> createRecipeStepTranslations(List<RecipeStepRequest> stepRequests, List<RecipeStep> steps, String locale, Connection conn)
    throws DAOException {
        List<RecipeStepTranslation> stepTranslations = new LinkedList<>();
        for (int i = 0; i < stepRequests.size(); i++) {
            RecipeStepRequest stepReq = stepRequests.get(i);
            RecipeStep step = steps.get(i);

            RecipeStepTranslation stepTranslation = new RecipeStepTranslation(step.getId(), locale, stepReq.getTitle(), stepReq.getDescription());
            recipeStepTranslationDAO.create(stepTranslation, conn);
            stepTranslations.add(stepTranslation);
        }
        return stepTranslations;
    }

    private List<RecipeStep> insertStepTranslationsIntoSteps(List<RecipeStep> steps, List<RecipeStepTranslation> stepTranslations) {
        for (RecipeStep step : steps) {
            List<RecipeStepTranslation> translationsForStep = new ArrayList<>();
            for (RecipeStepTranslation translation : stepTranslations) {
                if (translation.getStepId() == step.getId()) {
                    translationsForStep.add(translation);
                }
            }
            step.setTranslations(translationsForStep);
        }
        return steps;
    }

    /**
     * Loads the translation for a given recipe and locale. If a translation for the requested locale is not found, it falls back to the default locale.
     * 
     * @param recipeId the ID of the recipe for which to load the translation.
     * @param locale the locale code for the desired translation (e.g., "en", "es").
     * @return the {@link RecipeTranslation} for the requested locale, or the default locale if not found.
     * @throws DAOException if a database access error occurs while retrieving the translation, or if no translations are found for the recipe.
     * @throws IllegalArgumentException if the recipe ID is invalid or if the locale code is null or empty.
     */
    private RecipeTranslation loadTranslationWithFallback(long recipeId, String locale)
    throws DAOException, IllegalArgumentException {
        RecipeTranslation translation = recipeTranslationDAO.getByRecipeIdAndLocale(recipeId, locale);
        if (translation == null) {
            // Fallback to default locale if translation for requested locale is not found
            List<RecipeTranslation> allTranslations = recipeTranslationDAO.getByRecipeId(recipeId);
            if (allTranslations.isEmpty()) {
                throw new DAOException("No translations found for recipe with id " + recipeId);
            } else {
                return allTranslations.get(0);
            }
        }
        return translation;
    }

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
        if (request.getLocale() == null || request.getLocale().trim().isEmpty()) {
            throw new ValidationException("Locale is required");
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

    /**
     * Validates the fields of an {@link AddRecipeTranslationRequest} to ensure 
     * that all required information is present and correctly formatted 
     * for adding a new translation to an existing recipe.
     * 
     * @param request the add recipe translation request to validate
     * @throws ValidationException if the request is invalid
     */
    private void validateAddTranslationRequest(AddRecipeTranslationRequest request)
    throws ValidationException {
        if (request == null) {
            throw new ValidationException("Request can't be null");
        }
        if (request.getLocale() == null || request.getLocale().trim().isEmpty()) {
            throw new ValidationException("Locale is required");
        }
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new ValidationException("Recipe title is required");
        }
    }

    private void validateStepTranslationIds(List<RecipeStepTranslationRequest> stepTranslations, List<RecipeStep> existingSteps) 
    throws ValidationException {
        if (stepTranslations == null  || stepTranslations.size() != existingSteps.size()) {
            throw new ValidationException("The number of step translations must match the number of existing steps for the recipe.");
        }

        List<Long> existingStepIds = new ArrayList<>();
        for (RecipeStep step : existingSteps) {
            existingStepIds.add(step.getId());
        }

        List<Long> translationStepIds = new ArrayList<>();
        for (RecipeStepTranslationRequest stepTranslation : stepTranslations) {
            if (stepTranslation.getStepId() <= 0 || !existingStepIds.contains(stepTranslation.getStepId())) {
                throw new ValidationException("Each step translation must reference a valid existing step ID. Invalid step ID: " + stepTranslation.getStepId());
            }
            if (translationStepIds.contains(stepTranslation.getStepId())) {
                throw new ValidationException("Duplicate step ID found in translations: " + stepTranslation.getStepId());
            }
            translationStepIds.add(stepTranslation.getStepId());
        }
    }

    /**
     * Validates the fields of an {@link UpdateRecipeTranslationRequest} to ensure
     * that all required information is present and correctly formatted for updating an existing translation of a recipe
     * 
     * @param request the update recipe translation request to validate
     * @throws ValidationException if the request is invalid
     */
    private void validateUpdateTranslationRequest(UpdateRecipeTranslationRequest request)
    throws ValidationException {
        if (request == null) {
            throw new ValidationException("Request can't be null");
        }
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new ValidationException("Recipe title is required");
        }
    }

    private void validateUpdateStepRequest(UpdateRecipeStepRequest request) throws ValidationException {
        if (request == null) {
            throw new ValidationException("Request can't be null");
        }
        if (request.getStepNumber() == null || request.getStepNumber() < 1) {
            throw new ValidationException("Step number must be a positive integer");
        }
    }

    private void validateUpdateStepTranslationRequest(UpdateRecipeStepTranslationRequest request) throws ValidationException {
        if (request == null) {
            throw new ValidationException("Request can't be null");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new ValidationException("Step description is required");
        }
    }

    private User ensureRecipeOwnership(long recipeId, long userId)
    throws DAOException, IllegalArgumentException {
        if (!recipeDAO.isOwner(recipeId, userId)) {
            throw new IllegalArgumentException("Access denied: you  are not owner of the recipe");
        }
        return usersDAO.get(userId);
    }

    private void ensureStepBelongsToRecipe(long stepId, long recipeId)
    throws DAOException, IllegalArgumentException {
        RecipeStep step = recipeStepDAO.get(stepId);
        if (step.getRecipe().getId() != recipeId) {
            throw new IllegalArgumentException("Step with id " + stepId + " does not belong to recipe with id " + recipeId);
        }
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

    private RecipeTranslation buildRecipeTranslationEntityFromRequest(CreateRecipeRequest request, long recipeId) {
        RecipeTranslation translation = new RecipeTranslation();
        translation.setRecipeId(recipeId);
        if (request.getLocale() != null && !request.getLocale().trim().isEmpty()) {
            translation.setLocale(request.getLocale());
        } else {
            translation.setLocale(DEFAULT_LOCALE); // default locale

        }
        translation.setTitle(request.getTitle());
        translation.setDescription(request.getDescription());
        return translation;
    }

    private RecipeDetailResponse buildRecipeDetailResponse(Recipe recipe, RecipeTranslation translation, List<RecipeIngredient> ingredients, List<RecipeStep> steps) {
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
                step.getTranslation(translation.getLocale()).getTitle(),
                step.getTranslation(translation.getLocale()).getDescription(),
                step.getImagePath()
            ));
        }

        return new RecipeDetailResponse(
            recipe.getId(),
            translation.getTitle(),
            translation.getDescription(),
            translation.getLocale(),
            recipe.getAvailableLocales(),
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

    private List<RecipeSummaryResponse> toSummaryResponseList(List<Recipe> recipes, String locale) {
        List<RecipeSummaryResponse> summaries = new LinkedList<>();
        for (Recipe recipe : recipes) {
            RecipeTranslation translation = recipe.getTranslationByLocale(locale);
            summaries.add(toRecipeSummaryResponse(recipe, translation));
        }
        return summaries;
    }

    private List<RecipeSummaryResponse> toSummaryResponseListWithFallback(List<Recipe> recipes, String locale) 
    throws DAOException {
        if (locale == null || locale.trim().isEmpty()) {
            locale = DEFAULT_LOCALE;
        }
        List<RecipeSummaryResponse> summaries = new LinkedList<>();
        for (Recipe recipe: recipes) {
            RecipeTranslation translation = loadTranslationWithFallback(recipe.getId(), locale);
            summaries.add(toRecipeSummaryResponse(recipe, translation));
        }
        return summaries;
    }

    private RecipeSummaryResponse toRecipeSummaryResponse(Recipe recipe, RecipeTranslation translation) {
        return new RecipeSummaryResponse(
            recipe.getId(),
            translation.getTitle(),
            translation.getDescription(),
            recipe.getPreparationTime(),
            recipe.getCookingTime(),
            recipe.getDifficulty() != null ? recipe.getDifficulty().name() : null,
            recipe.getServings(),
            recipe.isPublic(),
            recipe.isLunchbox(),
            recipe.getImagePath(),
            recipe.getUser().getUsername(),
            recipe.getCreatedAt().toLocalDate()
        );
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
