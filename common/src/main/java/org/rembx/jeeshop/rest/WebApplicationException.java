package org.rembx.jeeshop.rest;


import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response;

@ApplicationException
public class WebApplicationException extends javax.ws.rs.WebApplicationException{
    public WebApplicationException(Response.Status status) {
        super(status);
    }
}
