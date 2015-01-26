package org.rembx.jeeshop.order;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderItem;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.order.model.SKUOrderItem;
import org.rembx.jeeshop.order.test.TestOrder;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.MailTemplateFinder;
import org.rembx.jeeshop.user.UserFinder;
import org.rembx.jeeshop.user.model.Address;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import sun.security.acl.PrincipalImpl;

import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.rembx.jeeshop.order.model.OrderStatus.CREATED;
import static org.rembx.jeeshop.order.model.OrderStatus.VALIDATED;

public class OrdersIT {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private TestOrder testOrder;
    private SessionContext sessionContextMock;
    private PriceEngine priceEngineMock;
    private Orders service;

    @BeforeClass
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME);
    }

    @Before
    public void setup() {
        testOrder = TestOrder.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        sessionContextMock = mock(SessionContext.class);
        priceEngineMock = mock(PriceEngine.class);

        service = new Orders(entityManager, new OrderFinder(entityManager), new UserFinder(entityManager),
                new MailTemplateFinder(entityManager), null  ,sessionContextMock, priceEngineMock);
    }

    @Test
    public void find() throws Exception {
        assertThat(service.find(1L)).isNotNull();
    }

    @Test
    public void find_withUnknownId_ShouldThrowException() throws Exception {
        try {
            service.find(999L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null, null, null,null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Order> orders = service.findAll(null, 0, 1, null, null,null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_ByLogin_shouldReturnSearchedOrder() {
        List<Order> orders = service.findAll(testOrder.firstOrder().getUser().getLogin(), 0, 1, null, null,null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void count() {
        assertThat(service.count(null,null)).isGreaterThan(0);
    }

    @Test
    public void count_withUnknownSearchCriteria() {
        assertThat(service.count("unknown",null)).isEqualTo(0);
    }

    @Test
    public void create_shouldThrowBadRequestWhenParametersHaveId() throws Exception {

        Address address = new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA");
        address.setId(777L);
        OrderItem orderItemWithId = new OrderItem();
        orderItemWithId.setId(777L);
        List<OrderItem> orderItems = Arrays.asList(orderItemWithId);

        try {
            Order order = new Order(null, address, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"));
            service.create(order, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        }

        try {
            Order order = new Order(null, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), address);
            service.create(order, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        }

        try {
            Order order = new Order(orderItems, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), address);
            service.create(order, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        }
    }

    @Test
    public void create_shouldPersistOrderAndItsAddressesInCascade_SetCurrentUserToOrderForUserRole() throws Exception {

        Address deliveryAddress = new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA");
        Address billingAddress = new Address("8 Rue Toto", "Paris", "75001", "John", "Doe", "M.", null, "FRA");

        when(sessionContextMock.isCallerInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.getCallerPrincipal()).thenReturn(new PrincipalImpl(testOrder.firstOrdersUser().getLogin()));

        entityManager.getTransaction().begin();
        Order order = new Order(null, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), new Address("8 Rue Toto", "Paris", "75001", "John", "Doe", "M.", null, "FRA"));

        service.create(order, null);
        entityManager.getTransaction().commit();

        verify(sessionContextMock).isCallerInRole(JeeshopRoles.USER);
        //verify(mailerMock).sendMail(testMailTemplate.orderConfirmationMailTemplate().getSubject(), testOrder.firstOrdersUser().getLogin(), "<html><body>Hello M. John Doe. Your order has been registered...</body></html>");

        final Order persistedOrder = entityManager.find(Order.class, order.getId());

        assertThat(persistedOrder).isNotNull();
        assertThat(persistedOrder.getStatus()).isEqualTo(CREATED);

        assertThat(persistedOrder.getUser()).isEqualTo(testOrder.firstOrdersUser());

        deliveryAddress.setId(persistedOrder.getDeliveryAddress().getId());
        billingAddress.setId(persistedOrder.getBillingAddress().getId());
        assertThat(persistedOrder.getBillingAddress()).isEqualTo(billingAddress);
        assertThat(persistedOrder.getDeliveryAddress()).isEqualTo(deliveryAddress);

        entityManager.remove(order);
    }

    @Test
    public void create_shouldPersistOrderAndItsOrderItems_AndComputeOrderPrice() throws Exception {

        List<OrderItem> orderItems = Arrays.asList(
                new SKUOrderItem(1L, 2),
                new SKUOrderItem(2L, 3)
        );


        when(sessionContextMock.isCallerInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.getCallerPrincipal()).thenReturn(new PrincipalImpl(testOrder.firstOrdersUser().getLogin()));

        entityManager.getTransaction().begin();
        Order order = new Order(orderItems, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"));
        when(priceEngineMock.computePrice(order)).thenReturn(79.0);

        service.create(order, null);
        entityManager.getTransaction().commit();


        verify(sessionContextMock).isCallerInRole(JeeshopRoles.USER);
        verify(priceEngineMock).computePrice(order);

        final Order persistedOrder = entityManager.find(Order.class, order.getId());

        assertThat(persistedOrder).isNotNull();
        assertThat(persistedOrder.getStatus()).isEqualTo(CREATED);

        assertThat(persistedOrder.getUser()).isEqualTo(testOrder.firstOrdersUser());

        SKUOrderItem expectedOrderItem1 = new SKUOrderItem(1L, 2);
        expectedOrderItem1.setSkuId(1L);
        expectedOrderItem1.setId(1L);
        SKUOrderItem expectedOrderItem2 = new SKUOrderItem(2L, 3);
        expectedOrderItem2.setSkuId(2L);
        expectedOrderItem2.setId(2L);

        assertThat(persistedOrder.getItems()).contains(expectedOrderItem1, expectedOrderItem2);
        assertThat(order.getPrice()).isEqualTo(79.0);

        entityManager.remove(order);
    }


    @Test
    public void create_shouldSetGivenUserByLoginInOrder_ForADMINRole() throws Exception {

        when(sessionContextMock.isCallerInRole(JeeshopRoles.USER)).thenReturn(false);
        when(sessionContextMock.isCallerInRole(JeeshopRoles.ADMIN)).thenReturn(true);

        entityManager.getTransaction().begin();
        Order order = new Order(null, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"));
        service.create(order, "test@test.com");
        entityManager.getTransaction().commit();

        verify(sessionContextMock).isCallerInRole(JeeshopRoles.USER);
        verify(sessionContextMock).isCallerInRole(JeeshopRoles.ADMIN);

        final Order persistedOrder = entityManager.find(Order.class, order.getId());

        assertThat(persistedOrder).isNotNull();
        assertThat(persistedOrder.getStatus()).isEqualTo(CREATED);

        assertThat(persistedOrder.getUser()).isEqualTo(testOrder.firstOrdersUser());

        entityManager.remove(order);
    }

    @Test
    public void delete_shouldRemove(){

        entityManager.getTransaction().begin();
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        entityManager.persist(order);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(order.getId());
        entityManager.getTransaction().commit();

        Assertions.assertThat(entityManager.find(Order.class, order.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx(){

        try {
            entityManager.getTransaction().begin();
            service.delete(666L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void modifyUnknownCatalog_ShouldThrowNotFoundException() {

        Order detachedOrder = new Order();
        detachedOrder.setId(9999L);
        try {
            service.modify(detachedOrder);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

}

