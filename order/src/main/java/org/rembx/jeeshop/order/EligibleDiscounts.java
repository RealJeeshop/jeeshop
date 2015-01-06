package org.rembx.jeeshop.order;

import org.rembx.jeeshop.catalog.DiscountFinder;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.UserFinder;
import org.rembx.jeeshop.user.model.User;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
@Stateless
public class EligibleDiscounts {

    @Inject
    private DiscountFinder discountFinder;

    @Inject
    private UserFinder userFinder;

    @Inject
    private OrderFinder orderFinder;

    @Resource
    private SessionContext sessionContext;

    @Inject
    private OrderConfiguration orderConfiguration;

    public EligibleDiscounts() {
    }

    public EligibleDiscounts(UserFinder userFinder, DiscountFinder discountFinder, OrderFinder orderFinder,
                             SessionContext sessionContext) {
        this.userFinder = userFinder;
        this.discountFinder = discountFinder;
        this.orderFinder = orderFinder;
        this.sessionContext = sessionContext;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.USER)
    public List<Discount> findEligible(@QueryParam("locale") String locale) {

        User currentUser = userFinder.findByLogin(sessionContext.getCallerPrincipal().getName());

        Long completedOrders = orderFinder.countUserCompletedOrders(currentUser);

        return discountFinder.findEligibleOrderDiscounts(locale, completedOrders );

    }


}
