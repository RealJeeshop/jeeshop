package org.rembx.jeeshop.order;

import io.quarkus.undertow.runtime.HttpSessionContext;
import org.apache.http.auth.BasicUserPrincipal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.test.TestCatalog;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderItem;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.order.test.TestOrder;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.MailTemplateFinder;
import org.rembx.jeeshop.user.UserFinder;
import org.rembx.jeeshop.user.model.Address;

import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.rembx.jeeshop.order.model.OrderStatus.PAYMENT_VALIDATED;

public class OrdersCT {

    private static EntityManagerFactory emf;
    private static EntityManagerFactory catalogEmf;
    EntityManager entityManager;
    private EntityManager catalogEntityManager;
    private TestOrder testOrder;
    private TestCatalog testCatalog;


    private SecurityContext sessionContextMock;
    private PriceEngine priceEngineMock;
    private PaymentTransactionEngine paymentTransactionEngine;
    private Orders service;

    @BeforeAll
    public static void beforeClass() {
        emf = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME);
        catalogEmf = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);


    }

    @BeforeEach
    public void setup() {
        testOrder = TestOrder.getInstance();
        testCatalog = TestCatalog.getInstance();

        entityManager = emf.createEntityManager();
        catalogEntityManager = catalogEmf.createEntityManager();
        sessionContextMock = mock(SecurityContext.class);
        priceEngineMock = mock(PriceEngine.class);
        final MailTemplateFinder mailTemplateFinder = new MailTemplateFinder(entityManager);
        paymentTransactionEngine = new DefaultPaymentTransactionEngine(
                new OrderFinder(entityManager, catalogEntityManager, new OrderConfiguration("20", "3")),
                mailTemplateFinder, new Mailer(), catalogEntityManager);

        service = new Orders(entityManager, new OrderFinder(entityManager, catalogEntityManager, new OrderConfiguration("11.0", "20.0")), new UserFinder(entityManager),
                mailTemplateFinder, null, sessionContextMock, priceEngineMock, paymentTransactionEngine);
    }

    @Test
    public void find() throws Exception {
        assertThat(service.find(1L, null)).isEqualTo(testOrder.firstOrder());
    }

    @Test
    public void find_withEnhanceResult_shouldReturnEnhancedOrder() throws Exception {
        Order enhancedOrder = service.find(1L, true);

        assertThat(enhancedOrder).isEqualTo(testOrder.firstOrder());

        assertThatOrderIsEnhanced(enhancedOrder);
    }

    @Test
    public void find_withUnknownId_ShouldThrowException() throws Exception {
        try {
            service.find(999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void find_whenClientHasUserRoleAndOrderBelongsToAnotherUser_ShouldThrowException() throws Exception {
        entityManager.getTransaction().begin();
        User user = new User("777@test.com", "test", "M.", "John", "Doe", "+33616161616", null, null, "fr_FR", null);
        entityManager.persist(user);
        entityManager.getTransaction().commit();

        when(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(sessionContextMock.getUserPrincipal()).thenReturn(new BasicUserPrincipal("777@test.com"));
        try {
            service.find(1L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.UNAUTHORIZED);
        } finally {
            entityManager.getTransaction().begin();
            entityManager.remove(user);
            entityManager.persist(user);

        }
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null, null, null, null, null, null)).containsExactly(testOrder.firstOrder(), testOrder.secondOrder());
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Order> orders = service.findAll(null, 0, 1, null, null, null, null, null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_ByLogin_shouldReturnSearchedOrder() {
        List<Order> orders = service.findAll(testOrder.firstOrder().getUser().getLogin(), 0, 1, null, null, null, null, null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_ByStatus_shouldReturnOrdersWithMatchingStatus() {
        List<Order> orders = service.findAll(null, 0, 1, null, null, OrderStatus.PAYMENT_VALIDATED, null, null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_ByStatus_shouldReturnEmptyListWhenThereAreNoOrdersInGivenStatus() {
        List<Order> orders = service.findAll(null, 0, 1, null, null, OrderStatus.DELIVERED, null, null);
        assertThat(orders).isEmpty();
    }

    @Test
    public void findAll_ByStatus_shouldReturnEmptyListWhenThereAreNoOrdersWithItemsMatchingGivenSKUId() {
        List<Order> orders = service.findAll(null, 0, 1, null, null, null, 2L, null);
        assertThat(orders).isEmpty();
    }

    @Test
    public void findAll_withEnhancedResults_shouldReturnEnhancedOrders() {
        List<Order> orders = service.findAll(null, 0, 1, null, null, null, null, true);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
        assertThat(orders.get(0).getDeliveryFee()).isNotNull();

        assertThatOrderIsEnhanced(orders.get(0));
    }

    @Test
    public void findAll_BySkuId_shouldReturnOrdersWithItemsHavingGivenSkuIdAndStatus() {
        List<Order> orders = service.findAll(null, 0, 1, null, null, OrderStatus.PAYMENT_VALIDATED, 1L, null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_BySkuId_shouldReturnOrdersWithItemsHavingGivenSkuId() {
        List<Order> orders = service.findAll(null, 0, 1, null, null, null, 1L, null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_whenClientHasUserRoleOnly_shouldReturnNoneEmptyList_WithoutOrdersWithStatusCREATED() {

        when(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(sessionContextMock.getUserPrincipal()).thenReturn(new BasicUserPrincipal(testOrder.firstOrdersUser().getLogin()));

        assertThat(service.findAll(null, null, null, null, null, null, null, null)).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_whenClientHasUserRoleOnlyAndwithPagination_shouldReturnNoneEmptyListPaginated() {

        when(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(sessionContextMock.getUserPrincipal()).thenReturn(new BasicUserPrincipal(testOrder.firstOrdersUser().getLogin()));

        List<Order> orders = service.findAll(null, 0, 1, null, null, null, null, null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_whenClientHasUserRoleOnlyAndByLogin_shouldReturnSearchedOrder() {
        when(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(sessionContextMock.getUserPrincipal()).thenReturn(new BasicUserPrincipal(testOrder.firstOrdersUser().getLogin()));

        List<Order> orders = service.findAll(testOrder.firstOrder().getUser().getLogin(), 0, 1, null, null, null, null, null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_whenClientHasUserRoleOnlyAndByStatus_shouldReturnOrdersWithMatchingStatus() {
        when(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(sessionContextMock.getUserPrincipal()).thenReturn(new BasicUserPrincipal(testOrder.firstOrdersUser().getLogin()));

        List<Order> orders = service.findAll(null, 0, 1, null, null, OrderStatus.PAYMENT_VALIDATED, null, null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.firstOrder());
    }

    @Test
    public void findAll_whenClientHasUserRoleAndByStatusCREATED_shouldReturnOrdersWithThisStatus() {
        when(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(sessionContextMock.getUserPrincipal()).thenReturn(new BasicUserPrincipal(testOrder.firstOrdersUser().getLogin()));

        List<Order> orders = service.findAll(null, 0, 1, null, null, OrderStatus.CREATED, null, null);
        assertThat(orders).isNotEmpty();
        assertThat(orders).containsExactly(testOrder.secondOrder());
    }

    @Test
    public void count() {
        assertThat(service.count(null, null, null)).isGreaterThan(0);
    }

    @Test
    public void count_withUnknownSearchCriteria() {
        assertThat(service.count("unknown", null, null)).isEqualTo(0);
    }

    @Test
    public void create_shouldThrowBadRequestWhenParametersHaveId() throws Exception {

        Address address = new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA");
        address.setId(777L);
        OrderItem orderItemWithId = new OrderItem();
        orderItemWithId.setId(777L);
        Set<OrderItem> orderItems = Collections.singleton(orderItemWithId);

        try {
            Order order = new Order(null, address, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"));
            service.create(order, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        }

        try {
            Order order = new Order(null, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), address);
            service.create(order, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        }

        try {
            Order order = new Order(orderItems, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), address);
            service.create(order, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        }
    }

    @Test
    public void create_shouldPersistOrderAndItsAddressesInCascade_SetCurrentUserToOrderForUserRole_AndProcessPayment() throws Exception {

        Address deliveryAddress = new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA");
        Address billingAddress = new Address("8 Rue Toto", "Paris", "75001", "John", "Doe", "M.", null, "FRA");

        when(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.getUserPrincipal()).thenReturn(new BasicUserPrincipal(testOrder.firstOrdersUser().getLogin()));

        entityManager.getTransaction().begin();
        Order order = new Order(new HashSet<>(), new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), new Address("8 Rue Toto", "Paris", "75001", "John", "Doe", "M.", null, "FRA"));

        order.setOrderDiscounts(new HashSet<>());

        service.create(order, null);
        entityManager.getTransaction().commit();

        verify(sessionContextMock).isUserInRole(JeeshopRoles.USER);
        //verify(mailerMock).sendMail(testMailTemplate.orderConfirmationMailTemplate().getSubject(), testOrder.firstOrdersUser().getLogin(), "<html><body>Hello M. John Doe. Your order has been registered...</body></html>");

        final Order persistedOrder = entityManager.find(Order.class, order.getId());

        assertThat(persistedOrder).isNotNull();
        assertThat(persistedOrder.getStatus()).isEqualTo(PAYMENT_VALIDATED);

        assertThat(persistedOrder.getUser()).isEqualTo(testOrder.firstOrdersUser());

        deliveryAddress.setId(persistedOrder.getDeliveryAddress().getId());
        billingAddress.setId(persistedOrder.getBillingAddress().getId());
        assertThat(persistedOrder.getBillingAddress()).isEqualTo(billingAddress);
        assertThat(persistedOrder.getDeliveryAddress()).isEqualTo(deliveryAddress);

        entityManager.getTransaction().begin();
        entityManager.remove(order);
        entityManager.getTransaction().commit();

    }

    @Test
    public void create_shouldPersistOrderWithOrderItems_computePrice_andProcessPayment() throws Exception {

        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(new OrderItem(1L, 1L, 2));
        orderItems.add(new OrderItem(2L, 2L, 3));


        when(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true);
        when(sessionContextMock.getUserPrincipal()).thenReturn(new BasicUserPrincipal(testOrder.firstOrdersUser().getLogin()));

        entityManager.getTransaction().begin();
        Order order = new Order(orderItems, new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"));
        order.setOrderDiscounts(new HashSet<>());

        service.create(order, null);
        entityManager.getTransaction().commit();


        verify(sessionContextMock).isUserInRole(JeeshopRoles.USER);
        verify(priceEngineMock).computePrice(order);

        final Order persistedOrder = entityManager.find(Order.class, order.getId());

        assertThat(persistedOrder).isNotNull();
        assertThat(persistedOrder.getStatus()).isEqualTo(PAYMENT_VALIDATED);

        assertThat(persistedOrder.getUser()).isEqualTo(testOrder.firstOrdersUser());

        assertThat(persistedOrder.getItems()).hasSize(2);

        entityManager.getTransaction().begin();
        entityManager.remove(order);
        entityManager.getTransaction().commit();

    }

    @Test
    public void create_shouldSetGivenUserByLoginInOrder_ForADMINRole() throws Exception {

        when(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(false);
        when(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(true);

        entityManager.getTransaction().begin();
        Order order = new Order(new HashSet<>(), new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"), new Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "USA"));
        order.setOrderDiscounts(new HashSet<>());

        service.create(order, "test@test.com");
        entityManager.getTransaction().commit();

        verify(sessionContextMock).isUserInRole(JeeshopRoles.USER);
        verify(sessionContextMock).isUserInRole(JeeshopRoles.ADMIN);

        final Order persistedOrder = entityManager.find(Order.class, order.getId());

        assertThat(persistedOrder).isNotNull();
        assertThat(persistedOrder.getStatus()).isEqualTo(PAYMENT_VALIDATED);

        assertThat(persistedOrder.getUser()).isEqualTo(testOrder.firstOrdersUser());

        entityManager.getTransaction().begin();
        entityManager.remove(order);
        entityManager.getTransaction().commit();

    }


    @Test
    public void delete_shouldRemove() {

        entityManager.getTransaction().begin();
        Order order = new Order();
        order.setStatus(OrderStatus.VALIDATED);
        entityManager.persist(order);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(order.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Order.class, order.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx() {

        try {
            entityManager.getTransaction().begin();
            service.delete(666L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void modifyUnknownCatalog_ShouldThrowNotFoundException() {

        Order detachedOrder = new Order();
        detachedOrder.setId(9999L);
        try {
            service.modify(detachedOrder);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    private void assertThatOrderIsEnhanced(Order order) {

        assertThat(order.getDeliveryFee()).isEqualTo(11.0);
        assertThat(order.getVat()).isEqualTo(20.0);

        order.getItems().forEach(orderItem -> {
                    assertThat(orderItem.getDisplayName()).isNotNull();
                    assertThat(orderItem.getSkuReference()).isNotNull();

                }
        );

        order.getOrderDiscounts().forEach(orderDiscount -> {
                    assertThat(orderDiscount.getDisplayName()).isNotNull();
                    assertThat(orderDiscount.getRateType()).isNotNull();
                }
        );
    }

}

