package org.rembx.jeeshop.order;

import org.apache.commons.collections.CollectionUtils;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.MailTemplateFinder;
import org.rembx.jeeshop.user.UserFinder;
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
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Orders resource.
 */
@Path("orders")
@Stateless
public class Orders {

    private final static Logger LOG = LoggerFactory.getLogger(Orders.class);

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private OrderFinder orderFinder;

    @Inject
    private UserFinder userFinder;

    @Inject
    private Mailer mailer;

    @Inject
    private MailTemplateFinder mailTemplateFinder;

    @Inject
    private PaymentTransactionEngine paymentTransactionEngine;

    @Inject
    private PriceEngine priceEngine;

    @Resource
    private SessionContext sessionContext;

    @Inject
    private OrderConfiguration orderConfiguration;

    public Orders() {
    }

    public Orders(EntityManager entityManager, OrderFinder orderFinder, UserFinder userFinder,
                  MailTemplateFinder mailTemplateFinder, Mailer mailer, SessionContext sessionContext, PriceEngine priceEngine, PaymentTransactionEngine paymentTransactionEngine) {
        this.entityManager = entityManager;
        this.orderFinder = orderFinder;
        this.userFinder = userFinder;
        this.mailTemplateFinder = mailTemplateFinder;
        this.mailer = mailer;
        this.sessionContext = sessionContext;
        this.priceEngine = priceEngine;
        this.paymentTransactionEngine = paymentTransactionEngine;
    }


    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Order find(@PathParam("orderId") @NotNull Long orderId,@QueryParam("enhanced") Boolean enhanced) {
        Order order = entityManager.find(Order.class, orderId);
        if (enhanced!=null && enhanced){
            orderFinder.enhanceOrder(order);
        }
        checkNotNull(order);

        return order;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<Order> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size,
                               @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc,@QueryParam("status") OrderStatus status,
                               @QueryParam("skuId") Long skuId, @QueryParam("enhanced") Boolean enhanced) {
        return orderFinder.findAll(start, size, orderBy, isDesc, search,status, skuId, enhanced != null? enhanced : false);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({JeeshopRoles.USER, JeeshopRoles.ADMIN})
    public Order create(Order order, @QueryParam("userLogin")String userLogin) {

        checkOrder(order);

        assignOrderToUser(order, userLogin);

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
    @RolesAllowed(JeeshopRoles.ADMIN)
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
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{orderId}")
    public void delete(@PathParam("orderId") Long orderId) {
        Order order = entityManager.find(Order.class, orderId);
        checkNotNull(order);
        entityManager.remove(order);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Long count(@QueryParam("search") String search, @QueryParam("status")OrderStatus status, @QueryParam("skuId") Long skuId) {
        return orderFinder.countAll(search,status, skuId);
    }

    @GET
    @Path("/fixeddeliveryfee")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Double getFixedDeliveryFee() {
        if (orderConfiguration!=null){
            return orderConfiguration.getFixedDeliveryFee();
        }
        return null;
    }



    private void assignOrderToUser(Order order, String userLogin) {
        User user;
        if (sessionContext.isCallerInRole(JeeshopRoles.USER)){
            user = userFinder.findByLogin(sessionContext.getCallerPrincipal().getName());
            order.setUser(user);
        }

        if (sessionContext.isCallerInRole(JeeshopRoles.ADMIN)){
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
            if (orderItem.getId()!=null)
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            orderItem.setOrder(order);
        });
    }

    private void checkOrder(Order order) { // TODO Complete checks on OrderItems, checks skuId and discountId visibility. Check that user does not add too much discount.

        if (order .getId() != null || (order.getDeliveryAddress() != null && order.getDeliveryAddress().getId() != null)
                || (order.getBillingAddress()!=null && order.getBillingAddress().getId() !=null)){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

    }

}
