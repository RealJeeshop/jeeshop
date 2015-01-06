package org.rembx.jeeshop.order;

import org.rembx.jeeshop.role.JeeshopRoles;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Orders resource.
 */
@Path("fees")
@Stateless
public class Fees {

    @Inject
    private OrderConfiguration orderConfiguration;

    public Fees() {
    }

    public Fees(OrderConfiguration orderConfiguration) {
        this.orderConfiguration = orderConfiguration;
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/shipping")
    @RolesAllowed({JeeshopRoles.USER, JeeshopRoles.ADMIN})
    public Double getShippingFee() {
        if (orderConfiguration!=null){
            return orderConfiguration.getFixedDeliveryFee();
        }
        return null;
    }


    @GET
    @Path("/vat")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({JeeshopRoles.USER, JeeshopRoles.ADMIN})
    public Double getVAT() {
        if (orderConfiguration!=null){
            return orderConfiguration.getVAT();
        }
        return null;
    }


}
