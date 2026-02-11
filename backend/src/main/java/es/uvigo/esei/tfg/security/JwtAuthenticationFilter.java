package es.uvigo.esei.tfg.security;

import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import es.uvigo.esei.tfg.util.JwtUtil;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    private static final String REALM = "Eatsily";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter() {
        this.jwtUtil = new JwtUtil();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        LOG.info("JWT Filter executing for path: " + requestContext.getUriInfo().getPath());

        // Get the Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            LOG.warning("Missing or invalid Authorization header");
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            // Validate the token
            if (!jwtUtil.validateToken(token)) {
                LOG.warning("Invalid JWT token or token expired");
                abortWithUnauthorized(requestContext, "Invalid or expired JWT token");
                return;
            }

        } catch (Exception e) {
            LOG.warning("Invalid JWT token: " + e.getMessage());
            abortWithUnauthorized(requestContext, "Token validation error: " + e.getMessage());
        }
    }

    /**
     * Checks if the Authorization header is valid 
     * and starts with the expected scheme.
     * 
     * @param authorizationHeader the value of the Authorization header to check
     * @return true if the header is valid and starts with the expected scheme, false otherwise
     */
    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        if (authorizationHeader == null) {
            return false;
        }

        return authorizationHeader
            .toLowerCase()
            .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        LOG.warning("Unauthorized access attempt: " + message);

        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                .entity("{\"error\": \"" + message + "\"}")
                .build()
        );
    }
    
}
