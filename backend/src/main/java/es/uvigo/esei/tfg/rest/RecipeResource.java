package es.uvigo.esei.tfg.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Generated;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.dao.RecipeDAO;
import es.uvigo.esei.tfg.entities.Recipe;

/**
 * REST resource for managing recipes.
 */
@Path("/recipes")
@Produces(MediaType.APPLICATION_JSON)
public class RecipeResource {
    
	private final static Logger LOG = Logger.getLogger(RecipeResource.class.getName());
	
	private final RecipeDAO dao;

    /**
     * Constructs a new instance of {@link RecipeResource}.
     */
    public RecipeResource() {
        this(new RecipeDAO());
    }

    RecipeResource(RecipeDAO dao) {
        this.dao = dao;
    }

    @GET
    @Path("/{id}")
    public Response get(
        @PathParam("id") int id
    ) {
        try {
            return Response.ok(this.dao.get(id)).build();

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error retrieving a recipe", e);
            return Response.serverError().entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response List() {
        try {
            return Response.ok(this.dao.list()).build();

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error retrieving recipes", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    public Response add(
        @FormParam("name") String name,
        @FormParam("description") String description,
        @FormParam("preparationTime") int preparationTime,
        @FormParam("cookingTime") int cookingTime,
        @FormParam("servings") int servings
    ) {
        try {
            final Recipe recipe = this.dao.add(name, description, preparationTime, cookingTime, servings);
            return Response.ok(recipe).build();

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding a recipe", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(
        @PathParam("id") int id,
        @FormParam("name") String name,
        @FormParam("description") String description,
        @FormParam("preparationTime") int preparationTime,
        @FormParam("cookingTime") int cookingTime,
        @FormParam("servings") int servings
    ) {
        try {
            final Recipe updatedRecipe = new Recipe(id, name, description, preparationTime, cookingTime, servings);
            this.dao.update(updatedRecipe);
                        
            return Response.ok(updatedRecipe).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error updating a recipe", e);
            return Response.serverError().entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(
        @PathParam("id") int id
    ) {
        try {
            this.dao.delete(id);
            return Response.ok().build();

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error deleting a recipe", e);
            return Response.serverError().entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}
