package es.uvigo.esei.tfg.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.tfg.dto.UserProfileResponse;
import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.security.Secured;
import es.uvigo.esei.tfg.services.user.UserPersonService;

/**
 * REST resource for managing users.
 * 
 * @author DRM
 */
@Secured
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource extends BaseResource{
	private final static Logger LOG = Logger.getLogger(UsersResource.class.getName());
	
	private final UserPersonService userPersonService;
	
	public UsersResource() {
		this(new UserPersonService());
	}

	public UsersResource(UserPersonService userPersonService) {
		this.userPersonService = userPersonService;
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserById(
		@PathParam("id") long id
	) {
		try {
			UserProfileResponse userProfileResponse = this.userPersonService.getById(id);
			return ok(userProfileResponse);

		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid user ID provided", iae);
			return badRequest(iae.getMessage());

		} catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error during user retrieval: ", e);
            return internalServerError("Internal server error during user retrieval");			
		}
	}

	@GET
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserProfile(
		@HeaderParam("Authorization") String authHeader
	) {
		try {
			UserProfileResponse userProfileResponse = this.userPersonService.getProfile(authHeader);
			return ok(userProfileResponse);

		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid Authorization header provided", iae);
			return badRequest(iae.getMessage());

		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error during user profile retrieval: ", e);
			return internalServerError("Internal server error during user profile retrieval");			
		}
	}

}
