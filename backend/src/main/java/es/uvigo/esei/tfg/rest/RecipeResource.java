package es.uvigo.esei.tfg.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Generated;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.exceptions.ValidationException;
import es.uvigo.esei.tfg.security.Secured;
import es.uvigo.esei.tfg.services.recipe.RecipeManagmentService;
import es.uvigo.esei.tfg.util.JwtUtil;
import es.uvigo.esei.tfg.dao.recipe.RecipeDAO;
import es.uvigo.esei.tfg.dto.recipe.requests.AddRecipeStepRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.AddRecipeTranslationRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.CreateRecipeRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.UpdateRecipeStepRequest;
import es.uvigo.esei.tfg.dto.recipe.requests.UpdateRecipeStepTranslationRequest;
import es.uvigo.esei.tfg.dto.recipe.responses.RecipeDetailResponse;
import es.uvigo.esei.tfg.dto.recipe.responses.RecipeSummaryResponse;
import es.uvigo.esei.tfg.dto.recipe.requests.UpdateRecipeTranslationRequest;
import es.uvigo.esei.tfg.entities.recipe.Recipe;

/**
 * REST resource for managing recipes.
 */
@Secured
@Path("/recipes")
@Produces(MediaType.APPLICATION_JSON)
public class RecipeResource extends BaseResource {
	private final static Logger LOG = Logger.getLogger(RecipeResource.class.getName());
    private final static String DEFAULT_LOCALE = "en";

	private final RecipeManagmentService recipeService;
    private final JwtUtil jwtUtil;

    /**
     * Constructs a new instance of {@link RecipeResource}.
     */
    public RecipeResource() {
        this.recipeService = new RecipeManagmentService();
        this.jwtUtil = new JwtUtil();
    }

    RecipeResource(RecipeManagmentService recipeService, JwtUtil jwtUtil) {
        this.recipeService = recipeService;
        this.jwtUtil = jwtUtil;
    }

//============     RECIPE ENDPOINTS     ============

    //============     CREATE     ============

