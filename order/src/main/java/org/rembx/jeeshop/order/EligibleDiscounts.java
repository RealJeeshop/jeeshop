package org.rembx.jeeshop.order;

import io.quarkus.undertow.runtime.HttpSessionContext;
import org.rembx.jeeshop.catalog.DiscountFinder;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.UserFinder;
import org.rembx.jeeshop.user.model.User;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Orders resource.
 */
@Path("discounts/eligible")
@Transactional
@RequestScoped
public class EligibleDiscounts {

    private DiscountFinder discountFinder;
    private UserFinder userFinder;
    private OrderFinder orderFinder;

    @Resource
    private HttpSessionContext sessionContext;

    private OrderConfiguration orderConfiguration;

    public EligibleDiscounts() {
    }

    EligibleDiscounts(UserFinder userFinder, DiscountFinder discountFinder, OrderFinder orderFinder,
                             HttpSessionContext sessionContext) {
        this.userFinder = userFinder;
        this.discountFinder = discountFinder;
        this.orderFinder = orderFinder;
        this.sessionContext = sessionContext;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.USER)
    public List<Discount> findEligible(@QueryParam("locale") String locale) {

        //User currentUser = userFinder.findByLogin(sessionContext.getCallerPrincipal().getName());
        User currentUser = userFinder.findByLogin("admin@jeeshop.org");

        Long completedOrders = orderFinder.countUserCompletedOrders(currentUser);

        return discountFinder.findEligibleOrderDiscounts(locale, completedOrders );

    }


}
