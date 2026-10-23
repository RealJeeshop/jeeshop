package org.rembx.jeeshop.rest;


import jakarta.ws.rs.core.Response;

// FIXME ? @ApplicationException
public class WebApplicationException extends jakarta.ws.rs.WebApplicationException{
    public WebApplicationException(Response.Status status) {
        super(status);
    }
}
