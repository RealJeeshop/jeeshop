package org.rembx.jeeshop.order;

import io.quarkus.hibernate.orm.PersistenceUnit;
import org.apache.commons.collections.CollectionUtils;
import org.rembx.jeeshop.catalog.CatalogItemFinder;

import org.rembx.jeeshop.catalog.model.Store;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.user.MailTemplateFinder;
import org.rembx.jeeshop.user.UserFinder;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.rembx.jeeshop.role.JeeshopRoles.*;
import static org.rembx.jeeshop.catalog.model.QStore.store;

/**
 * Orders resource.
 */
@Path("/orders")
@ApplicationScoped
public class Orders {

    private OrderConfiguration orderConfiguration;
    private EntityManager entityManager;
    private OrderFinder orderFinder;
    private CatalogItemFinder catalogItemFinder;
    private UserFinder userFinder;
    private Mailer mailer;
    private MailTemplateFinder mailTemplateFinder;
    private PaymentTransactionEngine paymentTransactionEngine;
    private PriceEngine priceEngine;

    Orders(@PersistenceUnit(UserPersistenceUnit.NAME) EntityManager entityManager, OrderFinder orderFinder, UserFinder userFinder,
                  MailTemplateFinder mailTemplateFinder, Mailer mailer, PriceEngine priceEngine, PaymentTransactionEngine paymentTransactionEngine, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.orderFinder = orderFinder;
        this.userFinder = userFinder;
        this.mailTemplateFinder = mailTemplateFinder;
        this.mailer = mailer;
        this.priceEngine = priceEngine;
        this.paymentTransactionEngine = paymentTransactionEngine;
        this.catalogItemFinder = catalogItemFinder;
    }


    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY, USER})
    public Order find(@Context SecurityContext securityContext, @PathParam("orderId") @NotNull Long orderId, @QueryParam("enhanced") Boolean enhanced) {
        Order order = entityManager.find(Order.class, orderId);
        if (securityContext.isUserInRole(USER) && !securityContext.isUserInRole(ADMIN)) {
            User authenticatedUser = userFinder.findByLogin(securityContext.getUserPrincipal().getName());
            if (!order.getCustomer().equals(authenticatedUser)) {
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
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<Order> findAll(@Context SecurityContext securityContext, @QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size,
                               @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc, @QueryParam("status") OrderStatus status,
                               @QueryParam("skuId") Long skuId, @QueryParam("enhanced") Boolean enhanced) {

            return orderFinder.findAll(start, size, orderBy, isDesc, search, status, skuId, enhanced != null ? enhanced : false);
    }

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({STORE_ADMIN, USER})
    public List<Order> findManaged(@Context SecurityContext securityContext, @QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size,
                                   @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc, @QueryParam("status") OrderStatus status,
                                   @QueryParam("skuId") Long skuId, @QueryParam("enhanced") Boolean enhanced) {

        if (securityContext.isUserInRole(STORE_ADMIN)) {

            List<Store> stores = catalogItemFinder.findByOwner(store, securityContext.getUserPrincipal().getName());
            return stores.stream()
                    .map(s -> orderFinder.findByOwner(s, start, size, orderBy, isDesc, status))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

        } else if (securityContext.isUserInRole(USER)) {

            User user = userFinder.findByLogin(securityContext.getUserPrincipal().getName());
            return orderFinder.findByCustomer(user, start, size, orderBy, isDesc, status);

        } else return new ArrayList<>();
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

        order.setCustomer(existingOrder.getCustomer());

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
            order.setCustomer(user);
        }

        if (securityContext.isUserInRole(ADMIN)) {
            user = userFinder.findByLogin(userLogin);
            order.setCustomer(user);
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
