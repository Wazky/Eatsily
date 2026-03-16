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
import es.uvigo.esei.tfg.dto.recipe.requests.CreateRecipeRequest;
import es.uvigo.esei.tfg.dto.recipe.responses.RecipeDetailResponse;
import es.uvigo.esei.tfg.dto.recipe.responses.RecipeSummaryResponse;
import es.uvigo.esei.tfg.entities.recipe.Recipe;

/**
 * REST resource for managing recipes.
 */
@Secured
@Path("/recipes")
@Produces(MediaType.APPLICATION_JSON)
public class RecipeResource extends BaseResource {
	private final static Logger LOG = Logger.getLogger(RecipeResource.class.getName());
	
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

    @GET
    @Path("/{id}")
    public Response get(
        @HeaderParam("Authorization") String authHeader,
        @PathParam("id") long id
    ) {
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            RecipeDetailResponse response = this.recipeService.getRecipebyId(id, username);
            return ok(response);

        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid recipe id or access denied: " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error getting recipe", e);
            return internalServerError("Internal server error while getting recipe");
        }
    }

    @GET
    public Response List(
        @QueryParam("title") String title,
        @QueryParam("ingredient") String ingredient
    ) {
        List<RecipeSummaryResponse> response;
        try {
            if (title != null || ingredient != null) {
                // Search by title or ingredient
                response = null;
            } else {
                response = this.recipeService.getPublicRecipes();
            } 

            return ok(response);

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error listing recipes", e);
            return internalServerError("Internal server error while listing recipes");
        }
    }

    @GET
    @Path("/my")
    public Response listMyRecipes(
        @HeaderParam("Authorization") String authHeader
    ) {
        List<RecipeSummaryResponse> response;
        try {
            String username = obtainUsernameFromAuthHeader(authHeader);
            response = this.recipeService.getUserRecipes(username);
            return ok(response);

        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid token or user not found: " + iae.getMessage(), iae);
            return badRequest(iae.getMessage());

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error listing user's recipes", e);
            return internalServerError("Internal server error while listing user's recipes");
        }
    }

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

    private String obtainUsernameFromAuthHeader(String authHeader) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        }
        return jwtUtil.getUsernameFromToken(token);
    }

}