    /**
     * Creates a new recipe.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param request The request body containing the recipe details.
     * @return A Response containing the created recipe details or an error message.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(
        @HeaderParam("Authorization") String authHeader,
        CreateRecipeRequest request
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = this.recipeService.createRecipe(request, username);
            return ok(response);

        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed creating recipe: " + ve.getMessage(), ve);
            return badRequest(ve.getMessage());
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid data creating recipe: " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error creating recipe", e);
            return internalServerError("Internal server error while creating recipe");
        } 
    }


    //============     READ     ============

    /**
     * Retrieves a recipe by its ID.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param id The ID of the recipe to retrieve.
     * @param locale Optional query parameter to specify the locale for the recipe translation.
     * @return A Response containing the recipe details or an error message.
     */
    @GET
    @Path("/{id}")
    public Response get(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        @QueryParam("locale") String locale
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = this.recipeService.getRecipebyId(id, username, locale);
            return ok(response);

        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid recipe id or access denied: " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error getting recipe", e);
            return internalServerError("Internal server error while getting recipe");
        }
    }

    /**
     * Retrieves a list of recipes, optionally filtered by title or ingredient.
     *
     * @param locale Optional query parameter to specify the locale for the recipe translations.
     * @param title Optional query parameter to filter recipes by title.
     * @param ingredient Optional query parameter to filter recipes by ingredient.
     * @return A Response containing a list of recipe summaries or an error message.
     */
    @GET
    public Response list(
        @QueryParam("locale") String locale,
        @QueryParam("title") String title,
        @QueryParam("ingredient") String ingredient
    ) {
        List<RecipeSummaryResponse> response;
        try {
            if (title != null || ingredient != null) {
                // Search by title or ingredient
                response = null;
            } else {
                response = this.recipeService.getPublicRecipes(locale);
            } 

            return ok(response);

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error listing recipes", e);
            return internalServerError("Internal server error while listing recipes");
        }
    }

    /**
     * Retrieves a list of recipes created by the authenticated user, optionally filtered by locale.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param locale Optional query parameter to specify the locale for the recipe translations.
     * @return A Response containing a list of recipe summaries or an error message.
     */
    @GET
    @Path("/my")
    public Response listMyRecipes(
        @HeaderParam("Authorization") String authHeader,
        @QueryParam("locale") String locale
    ) {
        List<RecipeSummaryResponse> response;
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            response = this.recipeService.getUserRecipes(username, locale);
            return ok(response);

        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid token or user not found: " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error listing user's recipes", e);
            return internalServerError("Internal server error while listing user's recipes");
        }
    }


    //============     UPDATE     ============

    /**
     * Updates an existing recipe (general fields + ingredients).
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param id The ID of the recipe to update.
     * @param request The request object containing the updated recipe details.
     * @return A Response containing the updated recipe details or an error message.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        CreateRecipeRequest request
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = this.recipeService.updateRecipe(id, request, username);
            return ok(response);
        
        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed updating recipe: " + ve.getMessage(), ve);
            return badRequest(ve.getMessage());
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid data or access denied updating recipe: " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error updating recipe", e);
            return internalServerError("Internal server error while updating recipe");
        }
    }

    /**
     * Updates the visibility of a recipe (public/private).
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param id The ID of the recipe to update.
     * @param isPublic The new visibility status of the recipe (true for public, false for private).
     * @return A Response indicating success or containing an error message.
     */
    @PUT
    @Path("/{id}/visibility")
    public Response updateVisibility(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        @QueryParam("public") boolean isPublic
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            this.recipeService.updateVisibility(id, isPublic, username);
            return ok();

        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Access denied updating visibility for recipe: " + id, iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error updating recipe visibility " + id, e);
            return internalServerError("Internal server error while updating recipe visibility");
        }
    }


    //============     DELETE     ============

    /**
     * Deletes an existing recipe.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param id The ID of the recipe to delete.
     * @return A Response indicating success or containing an error message.
     */
    @DELETE
    @Path("/{id}")  
    public Response delete(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            recipeService.deleteRecipe(id, username);
            return ok(id);

        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Access denied deleting recipe: " + id, iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error deleting recipe " + id, e);
            return internalServerError("Internal server error while deleting recipe");
        }
    }

//============     TRANSLATION ENDPOINTS     ============

    //============     CREATE     ============

    /**
     * Adds a new translation to an existing recipe.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param id The ID of the recipe to which the translation will be added.
     * @param request The request object containing the details of the translation to be added.
     * @return A Response indicating success or containing an error message.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/translations")
    public Response addTranslation(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        AddRecipeTranslationRequest request
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = this.recipeService.addRecipeTranslation(id, request, username);
            return ok(response);

        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed adding translation for recipe " + id + ": " + ve.getMessage(), ve);
            return badRequest(ve.getMessage());
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid data or access denied adding translation for recipe " + id + ": " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding translation for recipe " + id, e);
            return internalServerError("Internal server error while adding translation for recipe");
        }
    }


    //============     UPDATE     ============

    /**
    * Updates an existing translation of a recipe.
    *
    * @param authHeader The Authorization header containing the JWT token.
    * @param id The ID of the recipe whose translation will be updated.
    * @param locale The locale of the translation to be updated.
    * @param request The request object containing the updated details of the translation.
    * @return A Response indicating success or containing an error message.
    */
    @PUT
    @Path("/{id}/translation/{locale}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTranslation(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        @PathParam("locale") String locale,
        UpdateRecipeTranslationRequest request
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = this.recipeService.updateRecipeTranslation(id, locale, request, username);
            return ok(response);

        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed updating translation for recipe " + id + ": " + ve.getMessage(), ve);
            return badRequest(ve.getMessage());
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid data or access denied updating translation for recipe " + id + ": " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error updating translation for recipe " + id, e);
            return internalServerError("Internal server error while updating translation for recipe");
        }
    }


    //============     DELETE     ============

    /**
     * Deletes an existing translation of a recipe.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param id The ID of the recipe whose translation will be deleted.
     * @param locale The locale of the translation to be deleted.
     * @return A Response indicating success or containing an error message.
     */
    @DELETE
    @Path("/{id}/translation/{locale}")
    public Response deleteTranslation(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        @PathParam("locale") String locale
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            recipeService.deleteRecipeTranslation(id, locale, username);
            return ok();

        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed deleting translation for recipe " + id + ": " + ve.getMessage(), ve);
            return badRequest(ve.getMessage());
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Access denied deleting translation for recipe " + id + ": " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error deleting translation for recipe " + id, e);
            return internalServerError("Internal server error while deleting translation for recipe");
        }
    }



