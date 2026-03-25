package es.uvigo.esei.tfg.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.tfg.dto.auth.LoginRequest;
import es.uvigo.esei.tfg.dto.ErrorResponse;
import es.uvigo.esei.tfg.dto.TokenResponse;
import es.uvigo.esei.tfg.dto.auth.AuthResponse;
import es.uvigo.esei.tfg.dto.auth.RegisterRequest;
import es.uvigo.esei.tfg.exceptions.AccountBlockedException;
import es.uvigo.esei.tfg.exceptions.AuthenticationException;
import es.uvigo.esei.tfg.exceptions.ValidationException;
import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.security.Secured;
import es.uvigo.esei.tfg.services.user.AuthService;

/**
 * REST resource for authentication.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource extends BaseResource {
    private final static Logger LOG = Logger.getLogger(AuthResource.class.getName());

    private final AuthService authService;

    public AuthResource() {
        this(new AuthService());
    }

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(
        RegisterRequest request
    ) {
        try {
            AuthResponse registerResponse = this.authService.register(request);
            return ok(registerResponse);
        
        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed for registration request: " + request.getUsername(), ve);
            if (ve.getError() != null) {
                return badRequest(ve.getError());
            }
            return badRequest(ve.getMessage());
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error during registration: ", e);
            return internalServerError("Internal server error during registration");
        }
    }    

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
        LoginRequest request
    ) {
        try {
            final AuthResponse loginResponse = this.authService.login(request);
            return ok(loginResponse);
        
        } catch (AuthenticationException ae) {
            LOG.log(Level.FINE, "Authentication failed for user: " + request.getUsername(), ae);
            if (ae.getError() != null) {
                return unauthorized(ae.getError());
            }
            return unauthorized(ae.getMessage());

        } catch (AccountBlockedException abe) {
            LOG.log(Level.WARNING, "Blocked account login attempt for user: " + request.getUsername(), abe);
            if (abe.getError() != null) {
                return unauthorized(abe.getError());
            }
            return forbidden(abe.getMessage());

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error during login: ", e);
            return internalServerError("Internal server error during login");
        }
    }

    @POST
    @Secured
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(
        @HeaderParam("Authorization") String authHeader
    ) {
        try {
            this.authService.logout(authHeader);
            return ok();

        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid token provided for logout", iae);
            return badRequest(iae.getMessage());

        } catch (DAOException e) {            
            LOG.log(Level.SEVERE, "Error during logout: ", e);
            return internalServerError("Internal server error during logout");
        }
        
    }

    @POST
    @Secured
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken(
        @HeaderParam("Authorization") String authHeader
    ) { 
        try {
            TokenResponse tokenResponse = this.authService.refreshToken(authHeader);
            return ok(tokenResponse);             
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid token provided for refresh", iae);
            return badRequest(iae.getMessage());

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error during token refresh: ", e);
            return internalServerError("Internal server error during token refresh");
        }
    }

}


