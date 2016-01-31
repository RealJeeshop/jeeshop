package org.rembx.jeeshop.rest;


import javax.ejb.EJBAccessException;
import javax.ws.rs.core.Response;

/**
 * EJB access exception mapping to send back proper HTTP code to client
 */
public class EJBAccessExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<EJBAccessException> {
    @Override
    public Response toResponse(EJBAccessException e) {
        return Response.status(Response.Status.FORBIDDEN).build();
    }
}