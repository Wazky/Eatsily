package es.uvigo.esei.tfg.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

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

import es.uvigo.esei.tfg.dao.DAOException;
import es.uvigo.esei.tfg.entities.Pet;
import es.uvigo.esei.tfg.entities.Species;
import es.uvigo.esei.tfg.services.PetService;

@Path("/pets")
@Produces(MediaType.APPLICATION_JSON)
public class PetResource {  
    private final static Logger LOG = Logger.getLogger(PetResource.class.getName());
    
    private final PetService service;

    /**
     * Constructs a new instance of {@link PetResource}.
     */
    public PetResource() {
        this(new PetService());
    }

    // Needed for testing purposes
    PetResource(PetService service) {
        this.service = service;
    }

    /**
     * Returns a pet with the provided identifier.
     * 
     * @param id the identifier of the pet to retrieve.
     * @return a 200 OK response with a pet that has the provided identifier.
     * If the identifier does not corresponds with any pet, a 400 Bad Request
     * response with an error message will be returned. If an error happens
     * while retrieving the list, a 500 Internal Server Error response with an
     * error message will be returned.
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") int id) {
        try {
            final Pet pet = this.service.getPet(id);

            return Response.ok(pet).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid pet id in get method", iae);

            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error getting a pet", e);

            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Returns a complete list of pets sotred in the system.
     * 
     * @return a 200 OK response with the complete list of pets stored in the system.
     * If an error happens while retrieving the list, a 500 Internal Server Error
     * response with an error message will be returned.
     */
    @GET
    public Response list() {
        try {
            return Response.ok(this.service.listPets()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error listing pets", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    //LIST DE UN OWNER
    @GET
    @Path("/owner/{owner_id}")
    public Response listByOwner(@PathParam("owner_id") int  owner_id) {
        try {
            return Response.ok(this.service.listByOwner(owner_id)).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid owner id in list method", iae);

            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error listing pets", e);

            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    /**
     * Creates a new pet in the system.
     * 
     * @param name the new of the new pet
     * @param specie the specie of the new pet
     * @param breed the breed of the new pet
     * @param owner_id the owner of the new pet
     * @return a 200 OK response with a pet that has been created. If the
     * name or specie are not provided or the owner id is invalid, a 400 Bad Request
     * response with an error message will be returned. If an error happens
     * while retrieving the list, a 500 Internal Server Error response with an
     * error message will be returned.
     */
    @POST
    public Response add (
        @FormParam("name") String name,
        @FormParam("specie") Species specie, 
        @FormParam("breed") String breed, 
        @FormParam("owner_id") int owner_id 
    ) {
        try {
            final Pet newPet = this.service.addPet(name, specie, breed, owner_id);

            return Response.ok(newPet).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid pet attributes in add method", iae);

            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error adding a pet", e);

            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Modifies a pet in the system.
     * 
     * @param id the identifier of the pet to modify
     * @param name the new name of the pet
     * @param specie the new specie of the pet
     * @param breed the new breed of the pet
     * @param owner_id the new owner of the pet
     * @return a 200 OK response with a pet that has been modified. If the
     * identifier does not corresponds with any pet, a 400 Bad Request response
     */
    @PUT
    @Path("/{id}")
    public Response modify(
        @PathParam("id") int id,
        @FormParam("name") String name,
        @FormParam("specie") Species specie,
        @FormParam("breed") String breed,
        @FormParam("owner_id") int owner_id
    ) {
        try{
            Pet modifiedPet = this.service.updatePet(id, name, specie, breed, owner_id);

            return Response.ok(modifiedPet).build();
        } catch (NullPointerException npe) {
            final String message = String.format("Invalid data for pet (name: %s, specie: %s, breed: %s, owner_id: %d)",name, specie, breed, owner_id);
        
            LOG.log(Level.FINE, message);
            
            return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, iae.getMessage() + " in modify method", iae);

            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();   
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error modifying a pet", e);

            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")  
    public Response delete( @PathParam("id") int id) {
        try{
            this.service.deletePet(id);

            return Response.ok(id).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid pet id in delete method", iae);

            return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error deleting a pet", e);

            return Response.serverError().entity(e.getMessage()).build();
        }
    }
    



}
