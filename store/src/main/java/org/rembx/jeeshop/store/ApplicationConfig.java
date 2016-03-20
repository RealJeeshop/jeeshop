package org.rembx.jeeshop.store;

import org.rembx.jeeshop.catalog.*;
import org.rembx.jeeshop.order.EligibleDiscounts;
import org.rembx.jeeshop.order.Fees;
import org.rembx.jeeshop.order.Orders;
import org.rembx.jeeshop.rest.WebApplicationExceptionMapper;
import org.rembx.jeeshop.user.Users;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rs")
public class ApplicationConfig extends Application {

    // ======================================
    // =          Business methods          =
    // ======================================

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(Catalogs.class);
        classes.add(Categories.class);
        classes.add(Products.class);
        classes.add(SKUs.class);
        classes.add(Discounts.class);
        classes.add(EligibleDiscounts.class);
        classes.add(Users.class);
        classes.add(Orders.class);
        classes.add(Fees.class);
        //classes.add(SIPSAutoResponse.class);
        classes.add(WebApplicationExceptionMapper.class);
        return classes;
    }
}
