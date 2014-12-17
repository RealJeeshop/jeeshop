package org.rembx.jeeshop.order;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.collections.CollectionUtils;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.MailTemplateFinder;
import org.rembx.jeeshop.user.UserFinder;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringWriter;
import java.util.List;

import static org.rembx.jeeshop.order.mail.Mails.OrderConfirmation;

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
