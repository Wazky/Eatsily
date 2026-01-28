package es.uvigo.esei.tfg.rest;

import javax.ws.rs.container.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Provider
@PreMatching
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {
    
    // List of allowed origins
    private final Set<String> allowed_origins = new HashSet<>();    // Set allows efficient lookup

    // Constructor to initialize allowed origins
    public CORSFilter() {

        // Development origins (do not use in production)
        allowed_origins.add("http://localhost:3000");
        allowed_origins.add("http://localhost:5173");

        // Production origins from environment variable (if any)
        String prodOrigins = System.getenv("ALLOWED_ORIGINS");
        if (prodOrigins != null && !prodOrigins.isEmpty()) {
            String[] origins = prodOrigins.split(",");
            for (String origin : origins) {
                allowed_origins.add(origin.trim());
            }
        }
    }

    /**
     * Checks if the given origin is allowed.
     * 
     * @param origin the origin to check
     * @return true if the origin is allowed, false otherwise
     */
    private boolean isAllowedOrigin(String origin) {
        return origin != null && allowed_origins.contains(origin);
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        if (request.getMethod().equals("OPTIONS")) {
            String origin = request.getHeaderString("Origin");

            // Allowed origin
            if (isAllowedOrigin(origin)) {

                Response.ResponseBuilder responseBuilder = Response.ok();
                responseBuilder.header("Access-Control-Allow-Origin", origin);
                responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                responseBuilder.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
                responseBuilder.header("Access-Control-Allow-Credentials", "true");

                request.abortWith(responseBuilder.build());
            
            // Not allowed origin
            } else {
                request.abortWith(Response.status(Response.Status.FORBIDDEN).build());
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        String origin = request.getHeaderString("Origin");

        if (isAllowedOrigin(origin)) {    
            response.getHeaders().add("Access-Control-Allow-Origin", origin);
            response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        }
    }
}
