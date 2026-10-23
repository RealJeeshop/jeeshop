package org.rembx.jeeshop.order;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/rs/fees")
@ApplicationScoped
public class Fees {

    private OrderConfiguration orderConfiguration;

    Fees(OrderConfiguration orderConfiguration) {
        this.orderConfiguration = orderConfiguration;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/shipping")
    @PermitAll
    public Double getShippingFee() {
        if (orderConfiguration!=null){
            return orderConfiguration.getFixedDeliveryFee();
        }
        return null;
    }


    @GET
    @Path("/vat")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Double getVAT() {
        if (orderConfiguration!=null){
            return orderConfiguration.getVAT();
        }
        return null;
    }


}
