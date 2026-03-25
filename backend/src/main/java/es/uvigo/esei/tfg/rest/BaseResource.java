package es.uvigo.esei.tfg.rest;

import java.util.Map;

import javax.ws.rs.core.Response;

import es.uvigo.esei.tfg.dto.ErrorResponse;

public abstract class BaseResource {

    protected Response ok(Object entity) {
        return Response.ok(entity).build();
    }

    protected Response ok() {
        return Response.ok().build();
    }

    protected Response error(Response.Status status, ErrorResponse error) {
        return Response
            .status(status)
            .entity(error)
            .build();
    }

    protected Response error(Response.Status status, String code, String message, Map<String, Object> details) {
        return Response
            .status(status)
            .entity(new ErrorResponse(code, message, details))
            .build();
    }

    protected Response error(Response.Status status, String code, String message) {
        return Response
            .status(status)
            .entity(new ErrorResponse(code, message))
            .build();
    }

    // BAD REQUEST RESPONSES
    
    protected Response badRequest(ErrorResponse error) {
        return error(Response.Status.BAD_REQUEST, error);
    }

    protected Response badRequest(String code, String message, Map<String, Object> details) {
        return error(Response.Status.BAD_REQUEST, code, message, details);
    }

    protected Response badRequest(String code, String message) {
        return error(Response.Status.BAD_REQUEST, code, message);
    }

    protected Response badRequest(String message) {
        return badRequest("BAD_REQ_001", message);
    }

    protected Response badRequest() {
        return badRequest("Bad request");
    }

    // NOT FOUND RESPONSES

    protected Response notFound(ErrorResponse error) {
        return error(Response.Status.NOT_FOUND, error);
    }

    protected Response notFound(String code, String message, Map<String, Object> details) {
        return error(Response.Status.NOT_FOUND, code, message, details);
    }

    protected Response notFound(String code, String message) {
        return error(Response.Status.NOT_FOUND, code, message);
    }

    protected Response notFound(String message) {
        return notFound("NOT_FOUND_001", message);
    }

    protected Response notFound() {
        return notFound("Not found");
    }

    // UNAUTHORIZED RESPONSES

    protected Response unauthorized(ErrorResponse error) {
        return error(Response.Status.UNAUTHORIZED, error);
    }

    protected Response unauthorized(String code, String message, Map<String, Object> details) {
        return error(Response.Status.UNAUTHORIZED, code, message, details);
    }

    protected Response unauthorized(String code, String message) {
        return error(Response.Status.UNAUTHORIZED, code, message);
    }

    protected Response unauthorized(String message) {
        return unauthorized("UNAUTH_001", message);
    }

    protected Response unauthorized() {
        return unauthorized("Unauthorized");
    }

    // FORBIDDEN RESPONSES

    protected Response forbidden(String code, String message, Map<String, Object> details) {
        return error(Response.Status.FORBIDDEN, code, message, details);
    }

    protected Response forbidden(String code, String message) {
        return error(Response.Status.FORBIDDEN, code, message);
    }

    protected Response forbidden(String message) {
        return forbidden("FORB_001", message);
    }

    protected Response forbidden() {
        return forbidden("Forbidden");
    }

    // INTERNAL SERVER ERROR RESPONSES

    protected Response internalServerError(String code, String message, Map<String, Object> details) {
        return error(Response.Status.INTERNAL_SERVER_ERROR, code, message, details);
    }

    protected Response internalServerError(String code, String message) {
        return error(Response.Status.INTERNAL_SERVER_ERROR, code, message);
    }

    protected Response internalServerError(String message) {
        return internalServerError("SYS_001", message);
    }

    protected Response internalServerError() {
        return internalServerError("Internal server error");
    }

}
