package org.rembx.jeeshop.order.test;

import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.user.model.Address;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestUser;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by remi on 30/11/14.
 */
public class TestOrder {

    private static TestOrder instance;

    private static Order order1;
    private static TestUser testUser;

    // Date are initialized with java.sql.Timestamp as JPA get a Timestamp instance
    private final static Date now = Timestamp.from(ZonedDateTime.now().toInstant());
    private final static Date yesterday = Timestamp.from(ZonedDateTime.now().minusDays(1).toInstant());

    public static TestOrder getInstance() {
        if (instance != null)
            return instance;

        testUser = TestUser.getInstance();

        EntityManager entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        Address deliveryAddress = new Address("21 Blue street", "Chicago", "78801", "FRA");
        Address billingAddress = new Address("53 Green street", "Chicago", "78801", "FRA");

        entityManager.persist(deliveryAddress);
        entityManager.persist(billingAddress);

        order1 = new Order(testUser.firstUser(),null, deliveryAddress,billingAddress, OrderStatus.CREATED);

        entityManager.persist(order1);

        entityManager.getTransaction().commit();

        instance = new TestOrder();
        entityManager.close();
        return instance;
    }


    public Order firstOrder() {
        return order1;
    }

    public User firstOrdersUser() {
        return order1.getUser();
    }

}
