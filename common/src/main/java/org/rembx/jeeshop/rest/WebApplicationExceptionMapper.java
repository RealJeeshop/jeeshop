package org.rembx.jeeshop.rest;


import javax.ws.rs.core.Response;

/**
 * Avoid logging of stack trace for WebApplicationException instances thrown by application
 */
public class WebApplicationExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException e) {
        return e.getResponse();
    }
}