package org.rembx.jeeshop.order.test;

import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderItem;
import org.rembx.jeeshop.order.model.OrderPersistenceUnit;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.user.model.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by remi on 30/11/14.
 */
public class TestOrder {

    private static TestOrder instance;

    private static Order order1;
    private static User user1;
    private static SKU sku1;
    private static SKU sku2;

    // Date are initialized with java.sql.Timestamp as JPA get a Timestamp instance
    private final static Date now = Timestamp.from(ZonedDateTime.now().toInstant());
    private final static Date yesterday = Timestamp.from(ZonedDateTime.now().minusDays(1).toInstant());

    public static TestOrder getInstance() {
        if (instance != null)
            return instance;

        EntityManager entityManager = Persistence.createEntityManagerFactory(OrderPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        Address deliveryAddress = new Address("21 Blue street", "Chicago", "78801", "FRA");
        Address billingAddress = new Address("53 Green street", "Chicago", "78801", "FRA");

        user1 = new User("test@test.com", "test", "John", "Doe", "+33616161616",null,yesterday,"fr_FR",null);
        user1.setGender("M.");

        entityManager.persist(deliveryAddress);
        entityManager.persist(billingAddress);
        entityManager.persist(user1);

        sku1 = new SKU("sku1", "Sku1 enabled", 10d, 100, "X1213JJLB-1", now, null, false, 3);
        sku2 = new SKU("sku2", "Sku2 enabled", 10d, 100, "X1213JJLB-2", now, null, false, 3);

        entityManager.persist(sku1);
        entityManager.persist(sku2);

        order1 = new Order(user1,null, deliveryAddress,billingAddress, OrderStatus.CREATED);

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

    public SKU firstSKU(){
        return sku1;
    }

    public SKU secondSKU(){
        return sku2;
    }

}
