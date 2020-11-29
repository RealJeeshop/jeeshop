package org.rembx.jeeshop.rest;


import javax.ws.rs.core.Response;

// FIXME ? @ApplicationException
public class WebApplicationException extends javax.ws.rs.WebApplicationException{
    public WebApplicationException(Response.Status status) {
        super(status);
    }
}
