package org.rembx.jeeshop.order.test;

import com.google.common.collect.Sets;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderItem;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.user.model.Address;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestUser;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Created by remi on 30/11/14.
 */
public class TestOrder {

    private static TestOrder instance;

    private static Order order1;
    private static Order order2;

    private static OrderItem orderItem1;
    private static TestUser testUser;

    public static TestOrder getInstance() {
        if (instance != null)
            return instance;

        testUser = TestUser.getInstance();

        EntityManager entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        Address deliveryAddress = new Address("21 Blue street", "Chicago", "78801", "John", "Doe", "M.", null, "FRA");
        Address billingAddress = new Address("53 Green street", "Chicago", "78801", "John", "Doe", "M.", null, "FRA");

        entityManager.persist(deliveryAddress);
        entityManager.persist(billingAddress);

        order1 = new Order(testUser.firstUser(), null, deliveryAddress, billingAddress, OrderStatus.PAYMENT_VALIDATED);
        order1.setStoreId(1L);
        orderItem1 = new OrderItem(1L, 1L, 2);
        orderItem1.setOrder(order1);

        order1.setItems(Sets.newHashSet(orderItem1));
        entityManager.persist(order1);

        order2 = new Order(testUser.firstUser(), null, null, null, OrderStatus.CREATED);
        order2.setStoreId(1L);
        entityManager.persist(order2);

        entityManager.getTransaction().commit();

        instance = new TestOrder();
        entityManager.close();
        return instance;
    }


    public Order firstOrder() {
        return order1;
    }

    public Order secondOrder() {
        return order2;
    }

    public User firstOrdersUser() {
        return order1.getCustomer();
    }

}
