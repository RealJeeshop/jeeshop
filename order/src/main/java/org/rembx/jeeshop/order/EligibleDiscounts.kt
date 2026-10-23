package org.rembx.jeeshop.order;

import io.quarkus.undertow.runtime.HttpSessionContext;
import org.rembx.jeeshop.catalog.DiscountFinder;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.UserFinder;
import org.rembx.jeeshop.user.model.User;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * Orders resource.
 */
@Path("discounts/eligible")
@ApplicationScoped
public class EligibleDiscounts {

    private DiscountFinder discountFinder;
    private UserFinder userFinder;
    private OrderFinder orderFinder;

    private OrderConfiguration orderConfiguration;

    EligibleDiscounts(UserFinder userFinder, DiscountFinder discountFinder, OrderFinder orderFinder) {
        this.userFinder = userFinder;
        this.discountFinder = discountFinder;
        this.orderFinder = orderFinder;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.USER)
    public List<Discount> findEligible(@Context SecurityContext securityContext, @QueryParam("locale") String locale) {

        User currentUser = userFinder.findByLogin(securityContext.getUserPrincipal().getName());

        Long completedOrders = orderFinder.countUserCompletedOrders(currentUser);

        return discountFinder.findEligibleOrderDiscounts(locale, completedOrders );

    }


}
