package admin.org.rembx.jeeshop.admin;

import org.rembx.jeeshop.catalog.*;
import org.rembx.jeeshop.media.Medias;
import org.rembx.jeeshop.order.Orders;
import org.rembx.jeeshop.rest.WebApplicationExceptionMapper;
import org.rembx.jeeshop.user.MailTemplates;
import org.rembx.jeeshop.user.Users;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class
ApplicationConfig extends Application {

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
        classes.add(Users.class);
        classes.add(Medias.class);
        classes.add(MailTemplates.class);
        classes.add(Orders.class);
        classes.add(WebApplicationExceptionMapper.class);
        return classes;
    }
}
