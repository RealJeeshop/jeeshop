package org.rembx.jeeshop.order;

import io.quarkus.hibernate.orm.PersistenceUnit;
import io.quarkus.undertow.runtime.HttpSessionContext;
import org.apache.commons.collections4.CollectionUtils;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.user.MailTemplateFinder;
import org.rembx.jeeshop.user.UserFinder;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

import static org.rembx.jeeshop.role.JeeshopRoles.*;

/**
 * Orders resource.
 */
@Path("/rs/orders")
@ApplicationScoped
public class Orders {

    private OrderConfiguration orderConfiguration;
    private EntityManager entityManager;
    private OrderFinder orderFinder;
    private UserFinder userFinder;
    private Mailer mailer;
    private MailTemplateFinder mailTemplateFinder;
    private PaymentTransactionEngine paymentTransactionEngine;
    private PriceEngine priceEngine;

    Orders(@PersistenceUnit(UserPersistenceUnit.NAME) EntityManager entityManager, OrderFinder orderFinder, UserFinder userFinder,
                  MailTemplateFinder mailTemplateFinder, Mailer mailer, PriceEngine priceEngine, PaymentTransactionEngine paymentTransactionEngine) {
        this.entityManager = entityManager;
        this.orderFinder = orderFinder;
        this.userFinder = userFinder;
        this.mailTemplateFinder = mailTemplateFinder;
        this.mailer = mailer;
        this.priceEngine = priceEngine;
        this.paymentTransactionEngine = paymentTransactionEngine;
    }


    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY, USER})
    public Order find(@Context SecurityContext securityContext, @PathParam("orderId") @NotNull Long orderId, @QueryParam("enhanced") Boolean enhanced) {
        Order order = entityManager.find(Order.class, orderId);
        if (securityContext.isUserInRole(USER) && !securityContext.isUserInRole(ADMIN)) {
            User authenticatedUser = userFinder.findByLogin(securityContext.getUserPrincipal().getName());
            if (!order.getUser().equals(authenticatedUser)) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        }

        if (enhanced != null && enhanced) {
            orderFinder.enhanceOrder(order);
        }
        checkNotNull(order);

        return order;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY, USER})
    public List<Order> findAll(@Context SecurityContext securityContext, @QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size,
                               @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc, @QueryParam("status") OrderStatus status,
                               @QueryParam("skuId") Long skuId, @QueryParam("enhanced") Boolean enhanced) {

        if (securityContext.isUserInRole(USER) && !securityContext.isUserInRole(ADMIN)) {
            User authenticatedUser = userFinder.findByLogin(securityContext.getUserPrincipal().getName());
            return orderFinder.findByUser(authenticatedUser, start, size, orderBy, isDesc, status);
        } else {
            return orderFinder.findAll(start, size, orderBy, isDesc, search, status, skuId, enhanced != null ? enhanced : false);
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({USER, ADMIN})
    public Order create(@Context SecurityContext securityContext, Order order, @QueryParam("userLogin") String userLogin) {

        checkOrder(order);

        assignOrderToUser(securityContext, order, userLogin);

        order.setStatus(OrderStatus.CREATED);

        priceEngine.computePrice(order);

        assignOrderToOrderItems(order);

        entityManager.persist(order);

        paymentTransactionEngine.processPayment(order);

        return order;
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Order modify(@NotNull Order order) {
        Order existingOrder = entityManager.find(Order.class, order.getId());
        checkNotNull(existingOrder);

        order.setUser(existingOrder.getUser());

        order.getItems().forEach(orderItem -> orderItem.setOrder(order));

        return entityManager.merge(order);
    }


    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/{orderId}")
    public void delete(@PathParam("orderId") Long orderId) {
        Order order = entityManager.find(Order.class, orderId);
        checkNotNull(order);
        entityManager.remove(order);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count(@QueryParam("search") String search, @QueryParam("status") OrderStatus status, @QueryParam("skuId") Long skuId) {
        return orderFinder.countAll(search, status, skuId);
    }

    @GET
    @Path("/fixeddeliveryfee")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Double getFixedDeliveryFee() {
        if (orderConfiguration != null) {
            return orderConfiguration.getFixedDeliveryFee();
        }
        return null;
    }


    private void assignOrderToUser(SecurityContext securityContext, Order order, String userLogin) {
        User user;
        if (securityContext.isUserInRole(USER)) {
            user = userFinder.findByLogin(securityContext.getUserPrincipal().getName());
            order.setUser(user);
        }

        if (securityContext.isUserInRole(ADMIN)) {
            user = userFinder.findByLogin(userLogin);
            order.setUser(user);
        }
    }

    private void checkNotNull(Order order) {
        if (order == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    private void assignOrderToOrderItems(Order order) {
        if (CollectionUtils.isEmpty(order.getItems()))
            return;
        order.getItems().forEach(orderItem -> {
            if (orderItem.getId() != null)
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            orderItem.setOrder(order);
        });
    }

    private void checkOrder(Order order) { // TODO Complete checks on OrderItems, checks skuId and discountId visibility. Check that user does not add too much discount.

        if (order.getId() != null || (order.getDeliveryAddress() != null && order.getDeliveryAddress().getId() != null)
                || (order.getBillingAddress() != null && order.getBillingAddress().getId() != null)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

    }

}