//============     STEPS ENDPOINTS     ============

    //============     CREATE     ============

    /**
     * Adds a new step to an existing recipe.
     *
     * @param authHeader The Authorization header containing the JWT token.
     * @param id The ID of the recipe to which the step will be added.
     * @param request The request object containing the details of the step to be added.
     * @return A Response indicating success or containing an error message.
     */
    @POST
    @Path("/{id}/steps")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addStep(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        AddRecipeStepRequest request
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = recipeService.addRecipeStep(id, request, username);
            return ok(response);

        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed adding step for recipe " + id + ": " + ve.getMessage(), ve);
            return badRequest(ve.getMessage());
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid data or access denied adding step for recipe " + id + ": " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding step for recipe " + id, e);
            return internalServerError("Internal server error while adding step for recipe");
        }
    }


    //============     UPDATE     ============

    @PUT
    @Path("/{id}/steps/{stepId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateStep(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        @PathParam("stepId") long stepId,
        @QueryParam("locale") String locale,
        UpdateRecipeStepRequest request
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = recipeService.updateRecipeStep(id, stepId, request, username, locale);
            return ok(response);

        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed updating step " + stepId + " for recipe " + id + ": " + ve.getMessage(), ve);
            return badRequest(ve.getMessage());
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid data or access denied updating step " + stepId + " for recipe " + id + ": " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error updating step " + stepId + " for recipe " + id, e);
            return internalServerError("Internal server error while updating step for recipe");
        }
    }


    //============     DELETE     ============

    @DELETE
    @Path("/{id}/steps/{stepId}")
    public Response deleteStep(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        @PathParam("stepId") long stepId,
        @QueryParam("locale") String locale
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = recipeService.deleteRecipeStep(id, stepId, username, locale);
            return ok(response);

        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Access denied deleting step " + stepId + " for recipe " + id + ": " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error deleting step " + stepId + " for recipe " + id, e);
            return internalServerError("Internal server error while deleting step for recipe");
        }
    }


//============     STEP TRANSLATIONS ENDPOINTS     ============

    //============     UPDATE     ============

    @PUT
    @Path("/{id}/steps/{stepId}/translation/{locale}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateStepTranslation(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id,
        @PathParam("stepId") long stepId,
        @PathParam("locale") String locale,
        UpdateRecipeStepTranslationRequest request
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = recipeService.updateRecipeStepTranslation(id, stepId, locale, request, username);
            return ok(response);

        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed updating translation for step " + stepId + " of recipe " + id + ": " + ve.getMessage(), ve);
            return badRequest(ve.getMessage());
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid data or access denied updating translation for step " + stepId + " of recipe " + id + ": " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error updating translation for step " + stepId + " of recipe " + id, e);
            return internalServerError("Internal server error while updating translation for step");
        }
    }

//============     HELPER METHODS     ============

    private String obtainUsernameFromAuthHeader(String authHeader) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        }
        return jwtUtil.getUsernameFromToken(token);
    }

}
