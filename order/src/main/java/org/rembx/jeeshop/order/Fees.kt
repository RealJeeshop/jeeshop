package org.rembx.jeeshop.order

import jakarta.annotation.security.PermitAll
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/fees")
@ApplicationScoped
class Fees(private val orderConfiguration: OrderConfiguration?) {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/shipping")
    @PermitAll
    fun getShippingFee(): Double? = orderConfiguration?.getFixedDeliveryFee()

    @GET
    @Path("/vat")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    fun getVAT(): Double? = orderConfiguration?.getVAT()
}
