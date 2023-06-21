package org.rembx.jeeshop.order;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/fees")
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
