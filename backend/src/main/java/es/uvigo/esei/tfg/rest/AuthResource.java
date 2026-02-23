package es.uvigo.esei.tfg.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import es.uvigo.esei.tfg.dto.auth.LoginRequest;
import es.uvigo.esei.tfg.dto.ErrorResponse;
import es.uvigo.esei.tfg.dto.TokenResponse;
import es.uvigo.esei.tfg.dto.auth.AuthResponse;
import es.uvigo.esei.tfg.dto.auth.RegisterRequest;
import es.uvigo.esei.tfg.services.AuthService;
import es.uvigo.esei.tfg.exceptions.AccountBlockedException;
import es.uvigo.esei.tfg.exceptions.AuthenticationException;
import es.uvigo.esei.tfg.exceptions.ValidationException;
import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.security.Secured;

/**
 * REST resource for authentication.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    private final static Logger LOG = Logger.getLogger(AuthResource.class.getName());

    private final AuthService authService;

    public AuthResource() {
        this.authService = new AuthService();
    }

    @GET
    @Secured
    @Path("/securedPing")
    @Produces(MediaType.APPLICATION_JSON)
    public Response securedPing() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "¡Autenticación correcta!");
        return Response.ok(response).build();
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
            return Response.ok(registerResponse).build();
        
        } catch (ValidationException ve) {
            LOG.log(Level.FINE, "Validation failed for registration request: " + request.getUsername(), ve);
            
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ve.getError())
                .build();
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error during registration: ", e);
            return this.internalServerError("Internal server error during registration");
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
            return Response.ok(loginResponse).build();
        
        } catch (AuthenticationException ae) {
            LOG.log(Level.FINE, "Authentication failed for user: " + request.getUsername(), ae);

            return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(ae.getError())
                .build();

        } catch (AccountBlockedException abe) {
            LOG.log(Level.WARNING, "Blocked account login attempt for user: " + request.getUsername(), abe);

            return Response
                .status(Response.Status.FORBIDDEN)
                .entity(abe.getError())
                .build();

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error during login: ", e);
            return this.internalServerError("Internal server error during login");
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
            return Response.ok().build();

        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid token provided for logout", iae);

            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(iae.getMessage())
                .build();

        } catch (DAOException e) {            
            LOG.log(Level.SEVERE, "Error during logout: ", e);
            return this.internalServerError("Internal server error during logout");
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
            return Response.ok(tokenResponse).build();             
        
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid token provided for refresh", iae);

            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(iae.getMessage())
                .build();

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error during token refresh: ", e);
            return this.internalServerError("Internal server error during token refresh");
        }
    }

    /**
     * Helper method to create a standardized internal server error response with a custom message.
     * @param message The error message to include in the response
     * @return A Response object with status 500 and a JSON body containing the error details
     */
    private Response internalServerError(String message) {
        ErrorResponse error = new ErrorResponse(
            "SYS_001",
            message
        );

        return Response
            .serverError()
            .entity(error)
            .build();
    }

}


