package org.rembx.jeeshop.order;

import org.junit.Before;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderItem;

import javax.ejb.SessionContext;
import javax.persistence.EntityManager;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderPriceEngineImplTest {

    private EntityManager entityManager;
    private OrderConfiguration orderConfiguration;

    private OrderPriceEngineImpl orderPriceEngine;


    @Before
    public void setup(){
        entityManager =  mock(EntityManager.class);
        orderConfiguration =  mock(OrderConfiguration.class);

        orderPriceEngine = new OrderPriceEngineImpl(entityManager,orderConfiguration);
    }


    @Test
    public void computePrice_ShouldAddPriceOfEachOrderItemsMultipliedByQuantityWithFixedDeliveryFee() {
        SKU sku = new SKU();
        sku.setPrice(10.0);

        SKU sku2 = new SKU();
        sku2.setPrice(20.0);

        when(entityManager.find(SKU.class, 1L)).thenReturn(sku);
        when(entityManager.find(SKU.class, 2L)).thenReturn(sku2);
        when(orderConfiguration.getFixedDeliveryFee()).thenReturn(11.0);

        Order order = new Order();
        order.setItems(Arrays.asList(new OrderItem(1L,1), new OrderItem(2L,2)));

        Double price = orderPriceEngine.computePrice(order);

        verify(entityManager).find(SKU.class, 1L);
        verify(entityManager).find(SKU.class, 2L);

        verify(orderConfiguration).getFixedDeliveryFee();


        assertThat(price).isEqualTo(61.0);
    }


    @Test
    public void computePrice_ShouldAddPriceOfEachOrderItemsMultipliedByQuantity() {
        SKU sku = new SKU();
        sku.setPrice(10.0);

        SKU sku2 = new SKU();
        sku2.setPrice(20.0);

        when(entityManager.find(SKU.class, 1L)).thenReturn(sku);
        when(entityManager.find(SKU.class, 2L)).thenReturn(sku2);
        when(orderConfiguration.getFixedDeliveryFee()).thenReturn(null);

        Order order = new Order();
        order.setItems(Arrays.asList(new OrderItem(1L,1), new OrderItem(2L,2)));

        Double price = orderPriceEngine.computePrice(order);

        verify(entityManager).find(SKU.class, 1L);
        verify(entityManager).find(SKU.class, 2L);

        verify(orderConfiguration).getFixedDeliveryFee();


        assertThat(price).isEqualTo(50.0);
    }

    @Test
    public void computePrice_ShouldThrowEx_whenOrderHasNoItems() {

        Order order = new Order();

        try {
            Double price = orderPriceEngine.computePrice(order);
            fail("Should have thrown ex");
        }catch (IllegalStateException e){
        }
    }
}