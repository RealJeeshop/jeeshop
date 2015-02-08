package org.rembx.jeeshop.order.model;

import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.fest.assertions.Assertions.assertThat;


public class OrderTest {

    Order order;

    @Test
    public void testPrePersist() throws Exception {
        Order order = new Order();

        assertThat(order.getUpdateDate()).isNull();
        order.prePersist();
        assertThat(order.getUpdateDate()).isNotNull();
    }

    @Test
    public void testPreUpdate() throws Exception {
        Order order = new Order();

        assertThat(order.getCreationDate()).isNull();
        assertThat(order.getUpdateDate()).isNull();
        order.prePersist();
        assertThat(order.getCreationDate()).isNotNull();
        assertThat(order.getUpdateDate()).isNotNull();
    }

    @Test
    public void computeOrderReference_orderWithoutTransactionId() throws Exception {
        order = new Order();
        order.setId(1L);
        order.setCreationDate(new SimpleDateFormat("MMddyyyy").parse("11222222"));

        assertThat(order.getReference()).isNull();
        order.computeOrderReference();
        assertThat(order.getReference()).isEqualTo("11222222-1");
    }

    @Test
    public void computeOrderReference_orderWithTransactionId() throws Exception {
        order = new Order();
        order.setId(1L);
        order.setCreationDate(new SimpleDateFormat("MMddyyyy").parse("11222222"));
        order.setTransactionId("1234");
        order.computeOrderReference();

        assertThat(order.getReference()).isEqualTo("11222222-1-1234");
    }
}